package com.ald.ebei.auth.idcard.impl;

import android.app.Activity;
import android.content.Intent;

import com.ald.ebei.R;
import com.ald.ebei.auth.EbeiHandlePicCallBack;
import com.ald.ebei.auth.idcard.EbeiIDCardScan;
import com.ald.ebei.auth.model.EbeiIDCardModel;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;
import com.ald.ebei.auth.utils.EbeiHandleAuthCallBack;
import com.ald.ebei.auth.utils.EbeiHandleFaceAuthTask;
import com.ald.ebei.auth.utils.EbeiHandlePicTask;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.EbeiNetworkUtil;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiPermissionCheck;
import com.ald.ebei.util.EbeiPermissions;
import com.ald.ebei.util.EbeiUIUtils;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

//import com.megvii.idcardlib.EcaIDCardScanActivity;

/**
 * 单纯身份证验证(face++)
 * Created by sean yu on 2017/7/24.
 */

public class EbeiFaceIDCardPhoto implements EbeiIDCardScan {

    private static final String CARD_TYPE_SIDE = "side";
    //依图身份证认证常量
    private static final int CARD_TYPE_FRONT = 0;
    private static final int CARD_TYPE_BACK = 1;
    //
    private static final int REQUEST_CODE_STAGE_RR_IDF_FRONT = 0x112;
    private static final int REQUEST_CODE_STAGE_RR_IDF_BLACK = 0x111;
    private static final int PERMISSIONS_ASK_CODE = 0x110;
    //faceId返回图片
    private static final String ID_CARD_IMAGE = "idcardImg";
    private boolean isAllowAuth;
    //图片字节流

    private EbeiHandlePicCallBack callBack;
    private Activity context;

    public EbeiFaceIDCardPhoto(Activity context) {
        this.context = context;
    }

    /**
     * 设置回调监听
     *
     * @param callBack 处理返回监听
     */
    public EbeiFaceIDCardPhoto setCallBack(EbeiHandlePicCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     * 检查faceSDK授权
     */
    private void checkFaceAuth(final int side) {
        new EbeiHandleFaceAuthTask(context).setCallBack(new EbeiHandleAuthCallBack() {
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
        if (!EbeiPermissionCheck.getInstance().checkPermission(context, EbeiPermissions.facePermissions)) {
            EbeiPermissionCheck.getInstance().askForPermissions(context, EbeiPermissions.facePermissions, PERMISSIONS_ASK_CODE);
        } else {
//            Intent intent = new Intent(context, EcaIDCardScanActivity.class);
//            intent.putExtra(CARD_TYPE_SIDE, side);
//            intent.putExtra("isvertical", false);
//            if (side == CARD_TYPE_FRONT) {
//                context.startActivityForResult(intent, REQUEST_CODE_STAGE_RR_IDF_FRONT);
//            } else if (side == CARD_TYPE_BACK) {
//                context.startActivityForResult(intent, REQUEST_CODE_STAGE_RR_IDF_BLACK);
//            }
        }
        EbeiNetworkUtil.dismissCutscenes();
    }

    /**
     * 认证结果处理
     */
    public void handleScanResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_STAGE_RR_IDF_FRONT:
                    byte[] frontByte = data.getByteArrayExtra(ID_CARD_IMAGE);
                    if (frontByte != null && frontByte.length > 0) {
                        new EbeiHandlePicTask().setCallBack(callBack).execute(frontByte);
                    } else {
                        EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                    }
                    break;

                case REQUEST_CODE_STAGE_RR_IDF_BLACK:
                    byte[] blackByte = data.getByteArrayExtra(ID_CARD_IMAGE);
                    if (blackByte != null && blackByte.length > 0) {
                        new EbeiHandlePicTask().setCallBack(callBack).execute(blackByte);
                    } else {
                        EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
                    }
                    break;
            }
        }
    }

    @Override
    public void submitIDCard(String[] pictureArray) {
        if (pictureArray != null && pictureArray.length == 2) {
//            String subPath = "app/file/uploadIdNumberCardForFacePlus";
            String subPath = "/app/open/sdk/file/uploadIdNumberCardForFacePlus";
            String path = EbeiConfig.getEbeiServerProvider().getImageServer() + subPath;
            RequestBody frontRequestBody = RequestBody
                    .create(MediaType.parse("multipart/form-data"), new File(pictureArray[0]));
            RequestBody blackRequestBody = RequestBody
                    .create(MediaType.parse("multipart/form-data"), new File(pictureArray[1]));
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)//表单类型
                    .addFormDataPart("reqData", new JSONObject().toJSONString());
            builder.addFormDataPart("file", "front.jpg", frontRequestBody);
            builder.addFormDataPart("file", "back.jpg", blackRequestBody);
            List<MultipartBody.Part> parts = builder.build().parts();
            Observable<EbeiUploadCardResultModel> observable= EbeiClient.getService(EbeiApi.class).uploadFile(path, parts);
            observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EbeiUploadCardResultModel>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(EbeiUploadCardResultModel ecaEbeiUploadCardResultModel) {
                            EbeiNetworkUtil.dismissCutscenes();
                            if (ecaEbeiUploadCardResultModel != null && EbeiMiscUtils.isNotEmpty(ecaEbeiUploadCardResultModel.getCardPics()) && ecaEbeiUploadCardResultModel.getCardPics().size() == 2) {
                                submitFaceCard(ecaEbeiUploadCardResultModel);
                            } else {
                                EbeiUIUtils.showToast(R.string.ebei_rr_identification_success);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            EbeiUIUtils.showToast(R.string.ebei_rr_identification_err);
        }
    }

    @Override
    public void submitIDCard(String picture) {

    }

    /**
     * 提交身份证信息
     */
    private void submitFaceCard(EbeiUploadCardResultModel model) {
        JSONObject requestObj = new JSONObject();
        requestObj.put("address", model.getCardInfo().getAddress());
        requestObj.put("citizenId", model.getCardInfo().getCitizen_id() + "");
        requestObj.put("gender", model.getCardInfo().getGender());
        requestObj.put("nation", model.getCardInfo().getNation());
        requestObj.put("name", model.getCardInfo().getName());
        requestObj.put("validDateBegin", model.getCardInfo().getValid_date_begin());
        requestObj.put("validDateEnd", model.getCardInfo().getValid_date_end());
        requestObj.put("birthday", model.getCardInfo().getBirthday());
        requestObj.put("agency", model.getCardInfo().getAgency());
        requestObj.put("idFrontUrl", model.getCardPics().get(0).getUrl());
        requestObj.put("idBehindUrl", model.getCardPics().get(1).getUrl());
        requestObj.put("type", "FACE_PLUS_CARD");
        Observable<EbeiIDCardModel> observable = EbeiClient.getService(EbeiApi.class).saveRealnameInfo();
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EbeiIDCardModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EbeiIDCardModel ecaEbeiIdCardModel) {
                        EbeiUIUtils.showToast(R.string.ebei_rr_identification_success);
                        context.setResult(Activity.RESULT_OK);
                        context.finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
