package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;
import com.ald.ebei.model.EbeiUrlModel;

import java.util.List;

/**
 * 上传身份证返回Model
 * Created by yaowenda on 17/5/16.
 */

public class EbeiUploadCardResultModel extends EbeiBaseModel {
    //身份证相关信息
    private EbeiYiTuResultModel cardInfo;
    //身份证正反面URL地址
    private List<EbeiUrlModel> cardPics;

    public EbeiYiTuResultModel getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(EbeiYiTuResultModel cardInfo) {
        this.cardInfo = cardInfo;
    }

    public List<EbeiUrlModel> getCardPics() {
        return cardPics;
    }

    public void setCardPics(List<EbeiUrlModel> cardPics) {
        this.cardPics = cardPics;
    }
}
