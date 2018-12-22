package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * Created by ywd on 2018/11/30.
 */

public class EbeiRepaymentSuccessModel extends EbeiBaseModel {
    private String bankName;
    private String cardNum;
    private String repayAmount;
    private String repayNo;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public String getRepayNo() {
        return repayNo;
    }

    public void setRepayNo(String repayNo) {
        this.repayNo = repayNo;
    }
}
