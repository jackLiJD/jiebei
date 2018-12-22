package com.ald.ebei.auth.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;

import java.util.UUID;

import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.LivenessLicenseManager;


/**
 * Created by sean yu on 2017/7/23.
 */

public class EbeiHandleFaceAuthTask extends AsyncTask<String, Integer, Long> {
    public static final String AUTH_FACE = "auth_face";
    public static final String AUTH_ID_CARD = "auth_id_card";

    private Activity context;
    private String authType = AUTH_ID_CARD;
    private EbeiHandleAuthCallBack callBack;

    public EbeiHandleFaceAuthTask(Activity context) {
        this.context = context;
    }


    public EbeiHandleFaceAuthTask setAuthType(String authType) {
        this.authType = authType;
        return this;
    }

    public EbeiHandleFaceAuthTask setCallBack(EbeiHandleAuthCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callBack != null) {
            callBack.authStart();
        }
    }

    @Override
    protected Long doInBackground(String... params) {
        if (authType.equals(AUTH_FACE)) {
            return authFace();
        }
        return authIDCard();
    }

    /**
     * 身份证授权
     *
     * @return 授权码
     */
    private long authIDCard() {
        Manager manager = new Manager(context);
        IDCardQualityLicenseManager licenseManager = new IDCardQualityLicenseManager(context);
        manager.registerLicenseManager(licenseManager);

        String uuid = UUID.randomUUID().toString();
        uuid = Base64.encodeToString(uuid.getBytes(), Base64.DEFAULT);
        manager.takeLicenseFromNetwork(uuid);
        return licenseManager.checkCachedLicense();
    }

    /**
     * 人脸识别授权
     *
     * @return 授权码
     */
    private long authFace() {
        Manager manager = new Manager(context);
        LivenessLicenseManager licenseManager = new LivenessLicenseManager(context);
        manager.registerLicenseManager(licenseManager);

        String uuid = UUID.randomUUID().toString();
        uuid = Base64.encodeToString(uuid.getBytes(), Base64.DEFAULT);
        manager.takeLicenseFromNetwork(uuid);
        return licenseManager.checkCachedLicense();
    }


    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        if (aLong > 0 && callBack != null) {
            callBack.authSuccess();
        } else if (callBack != null) {
            callBack.authError();
        }
    }
}
