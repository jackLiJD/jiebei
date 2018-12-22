package com.ald.ebei.model;

public class EbeiLandCheckModel {

    /**
     * msg : 很抱歉，您的资质暂时不符合，您可以x天后重新提交申请, 如果数据为空就不用取，否则取，供页面显示
     * okLoan : true
     * redirectType : creditAuth(跳转到信用认证页面), identityAuth(跳转到身份证页面)，为空则不跳
     */

    private String msg;
    private boolean okLoan;
    private String redirectType;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOkLoan() {
        return okLoan;
    }

    public void setOkLoan(boolean okLoan) {
        this.okLoan = okLoan;
    }

    public String getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(String redirectType) {
        this.redirectType = redirectType;
    }
}
