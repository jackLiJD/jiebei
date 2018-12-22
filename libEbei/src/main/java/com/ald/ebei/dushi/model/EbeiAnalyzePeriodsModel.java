package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.math.BigDecimal;
import java.util.List;

public class EbeiAnalyzePeriodsModel extends EbeiBaseModel {

    private List<EbeiAnalyzePeriodsModelData> periods;

    private BigDecimal totalProfit;//手续费加服务费

    public List<EbeiAnalyzePeriodsModelData> getPeriods() {
        return periods;
    }

    public void setPeriods(List<EbeiAnalyzePeriodsModelData> periods) {
        this.periods = periods;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }



}
