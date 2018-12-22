package com.ald.ebei.dushi.model;

/**
 * Created by ywd on 2018/11/21.
 */

public class EbeiMonthlySupplyModel {
    private String period;
    private String interest;
    private String date;
    private String serviceFee;

    public String getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(String serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
