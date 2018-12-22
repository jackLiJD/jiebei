package com.ald.ebei.auth.model;

/*
 * Created by liangchen on 2018/5/24.
 */

import com.ald.ebei.model.EbeiBaseModel;

public class EbeiIDCardModel<T> extends EbeiBaseModel {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;
}
