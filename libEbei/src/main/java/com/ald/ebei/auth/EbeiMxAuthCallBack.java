package com.ald.ebei.auth;

/**
 * 魔蝎认证回调
 * Created by sean yu on 2017/7/10.
 */

public interface EbeiMxAuthCallBack {

    /**
     * 认证成功
     */
    void authSuccess(String authCode, String msg, String userId);

    /**
     * 认证失败
     */
    void authError(String authCode, String errorMsg);
}
