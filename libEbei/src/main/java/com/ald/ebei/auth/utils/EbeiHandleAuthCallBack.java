package com.ald.ebei.auth.utils;

/*
 * Created by sean yu on 2017/7/23.
 */

public interface EbeiHandleAuthCallBack {

    /**
     * 开始授权
     */
    void authStart();

    /**
     * 授权成功
     */
    void authSuccess();

    /**
     * 授权时报
     */
    void authError();
}
