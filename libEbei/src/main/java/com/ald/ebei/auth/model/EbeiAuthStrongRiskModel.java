package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * 提交强风控返回实体类
 * Created by yaowenda on 17/7/6.
 */

public class EbeiAuthStrongRiskModel extends EbeiBaseModel {
    private String creditRebateMsg;

    public String getCreditRebateMsg() {
        return creditRebateMsg;
    }

    public void setCreditRebateMsg(String creditRebateMsg) {
        this.creditRebateMsg = creditRebateMsg;
    }
}
