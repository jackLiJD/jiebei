package com.ald.ebei.auth.idcard.impl;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.auth.EbeiHandlePicCallBack;
import com.ald.ebei.auth.EbeiIDCardInfoModel;
import com.ald.ebei.auth.activity.EbeiIdAuthActivity;
import com.ald.ebei.auth.idcard.EbeiIDCardScan;
import com.ald.ebei.auth.idcard.EbeiIDCardScanActivity;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;
import com.ald.ebei.auth.utils.EbeiHandleAuthCallBack;
import com.ald.ebei.auth.utils.EbeiHandleFaceAuthTask;
import com.ald.ebei.auth.utils.EbeiHandlePicTask;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.EbeiNetworkUtil;
import com.ald.ebei.network.EbeiRequestCallBack;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.dialog.EbeiCustomDialog;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiPermissionCheck;
import com.ald.ebei.util.EbeiPermissions;
import com.ald.ebei.util.EbeiUIUtils;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * face++ 身份证
 * Created by sean yu on 2017/7/21.
 */

public class EbeiFaceIDCard implements EbeiIDCardScan {

    private static final String CARD_TYPE_SIDE = "side";
    //依图身份证认证常量
    private static final int CARD_TYPE_FRONT = 0;
    private static final int CARD_TYPE_BACK = 1;
    //

    //faceId返回图片
    private static final String ID_CARD_IMAGE = "idcardImg";
    private boolean isAllowAuth;

    private EbeiHandlePicCallBack callBack;
    private Activity mContext;
    private String stageJump;

    public EbeiFaceIDCard(Activity context) {
        this.mContext = context;
        this.stageJump = context.getIntent().getStringExtra(EbeiBundleKeys.STAGE_JUMP);
    }

    /**
     * 设置回调监听
     *
     * @param callBack 处理返回监听
     */
    public EbeiFaceIDCard setCallBack(EbeiHandlePicCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     * 检查faceSDK授权
     */
    private void checkFaceAuth(final int side) {
        new EbeiHandleFaceAuthTask(mContext).setCallBack(new EbeiHandleAuthCallBack() {
            @Override
            public void authStart() {

            }

            @Override
            public void authSuccess() {
                isAllowAuth = true;
                goToFaceID(side);
            }

            @Override
            public void authError() {
                isAllowAuth = false;
            }
        }).execute();
    }

    @Override
    public void scanFront() {
        if (isAllowAuth) {
            goToFaceID(CARD_TYPE_FRONT);
        } else {
            checkFaceAuth(CARD_TYPE_FRONT);
        }
    }

    @Override
    public void scanBack() {
        if (isAllowAuth) {
            goToFaceID(CARD_TYPE_BACK);
        } else {
            checkFaceAuth(CARD_TYPE_BACK);
        }

    }


    /**
     * face++ 身份证识别
     */
    private void goToFaceID(int side) {
        if (!EbeiPermissionCheck.getInstance().checkPermission(mContext, EbeiPermissions.facePermissions)) {
            EbeiPermissionCheck.getInstance().askForPermissions(mContext, EbeiPermissions.facePermissions, EbeiPermissionCheck.REQUEST_CODE_CAMERA);
        } else {
            Intent intent = new Intent(mContext, EbeiIDCardScanActivity.class);
            intent.putExtra(CARD_TYPE_SIDE, side);
            intent.putExtra("isvertical", false);
            if (side == CARD_TYPE_FRONT) {
                mContext.startActivityForResult(intent, EbeiBundleKeys.REQUEST_CODE_STAGE_RR_IDF_FRONT);
            } else if (side == CARD_TYPE_BACK) {
//                EbeiActivityUtils.push(EcaIDCardScanActivity.class, intent, REQUEST_CODE_STAGE_RR_IDF_BLACK);
                mContext.startActivityForResult(intent, EbeiBundleKeys.REQUEST_CODE_STAGE_RR_IDF_BACK);
//                context.startActivity(intent);
            }
        }
        EbeiNetworkUtil.dismissCutscenes();
    }

    /**
     * 认证结果处理
     */
    public void handleScanResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case EbeiBundleKeys.REQUEST_CODE_STAGE_RR_IDF_FRONT:
                    byte[] frontByte = data.getByteArrayExtra(ID_CARD_IMAGE);
                    if (frontByte != null && frontByte.length > 0) {
                        new EbeiHandlePicTask().setCallBack(callBack).execute(frontByte, null);
                        EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN,"id_card_front_success");
                    } else {
                        EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                        EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN,"id_card_front_failed");
                    }
                    break;

                case EbeiBundleKeys.REQUEST_CODE_STAGE_RR_IDF_BACK:
                    byte[] blackByte = data.getByteArrayExtra(ID_CARD_IMAGE);
                    if (blackByte != null && blackByte.length > 0) {
                        new EbeiHandlePicTask().setCallBack(callBack).execute(null, blackByte);
                        EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN,"id_card_back_success");
                    } else {
                        EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                        EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_ID_CARD_SCAN,"id_card_back_failed");
                    }
                    break;
            }
        }
    }

    @Override
    public void submitIDCard(String[] pictureArray) {
        if (pictureArray != null && pictureArray.length == 2) {
//            String subPath =  "app/file/uploadIdNumberCardForFacePlus";
//            String subPath =  "/app/open/sdk/file/uploadIdNumberCardForFacePlus";
            String subPath =  "/file/faceplus/idCard.up";
            String path = EbeiConfig.getEbeiServerProvider().getImageServer() + subPath;
            RequestBody frontRequestBody = RequestBody
                    .create(MediaType.parse("multipart/form-data"), new File(pictureArray[0]));
            RequestBody backRequestBody = RequestBody
                    .create(MediaType.parse("multipart/form-data"), new File(pictureArray[1]));
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)//表单类型
                    .addFormDataPart("reqData", new JSONObject().toJSONString());
            builder.addFormDataPart("files", "front.jpg", frontRequestBody);
            builder.addFormDataPart("files", "back.jpg", backRequestBody);
            List<MultipartBody.Part> parts = builder.build().parts();
            Observable<EbeiUploadCardResultModel> observable= EbeiClient.getService(EbeiApi.class).uploadFile(path, parts);
            EbeiNetworkUtil.showCutscenes(mContext,null);
            observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new EbeiBaseObserver<EbeiUploadCardResultModel>() {

                        @Override
                        public void onNext(EbeiUploadCardResultModel ecaEbeiUploadCardResultModel) {
                            if (ecaEbeiUploadCardResultModel != null && EbeiMiscUtils.isNotEmpty(ecaEbeiUploadCardResultModel.getCardPics()) && ecaEbeiUploadCardResultModel.getCardPics().size() == 2) {
                                Intent intent = new Intent();
                                intent.putExtra(EbeiBundleKeys.STAGE_JUMP, stageJump);
                                intent.putExtra(EbeiBundleKeys.RR_IDF_MODEL, ecaEbeiUploadCardResultModel);
                                intent.putExtra(EbeiBundleKeys.INTENT_KEY_CREDIT_PROMOTE_SCENE,
                                        mContext.getIntent().getStringExtra(EbeiBundleKeys.INTENT_KEY_CREDIT_PROMOTE_SCENE));

                                //防止RRIdAuthActivity已经被销毁 引发 leaked window
                                Activity currentActivity = EbeiConfig.getCurrentActivity();
                                if (currentActivity instanceof EbeiIdAuthActivity) {
//                                    ConfirmDialog dialog = new ConfirmDialog(EbeiConfig.getCurrentActivity(), intent, false);
//                                    dialog.show();
                                    ((EbeiIdAuthActivity)currentActivity).setUserInfo(ecaEbeiUploadCardResultModel);
                                }
                            } else {
                                EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                            }
                        }

                        @Override
                        public void onFailure(EbeiApiException ebeiApiException) {
                            super.onFailure(ebeiApiException);
                            if(ebeiApiException.getCode()==1307)
                            {
                                new EbeiCustomDialog.Builder(mContext).setMessage(ebeiApiException.getMessage())
                                        .setNegativeButton(R.string.ebei_cancel, null)
                                        .setNegativeButtonTextColor(ContextCompat.getColor(mContext, R.color.text_important_color))
                                        .setPositiveButton(R.string.ebei_dialog_btn_customer, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                String url = EbeiConfig.getEbeiServerProvider().getAppServer() + EbeiConstant.H5_URL_HELP;
                                                intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, url);
                                                EbeiActivityUtils.push(EbeiHtml5WebView.class, intent);
                                            }
                                        }).create().show();
                            }
                            EbeiUIUtils.showToast(ebeiApiException.getMessage());
                        }
                    });
        }
    }

    /**
     * @param picture 银行卡正面路径
     */
    @Override
    public void submitIDCard(String picture) {
        if (EbeiMiscUtils.isNotEmpty(picture)) {
            String path = EbeiConfig.getEbeiServerProvider().getImageServer() + "app/file/plusUpIdCardFront.htm";
            RequestBody frontRequestBody = RequestBody
                    .create(MediaType.parse("multipart/form-data"), new File(picture));
//            RequestBody blackRequestBody = RequestBody
//                    .create(MediaType.parse("multipart/form-data"), new File(pictureArray[1]));
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)//表单类型
                    .addFormDataPart("reqData", new JSONObject().toJSONString());
            builder.addFormDataPart("file", "front.jpg", frontRequestBody);
            List<MultipartBody.Part> parts = builder.build().parts();
            Call<EbeiIDCardInfoModel> call = EbeiClient.getService(EbeiApi.class).uploadIDFile(path, parts);
            EbeiNetworkUtil.showCutscenes(mContext, call);
            call.enqueue(new EbeiRequestCallBack<EbeiIDCardInfoModel>() {
                @Override
                public void onSuccess(Call<EbeiIDCardInfoModel> call, Response<EbeiIDCardInfoModel> response) {
                    EbeiNetworkUtil.dismissCutscenes();
                    if (response.body() != null && response.body() != null) {
                        EbeiIDCardInfoModel model = response.body();
                        callBack.onIDInfoRecognized(model.getCardInfo());
                    } else {
                        EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                    }
                }

                @Override
                protected boolean errorHandle(Call<EbeiIDCardInfoModel> call, EbeiApiException ebeiApiException) {
                    if (ebeiApiException.getCode() == 1307) {
                        new EbeiCustomDialog.Builder(mContext).setMessage(ebeiApiException.getMessage())
                                .setNegativeButton(R.string.ebei_cancel, null)
                                .setNegativeButtonTextColor(ContextCompat.getColor(mContext, R.color.text_important_color))
                                .setPositiveButton(R.string.ebei_dialog_btn_customer, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        String url = EbeiConfig.getEbeiServerProvider().getAppServer() + EbeiConstant.H5_URL_HELP;
                                        intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, url);
                                        EbeiActivityUtils.push(EbeiHtml5WebView.class, intent);
                                    }
                                }).create().show();
                    } else {
                        EbeiUIUtils.showToast(ebeiApiException.getMessage());
                    }
                    return true;
                }
            });
        } else {
            EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
        }
    }


}
