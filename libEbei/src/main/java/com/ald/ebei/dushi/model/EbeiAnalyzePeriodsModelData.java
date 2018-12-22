package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.math.BigDecimal;

public class EbeiAnalyzePeriodsModelData extends EbeiBaseModel {
    /**
     * amount : 金额
     * interestFee : 利息
     * nper : 期数
     * repayDay : 还款日
     * serviceFee : 服务费
     */

    private BigDecimal amount;
    private BigDecimal interestFee;
    private int nper;
    private String repayDay;
    private BigDecimal serviceFee;

    @Override
    public String toString() {
        return "Data{" +
                "amount=" + amount +
                ", interestFee=" + interestFee +
                ", nper=" + nper +
                ", repayDay='" + repayDay + '\'' +
                ", serviceFee=" + serviceFee +
                '}';
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterestFee() {
        return interestFee;
    }

    public void setInterestFee(BigDecimal interestFee) {
        this.interestFee = interestFee;
    }

    public int getNper() {
        return nper;
    }

    public void setNper(int nper) {
        this.nper = nper;
    }

    public String getRepayDay() {
        return repayDay;
    }

    public void setRepayDay(String repayDay) {
        this.repayDay = repayDay;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }
}
