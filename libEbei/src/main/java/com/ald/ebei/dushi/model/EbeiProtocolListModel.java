package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.util.List;

/**
 * Created by ywd on 2018/12/6.
 */

public class EbeiProtocolListModel extends EbeiBaseModel {
    private List<EbeiProtocolModel> loanInfos;

    public List<EbeiProtocolModel> getLoanInfos() {
        return loanInfos;
    }

    public void setLoanInfos(List<EbeiProtocolModel> loanInfos) {
        this.loanInfos = loanInfos;
    }
}
