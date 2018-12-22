package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * 银行实体类
 * Created by ywd on 2018/11/25.
 */

public class EbeiBankModel extends EbeiBaseModel {
    private String bankCode;
    private String bankIcon;
    private String bankName;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankIcon() {
        return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
        this.bankIcon = bankIcon;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
