package com.ald.ebei.auth.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.auth.EbeiHandlePicCallBack;
import com.ald.ebei.auth.EbeiIDCardInfoModel;
import com.ald.ebei.auth.idcard.EbeiIDCardScanFactory;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.entity.EbeiApiResponse;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiEditTextWithDelNew;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiImageUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiPermissionCheck;
import com.ald.ebei.util.EbeiPermissions;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;


public class EbeiIdAuthActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private EbeiIDCardScanFactory cardScanFactory;
    private Context mContext;
    private Boolean mIsAppeal;
    private String uploadPath[] = new String[]{"", ""};

    public String frontSource = "";
    public String backSource = "";

    private ImageView ivIdBack;
    private ImageView ivIdFront;
    private RelativeLayout rlIdFront;
    private RelativeLayout rlIdBack;
    private EbeiEditTextWithDelNew etName;
    private TextView tvCardNum;
    private EbeiNoDoubleClickButton btnStart;
    private EbeiUploadCardResultModel model;

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_rrid_auth;
    }

    @Override
    protected void setViewModel() {

    }


    @Override
    public String getStatName() {
        return "身份认证";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EbeiBundleKeys.REQUEST_CODE_STAGE_RR_IDF_FRONT || requestCode == EbeiBundleKeys.REQUEST_CODE_STAGE_RR_IDF_BACK) {
            //7253
            cardScanFactory.handleScanResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        init();
        setListener();
    }

    private void init() {
        mContext = this;
        setTitle("上传身份证");
        setTitleColor(ContextCompat.getColor(this, R.color.text_important_color));
        setLeftImage(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                onBackPressed();
            }
        });


        mIsAppeal = false;

        ivIdBack = findViewById(R.id.ic_idback);
        ivIdFront = findViewById(R.id.ic_idfront);
        rlIdFront = findViewById(R.id.rl_idfront);
        rlIdBack = findViewById(R.id.rl_id_back);
        etName = findViewById(R.id.et_real_name);
        tvCardNum = findViewById(R.id.tv_id_number);
        btnStart = findViewById(R.id.btn_start);
        cardScanFactory = new EbeiIDCardScanFactory((Activity) mContext).setCallBack(new EbeiHandlePicCallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onHandle() {

            }

            @Override
            public void onSuccess(String[] picPath) {
                if (picPath.length > 1 && !EbeiMiscUtils.isEmpty(picPath[1])) {
                    uploadPath[1] = picPath[1];
                    backSource = picPath[1];
                    EbeiImageUtils.setRoundImage(ivIdBack, backSource, ContextCompat.getDrawable(mContext, R.drawable.bg_id_back), 10);
                    Glide.with(mContext).load(picPath[1]).into(ivIdBack);
                    ivIdBack.setVisibility(View.VISIBLE);
                } else {
                    uploadPath[0] = picPath[0];
                    frontSource = picPath[0];
                    EbeiImageUtils.setRoundImage(ivIdFront, frontSource, ContextCompat.getDrawable(mContext, R.drawable.bg_id_back), 10);
                    Glide.with(mContext).load(picPath[0]).into(ivIdFront);
                    ivIdFront.setVisibility(View.VISIBLE);
                }
                if (!EbeiMiscUtils.isEmpty(uploadPath[0]) && !EbeiMiscUtils.isEmpty(uploadPath[1])) {
                    EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN, "submit_id_click");
                    cardScanFactory.submitIDCard(uploadPath);
                    btnStart.setEnabled(true);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onIDInfoRecognized(EbeiIDCardInfoModel.CardInfo cardInfo) {

            }

        });
        ivIdBack.setVisibility(View.GONE);
        btnStart.setEnabled(false);
        EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN, "id_card_scan_open");
    }

    private void setListener() {
        rlIdFront.setOnClickListener(this);
        rlIdBack.setOnClickListener(this);
        ivIdFront.setOnClickListener(this);
        ivIdBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_idfront || id == R.id.ic_idfront) {
            if (checkPermission()) {
                EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN, "positive_click");
                cardScanFactory.scanFront();
            }
        } else if (id == R.id.rl_id_back || id == R.id.ic_idback) {
            if (checkPermission()) {
                EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN, "back_click");
                cardScanFactory.scanBack();
            }
        } else if (id == R.id.btn_start) {
            if (EbeiMiscUtils.isEmpty(uploadPath[0]) || EbeiMiscUtils.isEmpty(uploadPath[1])) {
                EbeiUIUtils.showToast("资料采集不全");
            } else {
//                EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN, "submit_id_click");
//                cardScanFactory.submitIDCard(uploadPath);
                if (model != null) {
                    updateRealNameAndJump();
                } else {
                    EbeiUIUtils.showToast("实名信息获取失败，请重试");
                }
            }
        }
    }

    public void setUserInfo(EbeiUploadCardResultModel ecaEbeiUploadCardResultModel) {
        model = ecaEbeiUploadCardResultModel;
        etName.setText(ecaEbeiUploadCardResultModel.getCardInfo().getName());
//        setEditTextChinese(editTextName);
        tvCardNum.setText(ecaEbeiUploadCardResultModel.getCardInfo().getCitizen_id());
    }

    private void updateRealNameAndJump() {
        JSONObject reqObj = new JSONObject();
        reqObj.put("citizenId", tvCardNum.getText().toString().trim());
        reqObj.put("realName", etName.getText().toString().trim());
        EbeiClient.getService(EbeiApi.class)
                .changeRealname(reqObj)
                .compose(EbeiRxUtils.<EbeiApiResponse>io_main())
                .subscribe(new EbeiBaseObserver<EbeiApiResponse>() {
                    @Override
                    public void onNext(EbeiApiResponse ebeiApiResponse) {
                        super.onNext(ebeiApiResponse);
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                    }
                });
        //从RRIdAuthActivity启动
        Intent intent = new Intent();
        intent.putExtra(EbeiBundleKeys.RR_IDF_MODEL, model);
        EbeiActivityUtils.push(EbeiStartFaceActivity.class, intent);
    }

    //相机权限检查
    private boolean checkPermission() {
        if (!EbeiPermissionCheck.getInstance().checkPermission(mContext, EbeiPermissions.facePermissions)) {
            EbeiPermissionCheck.getInstance().askForPermissions((Activity) mContext, EbeiPermissions.facePermissions, EbeiPermissionCheck.REQUEST_CODE_CAMERA);
            return false;
        } else {
            Camera camera;
            try {
                camera = Camera.open();
                camera.startPreview();
            } catch (Exception ex) {
                EbeiPermissionCheck.getInstance().showAskDialog((Activity) mContext,
                        R.string.ebei_permission_name_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                return false;
            } finally {
                camera = null;
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == EbeiPermissionCheck.REQUEST_CODE_CAMERA) {
                EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ASK_PERMISSION_CAMERA, EbeiConstant.APP_ASK_PERMISSION_CAMERA_SUCCESS);
            }
        }
        // 权限被用户拒绝时被调用
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissions[0])) {
            if (requestCode == EbeiPermissionCheck.REQUEST_CODE_CAMERA) {
                EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ASK_PERMISSION_CAMERA, EbeiConstant.APP_ASK_PERMISSION_CAMERA_FAILED);
            }
        }

    }
}
