package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.math.BigDecimal;

/**
 * Created by ywd on 2018/12/6.
 */

public class EbeiProtocolModel extends EbeiBaseModel {
    private BigDecimal amount;
    private BigDecimal loanId;
    private String loanRemark;
    private BigDecimal periods;
    private String preProtocolUrl;
    private String protocolName;
    private String protocolUrl;
    private String repayRemark;
    private BigDecimal userId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLoanId() {
        return loanId;
    }

    public void setLoanId(BigDecimal loanId) {
        this.loanId = loanId;
    }

    public String getLoanRemark() {
        return loanRemark;
    }

    public void setLoanRemark(String loanRemark) {
        this.loanRemark = loanRemark;
    }

    public BigDecimal getPeriods() {
        return periods;
    }

    public void setPeriods(BigDecimal periods) {
        this.periods = periods;
    }

    public String getPreProtocolUrl() {
        return preProtocolUrl;
    }

    public void setPreProtocolUrl(String preProtocolUrl) {
        this.preProtocolUrl = preProtocolUrl;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public String getProtocolUrl() {
        return protocolUrl;
    }

    public void setProtocolUrl(String protocolUrl) {
        this.protocolUrl = protocolUrl;
    }

    public String getRepayRemark() {
        return repayRemark;
    }

    public void setRepayRemark(String repayRemark) {
        this.repayRemark = repayRemark;
    }

    public BigDecimal getUserId() {
        return userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }
}
