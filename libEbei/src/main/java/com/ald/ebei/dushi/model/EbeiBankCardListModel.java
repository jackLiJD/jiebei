package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.util.List;

/**
 * 银行卡列表实体类
 * Created by ywd on 2018/11/22.
 */

public class EbeiBankCardListModel extends EbeiBaseModel {
    private List<EbeiBankCardModel> bankInfos;

    public List<EbeiBankCardModel> getBankInfos() {
        return bankInfos;
    }

    public void setBankInfos(List<EbeiBankCardModel> bankInfos) {
        this.bankInfos = bankInfos;
    }
}
