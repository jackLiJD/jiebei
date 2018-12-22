package com.ald.ebei.auth.liveness;

import android.app.Activity;

/**
 * Created by sean yu on 2017/7/24.
 */

public interface EbeiFaceLivenessCallBack extends EbeiLivenessCallBack {

    /**
     * face++验证成功回调
     */
    void onFaceSuccess(String delta, byte[] queryPackage, Activity context);
}
