package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

public class EbeiAuth51FundUrl extends EbeiBaseModel {
    private String authUrl;
    private String token;
    private String orderSn;

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
}
