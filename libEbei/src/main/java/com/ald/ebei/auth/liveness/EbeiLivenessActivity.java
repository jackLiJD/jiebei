package com.ald.ebei.auth.liveness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.auth.liveness.util.EbeiConUtil;
import com.ald.ebei.auth.liveness.util.EbeiDialogUtil;
import com.ald.ebei.auth.liveness.util.EbeiICamera;
import com.ald.ebei.auth.liveness.util.EbeiIDetection;
import com.ald.ebei.auth.liveness.util.EbeiIMediaPlayer;
import com.ald.ebei.auth.liveness.util.EbeiScreen;
import com.ald.ebei.auth.liveness.util.EbeiSensorUtil;
import com.ald.ebei.auth.liveness.view.EbeiCircleProgressBar;
import com.megvii.livenessdetection.DetectionConfig;
import com.megvii.livenessdetection.DetectionFrame;
import com.megvii.livenessdetection.Detector;
import com.megvii.livenessdetection.Detector.DetectionFailedType;
import com.megvii.livenessdetection.Detector.DetectionListener;
import com.megvii.livenessdetection.Detector.DetectionType;
import com.megvii.livenessdetection.FaceQualityManager;
import com.megvii.livenessdetection.FaceQualityManager.FaceQualityErrorType;
import com.megvii.livenessdetection.bean.FaceIDDataStruct;
import com.megvii.livenessdetection.bean.FaceInfo;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class EbeiLivenessActivity extends Activity
        implements PreviewCallback, DetectionListener, TextureView.SurfaceTextureListener {

    private TextureView camerapreview;
    private EbeiFaceMask mEbeiFaceMask;// 画脸位置的类（调试时会用到）
    private ProgressBar mProgressBar;// 网络上传请求验证时出现的ProgressBar
    private LinearLayout headViewLinear;// "请在光线充足的情况下进行检测"这个视图
    private RelativeLayout rootView;// 根视图
    private TextView timeOutText;
    private RelativeLayout timeOutRel;
    private EbeiCircleProgressBar mEbeiCircleProgressBar;
    private Detector mDetector;// 活体检测器
    private EbeiICamera mEbeiICamera;// 照相机工具类
    private Handler mainHandler;
    private HandlerThread mHandlerThread = new HandlerThread("videoEncoder");
    private Handler mHandler;
    private JSONObject jsonObject;
    private EbeiIMediaPlayer mEbeiIMediaPlayer;// 多媒体工具类
    private EbeiIDetection mEbeiIDetection;
    private EbeiDialogUtil mEbeiDialogUtil;

    private TextView promptText;
    private boolean isHandleStart;// 是否开始检测
    private FaceQualityManager mFaceQualityManager;
    private EbeiSensorUtil ebeiSensorUtil;
    private int mFailFrame = 0;
    private int mCurStep = 0;// 检测动作的次数
    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            // 倒计时开始
            initDetecteSession();
            if (mEbeiIDetection.mDetectionSteps != null)
                changeType(mEbeiIDetection.mDetectionSteps.get(0), 10);
        }
    };
    private boolean mHasSurface = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liveness_layout);
        init();
        initData();
    }

    private void init() {
        ebeiSensorUtil = new EbeiSensorUtil(this);
        EbeiScreen.initialize(this);
        mainHandler = new Handler();
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mEbeiIMediaPlayer = new EbeiIMediaPlayer(this);
        mEbeiDialogUtil = new EbeiDialogUtil(this);
        rootView = (RelativeLayout) findViewById(R.id.liveness_layout_rootRel);
        mEbeiIDetection = new EbeiIDetection(this, rootView);
        mEbeiFaceMask = (EbeiFaceMask) findViewById(R.id.liveness_layout_facemask);
        mEbeiICamera = new EbeiICamera();
        promptText = (TextView) findViewById(R.id.liveness_layout_promptText);
        camerapreview = (TextureView) findViewById(R.id.liveness_layout_textureview);
        camerapreview.setSurfaceTextureListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.liveness_layout_progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);
        headViewLinear = (LinearLayout) findViewById(R.id.liveness_layout_bottom_tips_head);
        headViewLinear.setVisibility(View.VISIBLE);
        timeOutRel = (RelativeLayout) findViewById(R.id.detection_step_timeoutRel);
        timeOutText = (TextView) findViewById(R.id.detection_step_timeout_garden);
        mEbeiCircleProgressBar = (EbeiCircleProgressBar) findViewById(R.id.detection_step_timeout_progressBar);
        mEbeiIDetection.viewsInit();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化活体检测器
        DetectionConfig config = new DetectionConfig.Builder().build();
        mDetector = new Detector(this, config);
        boolean initSuccess = mDetector.init(this, EbeiConUtil.readModel(this), "");
        if (!initSuccess) {
            mEbeiDialogUtil.showDialog(getString(R.string.meglive_detect_initfailed));
        }

        // 初始化动画
        new Thread(new Runnable() {
            @Override
            public void run() {
                mEbeiIDetection.animationInit();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isHandleStart = false;
        // 打开照相机
        boolean isHaveFront = EbeiICamera.hasFrontFacingCamera();
        if (!isHaveFront) {
            mEbeiICamera.cameraId = 0;
        }
        Camera mCamera = mEbeiICamera.openCamera(this);
        if (mCamera != null) {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(mEbeiICamera.cameraId, cameraInfo);
            mEbeiFaceMask.setFrontal(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
            // 获取到相机分辨率对应的显示大小，并把这个值复制给camerapreview
            RelativeLayout.LayoutParams layout_params = mEbeiICamera.getLayoutParam();
            camerapreview.setLayoutParams(layout_params);
            mEbeiFaceMask.setLayoutParams(layout_params);
            // 初始化人脸质量检测管理类
            mFaceQualityManager = new FaceQualityManager(1 - 0.5f, 0.5f);
            mEbeiIDetection.mCurShowIndex = -1;
        } else {
            mEbeiDialogUtil.showDialog(getString(R.string.meglive_camera_initfailed));
        }
    }

    /**
     * 开始检测
     */
    private void handleStart() {
        if (isHandleStart)
            return;
        isHandleStart = true;
        // 开始动画
        Animation animationIN = AnimationUtils.loadAnimation(EbeiLivenessActivity.this, R.anim.liveness_rightin);
        Animation animationOut = AnimationUtils.loadAnimation(EbeiLivenessActivity.this, R.anim.liveness_leftout);
        headViewLinear.startAnimation(animationOut);
        mEbeiIDetection.mAnimViews[0].setVisibility(View.VISIBLE);
        mEbeiIDetection.mAnimViews[0].startAnimation(animationIN);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timeOutRel.setVisibility(View.VISIBLE);
            }
        });
        // 开始活体检测
        mainHandler.post(mTimeoutRunnable);
    }

    private void initDetecteSession() {
        if (mEbeiICamera.mCamera == null)
            return;

        mProgressBar.setVisibility(View.INVISIBLE);
        mEbeiIDetection.detectionTypeInit();

        mCurStep = 0;
        mDetector.reset();
        mDetector.changeDetectionType(mEbeiIDetection.mDetectionSteps.get(0));
    }

    /**
     * 照相机预览数据回调 （PreviewCallback的接口回调方法）
     */
    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        Size previewsize = camera.getParameters().getPreviewSize();
        // 活体检测器检测
        if (EbeiICamera.hasFrontFacingCamera()) {
            mDetector.doDetection(data, previewsize.width, previewsize.height, 360 - mEbeiICamera.getCameraAngle(this));
        } else {
            mDetector.doDetection(data, previewsize.width, previewsize.height, 180 - mEbeiICamera.getCameraAngle(this));
        }
    }

    /**
     * 活体验证成功 （DetectionListener的接口回调方法）
     */
    @Override
    public DetectionType onDetectionSuccess(final DetectionFrame validFrame) {
        mEbeiIMediaPlayer.reset();
        mCurStep++;
        mEbeiFaceMask.setFaceInfo(null);

        if (mCurStep == mEbeiIDetection.mDetectionSteps.size()) {
            mProgressBar.setVisibility(View.VISIBLE);
            handleResult(R.string.verify_success);
        } else
            changeType(mEbeiIDetection.mDetectionSteps.get(mCurStep), 10);

        // 检测器返回值：如果不希望检测器检测则返回DetectionType.DONE，如果希望检测器检测动作则返回要检测的动作
        return mCurStep >= mEbeiIDetection.mDetectionSteps.size() ? DetectionType.DONE
                : mEbeiIDetection.mDetectionSteps.get(mCurStep);
    }

    /**
     * 活体检测失败 （DetectionListener的接口回调方法）
     */
    @Override
    public void onDetectionFailed(final DetectionFailedType type) {
        Log.e("TAG", "" + type);
        int resourceID = R.string.liveness_detection_failed;
        switch (type) {
            case ACTIONBLEND:
                resourceID = R.string.liveness_detection_failed_action_blend;
                break;
            case NOTVIDEO:
                resourceID = R.string.liveness_detection_failed_not_video;
                break;
            case TIMEOUT:
                resourceID = R.string.liveness_detection_failed_timeout;
                break;
        }
        handleResult(resourceID);
    }

    /**
     * 活体验证中（这个方法会持续不断的回调，返回照片detection信息） （DetectionListener的接口回调方法）
     */
    @Override
    public void onFrameDetected(long timeout, DetectionFrame detectionFrame) {
        if (ebeiSensorUtil.isVertical()) {
            faceOcclusion(detectionFrame);
            handleNotPass(timeout);
            mEbeiFaceMask.setFaceInfo(detectionFrame);
        } else {
            if (ebeiSensorUtil.Y == 0)
                promptText.setText(R.string.meglive_getpermission_motion);
            else
                promptText.setText(R.string.meglive_phone_vertical);
        }
    }

    /**
     * 照镜子环节
     * 流程：1,先从返回的DetectionFrame中获取FaceInfo。在FaceInfo中可以先判断这张照片上的人脸是否有被遮挡的状况
     * ，入股有直接return
     * 2,如果没有遮挡就把SDK返回的DetectionFramed传入人脸质量检测管理类mFaceQualityManager中获取FaceQualityErrorType的list
     * 3.通过返回的list来判断这张照片上的人脸是否合格。
     * 如果返回list为空或list中FaceQualityErrorType的对象数量为0则表示这张照片合格开始进行活体检测
     */
    private void faceOcclusion(DetectionFrame detectionFrame) {
        mFailFrame++;
        if (detectionFrame != null) {
            FaceInfo faceInfo = detectionFrame.getFaceInfo();
            if (faceInfo != null) {
                if (faceInfo.eyeLeftOcclusion > 0.5 || faceInfo.eyeRightOcclusion > 0.5) {
                    if (mFailFrame > 10) {
                        mFailFrame = 0;
                        promptText.setText(R.string.meglive_keep_eyes_open);
                    }
                    return;
                }
                if (faceInfo.mouthOcclusion > 0.5) {
                    if (mFailFrame > 10) {
                        mFailFrame = 0;
                        promptText.setText(R.string.meglive_keep_mouth_open);
                    }
                    return;
                }
                boolean faceTooLarge = faceInfo.faceTooLarge;
                mEbeiIDetection.checkFaceTooLarge(faceTooLarge);
            }
        }
        // 从人脸质量检测管理类中获取错误类型list
        faceInfoChecker(mFaceQualityManager.feedFrame(detectionFrame));
    }

    public void faceInfoChecker(List<FaceQualityErrorType> errorTypeList) {
        if (errorTypeList == null || errorTypeList.size() == 0)
            handleStart();
        else {
            String infoStr = "";
            FaceQualityErrorType errorType = errorTypeList.get(0);
            if (errorType == FaceQualityErrorType.FACE_NOT_FOUND) {
                infoStr = getString(R.string.face_not_found);
            } else if (errorType == FaceQualityErrorType.FACE_POS_DEVIATED) {
                infoStr = getString(R.string.face_not_found);
            } else if (errorType == FaceQualityErrorType.FACE_NONINTEGRITY) {
                infoStr = getString(R.string.face_not_found);
            } else if (errorType == FaceQualityErrorType.FACE_TOO_DARK) {
                infoStr = getString(R.string.face_too_dark);
            } else if (errorType == FaceQualityErrorType.FACE_TOO_BRIGHT) {
                infoStr = getString(R.string.face_too_bright);
            } else if (errorType == FaceQualityErrorType.FACE_TOO_SMALL) {
                infoStr = getString(R.string.face_too_small);
            } else if (errorType == FaceQualityErrorType.FACE_TOO_LARGE) {
                infoStr = getString(R.string.face_too_large);
            } else if (errorType == FaceQualityErrorType.FACE_TOO_BLURRY) {
                infoStr = getString(R.string.face_too_blurry);
            } else if (errorType == FaceQualityErrorType.FACE_OUT_OF_RECT) {
                infoStr = getString(R.string.face_out_of_rect);
            }

            if (mFailFrame > 10) {
                mFailFrame = 0;
                promptText.setText(infoStr);
            }
        }
    }

    /**
     * 跳转Activity传递信息
     */
    private void handleResult(final int resID) {
        Intent intent = new Intent();
        if (resID == R.string.verify_success) {
            FaceIDDataStruct idDataStruct = mDetector.getFaceIDDataStruct();
            //获取delta
            String delta = idDataStruct.delta;
            //获取所有活体图片
            Map<String, byte[]> images = idDataStruct.images;
            EbeiLivenessData ebeiLivenessData = new EbeiLivenessData(delta, images, resID);
            intent.putExtra("result", ebeiLivenessData);
        } else {
            EbeiLivenessData ebeiLivenessData = new EbeiLivenessData(null, null, resID);
            intent.putExtra("result", ebeiLivenessData);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public void changeType(final DetectionType detectiontype, long timeout) {
        // 动画切换
        mEbeiIDetection.changeType(detectiontype, timeout);
        mEbeiFaceMask.setFaceInfo(null);

        // 语音播放
        if (mCurStep == 0) {
            mEbeiIMediaPlayer.doPlay(mEbeiIMediaPlayer.getSoundRes(detectiontype));
        } else {
            mEbeiIMediaPlayer.doPlay(R.raw.meglive_well_done);
            mEbeiIMediaPlayer.setOnCompletionListener(detectiontype);
        }
    }

    public void handleNotPass(final long remainTime) {
        if (remainTime > 0) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    timeOutText.setText(remainTime / 1000 + "");
                    mEbeiCircleProgressBar.setProgress((int) (remainTime / 100));
                }
            });
        }
    }

    /**
     * TextureView启动成功后 启动相机预览和添加活体检测回调
     * （TextureView.SurfaceTextureListener的接口回调方法）
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mHasSurface = true;
        doPreview();

        // 添加活体检测回调 （本Activity继承了DetectionListener）
        mDetector.setDetectionListener(this);
        // 添加相机预览回调（本Activity继承了PreviewCallback）
        mEbeiICamera.actionDetect(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    /**
     * TextureView销毁后 （TextureView.SurfaceTextureListener的接口回调方法）
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mHasSurface = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void doPreview() {
        if (!mHasSurface)
            return;

        mEbeiICamera.startPreview(camerapreview.getSurfaceTexture());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainHandler.removeCallbacksAndMessages(null);
        mEbeiICamera.closeCamera();
        mEbeiIMediaPlayer.close();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetector != null)
            mDetector.release();
        mEbeiDialogUtil.onDestory();
        mEbeiIDetection.onDestroy();
        ebeiSensorUtil.release();
    }
}