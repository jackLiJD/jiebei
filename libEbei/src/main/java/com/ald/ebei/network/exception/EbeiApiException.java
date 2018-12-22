package com.ald.ebei.network.exception;

import android.util.Log;

import com.ald.ebei.util.EbeiHtmlRegexpUtil;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述：HTTP请求异常类
 * 修订历史：
 */
public class EbeiApiException extends RuntimeException {
    private int code;
    private String msg;

    public EbeiApiException(int resultCode, String msg) {
        this(msg);
        this.code = resultCode;
        this.msg = EbeiHtmlRegexpUtil.filterHtml(msg);
        Log.d("msg", msg);
        Log.d("resultCode", resultCode + "");
    }

    public EbeiApiException(String detailMessage) {
        super(detailMessage);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = EbeiHtmlRegexpUtil.filterHtml(msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
