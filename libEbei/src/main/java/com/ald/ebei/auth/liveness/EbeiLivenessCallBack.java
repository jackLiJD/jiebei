package com.ald.ebei.auth.liveness;

import android.app.Activity;

/**
 * 活体认证监听
 * Created by sean yu on 2017/7/23.
 */

public interface EbeiLivenessCallBack {

    /**
     * 活体认证成功
     *
     * @param queryPackage;
     * @param context
     */
    void onDetectionSuccess(byte[] queryPackage, Activity context);

    /**
     * 活体认证失败
     *
     * @param failedMsg 认证失败信息
     * @param context
     */
    void onDetectionFailed(String failedMsg, Activity context);
}
