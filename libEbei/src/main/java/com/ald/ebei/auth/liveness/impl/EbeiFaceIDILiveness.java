package com.ald.ebei.auth.liveness.impl;

import android.app.Activity;
import android.content.Intent;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.auth.activity.EbeiFaceErrActivity;
import com.ald.ebei.auth.activity.EbeiIdAuthActivity;
import com.ald.ebei.auth.activity.EbeiStartFaceActivity;
import com.ald.ebei.auth.liveness.EbeiILiveness;
import com.ald.ebei.auth.liveness.EbeiLivenessActivity;
import com.ald.ebei.auth.liveness.EbeiLivenessData;
import com.ald.ebei.auth.model.EbeiFaceLivenessModel;
import com.ald.ebei.auth.model.EbeiIDCardModel;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;
import com.ald.ebei.auth.utils.EbeiHandleAuthCallBack;
import com.ald.ebei.auth.utils.EbeiHandleFaceAuthTask;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.EbeiNetworkUtil;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiTipsDialog;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiDataUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiUIUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ald.ebei.util.EbeiUIUtils.sendBroadcast;

/**
 * face++人脸识别
 * Created by sean yu on 2017/7/23.
 */

public class EbeiFaceIDILiveness implements EbeiILiveness, Serializable {
    private EbeiUploadCardResultModel model;
    private Activity context;

    private boolean isAllowAuth;

    public static int FACE_ID_RESULT = 0x111;
    public static int REQUEST_CODE_ERR = 0x1025;

    public EbeiFaceIDILiveness(Activity context) {
        this.context = context;
    }

    /**
     *
     */
    @Override
    public void startLiveness(EbeiUploadCardResultModel model, String scene) {
        this.model = model;
        if (isAllowAuth) {
            goToLiveness();
        } else {
            checkFaceAuth();
        }
    }

    /**
     * 提交数据到后台服务器
     * Face++人脸
     */
    private void submitFace(String delta, Map<String, byte[]> data, final Activity context) throws IOException {
        EbeiNetworkUtil.showCutscenes(context, "处理中", "处理中...");
//        String subPath = "app/file/uploadFaceForFacePlus";
//        String subPath = "/app/open/sdk/file/uploadFaceForFacePlus";
        String subPath = "/file/faceplus/faces.up";
        String path = EbeiConfig.getEbeiServerProvider().getImageServer() + subPath;

        File file = EbeiDataUtils.createIfNotExists("face");
        EbeiDataUtils.saveToFile(data.get("image_best"), file);
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        File fileBackgroud = EbeiDataUtils.createIfNotExists("backgroud");
        EbeiDataUtils.saveToFile(data.get("image_env"), fileBackgroud);
        RequestBody bgBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileBackgroud);

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)//表单类型
                .addFormDataPart("delta", delta);
        builder.addFormDataPart("idCardNumber", model.getCardInfo().getCitizen_id());
        builder.addFormDataPart("idCardName", model.getCardInfo().getName());


        builder.addFormDataPart("files", "filename=\"front.png", imageBody);
        builder.addFormDataPart("files", "filename=\"image_env.png", bgBody);

        List<MultipartBody.Part> parts = builder.build().parts();
        Observable<EbeiFaceLivenessModel> observable = EbeiClient.getService(EbeiApi.class).uploadFaceLivenessFile(path, parts);
        EbeiNetworkUtil.showCutscenes(context, null);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new EbeiBaseObserver<EbeiFaceLivenessModel>() {
                    @Override
                    public void onNext(EbeiFaceLivenessModel livenessModel) {
                        super.onNext(livenessModel);
                        if (livenessModel != null) {
//                            JSONObject dataObj = new JSONObject();
//                            dataObj.put("imageBestUrl", livenessModel.getImageBestUrl());
//                            dataObj.put("confidence", livenessModel.getConfidence() + "");
//                            dataObj.put("thresholds", livenessModel.getThresholds());
//                            Observable<EbeiIDCardModel> observable = EbeiClient.getService(EbeiApi.class).submitFaceInfoForFacePlus(dataObj);
//                            observable.subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .unsubscribeOn(Schedulers.io())
//                                    .subscribe(new EbeiBaseObserver<EbeiIDCardModel>() {
//                                                   @Override
//                                                   public void onNext(EbeiIDCardModel ecaIdCardModel) {
//                                                       EbeiNetworkUtil.dismissForceCutscenes();
////                                                       EbeiActivityUtils.popUntilWithoutRefresh(EbeiStartFaceActivity.class);
//                                                       EbeiActivityUtils.finish(EbeiIdAuthActivity.class);
//                                                       sendBroadcast(new Intent(EbeiHtml5WebView.ACTION_AUTH_FACE_FINISH));
//                                                       context.finish();
//                                                   }
//                                               }
//                                    );
                            saveRealNameInfo();
                        } else {
                            EbeiNetworkUtil.dismissForceCutscenes();
                            EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                        }
                    }
                });
    }

    /**
     * 提交身份证信息
     */
    private void saveRealNameInfo() {
        Observable<EbeiIDCardModel> observable = EbeiClient.getService(EbeiApi.class).saveRealnameInfo();
        EbeiNetworkUtil.showCutscenes(context, null);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new EbeiBaseObserver<EbeiIDCardModel>() {

                    @Override
                    public void onNext(EbeiIDCardModel ecaEbeiIdCardModel) {
                        EbeiNetworkUtil.dismissCutscenes();
                        EbeiActivityUtils.popUntilWithoutRefresh(EbeiStartFaceActivity.class);
                        EbeiActivityUtils.finish(EbeiIdAuthActivity.class);
                        sendBroadcast(new Intent(EbeiHtml5WebView.ACTION_AUTH_FACE_FINISH));
                        context.setResult(Activity.RESULT_OK);
                        context.finish();
//                        if (isAllowAuth) {
//                            goToLiveness();
//                        } else {
//                            checkFaceAuth();
//                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        if (ebeiApiException.getCode() == 1310) {
                            EbeiTipsDialog dialog = new EbeiTipsDialog(context);
                            dialog.setTitle("提示");
                            dialog.setContent(ebeiApiException.getMessage());
                            dialog.show();
                        } else
                            EbeiUIUtils.showToastWithoutTime(ebeiApiException.getMsg());
                    }

                });
    }

    /**
     * 打开face++人脸识别
     */
    private void goToLiveness() {
        Intent intent = new Intent(context, EbeiLivenessActivity.class);
        context.startActivityForResult(intent, FACE_ID_RESULT);
//        EcaUIUtils.showToast("gotoliveness");
    }

    /**
     * 检查faceSDK授权
     */
    private void checkFaceAuth() {
        new EbeiHandleFaceAuthTask(context).setCallBack(new EbeiHandleAuthCallBack() {
            @Override
            public void authStart() {

            }

            @Override
            public void authSuccess() {
                isAllowAuth = true;
                goToLiveness();
            }

            @Override
            public void authError() {
                isAllowAuth = false;
            }
        }).setAuthType(EbeiHandleFaceAuthTask.AUTH_FACE).execute();
    }

    @Override
    public void handleLivenessResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == FACE_ID_RESULT) {
                EbeiLivenessData resultData = (EbeiLivenessData) data.getSerializableExtra("result");
                if (EbeiMiscUtils.isNotEmpty(resultData.getDelta()) && resultData.getImages() != null) {
                    if (resultData.getImages().containsKey("image_best")) {
                        try {
//                            submitFace(resultData.getDelta(), resultData.getImages().get("image_best"), context);
                            submitFace(resultData.getDelta(), resultData.getImages(), context);
                            EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_FACE_LIVENESS, "distinguish_success");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (resultData.getResID() != 0) {
                    String errorMsg = context.getResources().getString(resultData.getResID());
                    faceErr(errorMsg);
                    EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_FACE_LIVENESS, "distinguish_failed");
                } else {
                    String errorMsg = context.getResources().getString(R.string.ebei_liven_app_error);
                    faceErr(errorMsg);
                    EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_FACE_LIVENESS, "distinguish_failed");
                }


            }
        }

    }

    /**
     * 人脸识别失败
     *
     * @param errMsg 错误信息
     */
    private void faceErr(String errMsg) {
        Intent intent = new Intent();
        intent.putExtra("errMsg", errMsg);
        EbeiActivityUtils.push(EbeiFaceErrActivity.class, intent, REQUEST_CODE_ERR);
    }
}
