package com.ald.ebei.model;

public class EbeiAvailableCreditModel extends EbeiBaseModel {
    private String totalAmount;
    private String usedAmount;

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(String usedAmount) {
        this.usedAmount = usedAmount;
    }
}
