package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.util.List;

/**
 * Created by ywd on 2018/11/25.
 */

public class EbeiSupportBankListModel extends EbeiBaseModel {
    private List<EbeiSupportBankModel> listBank;

    public List<EbeiSupportBankModel> getListBank() {
        return listBank;
    }

    public void setListBank(List<EbeiSupportBankModel> listBank) {
        this.listBank = listBank;
    }
}
