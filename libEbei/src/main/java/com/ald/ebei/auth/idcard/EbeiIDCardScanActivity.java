package com.ald.ebei.auth.idcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.auth.idcard.util.EbeiDialogUtil;
import com.ald.ebei.auth.idcard.util.EbeiICamera;
import com.ald.ebei.auth.idcard.util.EbeiRotaterUtil;
import com.ald.ebei.auth.idcard.util.EbeiUtil;
import com.ald.ebei.auth.idcard.view.EbeiIDCardIndicator;
import com.ald.ebei.auth.idcard.view.EbeiIDCardNewIndicator;
import com.megvii.idcardquality.IDCardQualityAssessment;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class EbeiIDCardScanActivity extends Activity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    private TextureView textureView;
    private EbeiDialogUtil mEbeiDialogUtil;
    private EbeiICamera mEbeiICamera;// 照相机工具类
    private IDCardQualityAssessment idCardQualityAssessment = null;
    private EbeiIDCardNewIndicator mNewIndicatorView;
    private EbeiIDCardIndicator mEbeiIdCardIndicator;
    private IDCardAttr.IDCardSide mSide;
    private DecodeThread mDecoder = null;
    private boolean mIsVertical = false;
    private TextView fps;
    private TextView errorType;
    private TextView horizontalTitle, verticalTitle;
    private TextView logInfo;
    private View debugRectangle;
    private boolean isDebugMode = false;
    int continuousClickCount = 0;
    long lastClickMillis = 0;
    private Vibrator vibrator;
    private float mInBound, mIsIDCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idcardscan_layout);
        init();
        initData();
    }

    private void init() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSide = getIntent().getIntExtra("side", 0) == 0 ? IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT
                : IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
        mIsVertical = getIntent().getBooleanExtra("isvertical", false);
        mEbeiICamera = new EbeiICamera(mIsVertical);
        mEbeiDialogUtil = new EbeiDialogUtil(this);
        textureView = (TextureView) findViewById(R.id.idcardscan_layout_surface);
        textureView.setSurfaceTextureListener(this);
        textureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEbeiICamera.autoFocus();
            }
        });
        fps = (TextView) findViewById(R.id.idcardscan_layout_fps);
        logInfo = (TextView) findViewById(R.id.text_debug_info);
        errorType = (TextView) findViewById(R.id.idcardscan_layout_error_type);
        horizontalTitle = (TextView) findViewById(R.id.idcardscan_layout_horizontalTitle);
        verticalTitle = (TextView) findViewById(R.id.idcardscan_layout_verticalTitle);
        mFrameDataQueue = new LinkedBlockingDeque<byte[]>(1);
        mNewIndicatorView = (EbeiIDCardNewIndicator) findViewById(R.id.idcardscan_layout_newIndicator);
        mEbeiIdCardIndicator = (EbeiIDCardIndicator) findViewById(R.id.idcardscan_layout_indicator);
        debugRectangle = findViewById(R.id.debugRectangle);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEbeiICamera.autoFocus();
                launchDebugMode();
            }
        };
        mNewIndicatorView.setOnClickListener(onClickListener);
        mEbeiIdCardIndicator.setOnClickListener(onClickListener);

        if (mIsVertical) {
            horizontalTitle.setVisibility(View.GONE);
            verticalTitle.setVisibility(View.VISIBLE);
            mEbeiIdCardIndicator.setVisibility(View.VISIBLE);
            mNewIndicatorView.setVisibility(View.GONE);
            mEbeiIdCardIndicator.setCardSideAndOrientation(mIsVertical, mSide);
            mNewIndicatorView.setCardSideAndOrientation(mIsVertical, mSide);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            horizontalTitle.setVisibility(View.VISIBLE);
            verticalTitle.setVisibility(View.GONE);
            mEbeiIdCardIndicator.setVisibility(View.GONE);
            mNewIndicatorView.setVisibility(View.VISIBLE);
            mEbeiIdCardIndicator.setCardSideAndOrientation(mIsVertical, mSide);
            mNewIndicatorView.setCardSideAndOrientation(mIsVertical, mSide);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (mDecoder == null) {
            mDecoder = new DecodeThread();
        }
        if (!mDecoder.isAlive()) {
            mDecoder.start();
        }
    }

    private void launchDebugMode() {
        long currentMillis = System.currentTimeMillis();
        if (continuousClickCount == 0 || (continuousClickCount > 0 && currentMillis - lastClickMillis < 200)) {
            continuousClickCount++;
        }
        lastClickMillis = currentMillis;
        if (continuousClickCount == 6) {
            isDebugMode = true;
            continuousClickCount = 0;
        }
    }

    private void setDebugRectanglePosition() {

        Rect debugRoi;
        if (!mIsVertical) {
            debugRoi = mNewIndicatorView.getMargin();
        } else {
            debugRoi = mEbeiIdCardIndicator.getMargin();
        }
        final Rect fDebugRect = debugRoi;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) debugRectangle.getLayoutParams();
        params.setMargins(fDebugRect.left, fDebugRect.top, fDebugRect.right, fDebugRect.bottom);
        debugRectangle.setLayoutParams(params);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        idCardQualityAssessment = new IDCardQualityAssessment.Builder().setIsIgnoreShadow(true).setIsIgnoreHighlight(false).build();
        boolean initSuccess = idCardQualityAssessment.init(this, EbeiUtil.readModel(this));
        if (!initSuccess) {
            mEbeiDialogUtil.showDialog("检测器初始化失败");
        } else {
            mInBound = idCardQualityAssessment.mInBound;
            mIsIDCard = idCardQualityAssessment.mIsIdcard;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEbeiDialogUtil.onDestory();
        try {
            if (mDecoder != null) {
                mDecoder.interrupt();
                mDecoder.join();
                mDecoder = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idCardQualityAssessment.release();
        idCardQualityAssessment = null;
    }

    private void doPreview() {
        if (!mHasSurface)
            return;

        mEbeiICamera.startPreview(textureView.getSurfaceTexture());
        setDebugRectanglePosition();
    }

    private boolean mHasSurface = false;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Camera mCamera = mEbeiICamera.openCamera(this);
        if (mCamera != null) {
            RelativeLayout.LayoutParams layout_params = mEbeiICamera.getLayoutParam(this);
            textureView.setLayoutParams(layout_params);
            mNewIndicatorView.setLayoutParams(layout_params);
            mEbeiIdCardIndicator.setLayoutParams(layout_params);
        } else {
            mEbeiDialogUtil.showDialog("打开摄像头失败");
            return;
        }
        mHasSurface = true;
        doPreview();
        mEbeiICamera.actionDetect(this);

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mEbeiICamera.closeCamera();
        mHasSurface = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        mFrameDataQueue.offer(data);
    }

    private BlockingQueue<byte[]> mFrameDataQueue;

    private class DecodeThread extends Thread {
        boolean mHasSuccess = false;
        int mCount = 0;
        int mTimSum = 0;
        private IDCardQualityResult.IDCardFailedType mLstErrType;

        @Override
        public void run() {
            byte[] imgData = null;
            try {
                while ((imgData = mFrameDataQueue.take()) != null) {
                    if (mHasSuccess)
                        return;
                    int imageWidth = mEbeiICamera.cameraWidth;
                    int imageHeight = mEbeiICamera.cameraHeight;

                    imgData = EbeiRotaterUtil.rotate(imgData, imageWidth, imageHeight,
                            mEbeiICamera.getCameraAngle(EbeiIDCardScanActivity.this));
                    if (mIsVertical) {
                        imageWidth = mEbeiICamera.cameraHeight;
                        imageHeight = mEbeiICamera.cameraWidth;
                    }
                    long start = System.currentTimeMillis();
                    RectF rectF;
                    if (!mIsVertical) {
                        rectF = mNewIndicatorView.getPosition();
                    } else {
                        rectF = mEbeiIdCardIndicator.getPosition();
                    }
                    Rect roi = new Rect();
                    roi.left = (int) (rectF.left * imageWidth);
                    roi.top = (int) (rectF.top * imageHeight);
                    roi.right = (int) (rectF.right * imageWidth);
                    roi.bottom = (int) (rectF.bottom * imageHeight);

                    if (!isEven01(roi.left))
                        roi.left = roi.left + 1;
                    if (!isEven01(roi.top))
                        roi.top = roi.top + 1;
                    if (!isEven01(roi.right))
                        roi.right = roi.right - 1;
                    if (!isEven01(roi.bottom))
                        roi.bottom = roi.bottom - 1;

                    final IDCardQualityResult result = idCardQualityAssessment.getQuality(imgData, imageWidth,
                            imageHeight, mSide, roi);

                    long end = System.currentTimeMillis();

                    final long perFrameMillis = end - start;
                    mCount++;
                    mTimSum += (end - start);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isDebugMode) {
                                if (result != null && result.attr != null) {
                                    String debugResult = "clear: " + new BigDecimal(result.attr.lowQuality).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                            + "in_bound: " + new BigDecimal(result.attr.inBound).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                            + "is_idcard: " + new BigDecimal(result.attr.isIdcard).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                            + "flare: " + result.attr.specularHightlightCount + "\n"
                                            + "shadow: " + result.attr.shadowCount + "\n"
                                            + "millis: " + perFrameMillis;
                                    logInfo.setText(debugResult);
                                }
                                debugRectangle.setVisibility(View.VISIBLE);
                            } else {
                                logInfo.setText("");
                                debugRectangle.setVisibility(View.GONE);
                            }
                        }
                    });

                    if (result != null) {
                        if (result.attr != null) {
                            float inBound = result.attr.inBound;
                            float isIDCard = result.attr.isIdcard;
                            if (isIDCard > mIsIDCard && inBound > 0)
                                if (!mIsVertical)
                                    mNewIndicatorView.setBackColor(EbeiIDCardScanActivity.this, 0xaa000000);
                                else
                                    mEbeiIdCardIndicator.setBackColor(EbeiIDCardScanActivity.this, 0xaa000000);
                            else if (!mIsVertical)
                                mNewIndicatorView.setBackColor(EbeiIDCardScanActivity.this, 0x00000000);
                            else
                                mEbeiIdCardIndicator.setBackColor(EbeiIDCardScanActivity.this, 0x00000000);
                        }
                        if (result.isValid()) {
                            vibrator.vibrate(new long[]{0, 50, 50, 100, 50}, -1);
                            mHasSuccess = true;
                            handleSuccess(result);
                            return;
                        } else {
                            if (!mIsVertical)
                                mNewIndicatorView.setBackColor(EbeiIDCardScanActivity.this, 0x00000000);
                            else
                                mEbeiIdCardIndicator.setBackColor(EbeiIDCardScanActivity.this, 0x00000000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<IDCardQualityResult.IDCardFailedType> failTypes = (result == null) ? null : result.fails;
                                    if (failTypes != null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        IDCardQualityResult.IDCardFailedType errType = failTypes.get(0);
                                        if (mIsVertical)
                                            verticalTitle.setText(EbeiUtil.errorType2HumanStr(failTypes.get(0), mSide));
                                        else
                                            horizontalTitle.setText(EbeiUtil.errorType2HumanStr(failTypes.get(0), mSide));
                                        mLstErrType = errType;
                                        errorType.setText(stringBuilder.toString());
                                    } else {
                                        verticalTitle.setText("");
                                        horizontalTitle.setText("");
                                    }
                                    if (mCount != 0 && mTimSum != 0)
                                        fps.setText((1000 * mCount / mTimSum) + " FPS");
                                }
                            });
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void handleSuccess(IDCardQualityResult result) {
            Intent intent = new Intent();
            intent.putExtra("side", mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1);
            intent.putExtra("idcardImg", EbeiUtil.bmp2byteArr(result.croppedImageOfIDCard()));
            if (result.attr.side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
                intent.putExtra("portraitImg", EbeiUtil.bmp2byteArr(result.croppedImageOfPortrait()));
            }
            setResult(RESULT_OK, intent);
            EbeiUtil.cancleToast(EbeiIDCardScanActivity.this);
            finish();
        }
    }

    public static void startMe(Context context, IDCardAttr.IDCardSide side) {
        if (side == null || context == null)
            return;
        Intent intent = new Intent(context, EbeiIDCardScanActivity.class);
        intent.putExtra("side", side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1);
        context.startActivity(intent);
    }

    // 用取余运算
    public boolean isEven01(int num) {
        if (num % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }
}