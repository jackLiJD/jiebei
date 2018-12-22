package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * Created by ywd on 2018/11/27.
 */

public class EbeiUserInfoModel extends EbeiBaseModel {
    private String bankcardStatus;
    private String idNumber;
    private String idcard;
    private String name;
    private String realName;

    public String getBankcardStatus() {
        return bankcardStatus;
    }

    public void setBankcardStatus(String bankcardStatus) {
        this.bankcardStatus = bankcardStatus;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
