package com.ald.ebei.dushi.model;

import java.math.BigDecimal;

/**
 * Created by ywd on 2018/11/23.
 */

public class EbeiHomeNperModel {
    private String nper;//期数
    private BigDecimal remitFee;//减免手续费

    public String getNper() {
        return nper;
    }

    public void setNper(String nper) {
        this.nper = nper;
    }

    public BigDecimal getRemitFee() {
        return remitFee;
    }

    public void setRemitFee(BigDecimal remitFee) {
        this.remitFee = remitFee;
    }
}
