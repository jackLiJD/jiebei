package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * Created by sean yu on 2017/7/24.
 */
public class EbeiFaceTypeModel extends EbeiBaseModel {
    public String type;

    public String switchType;

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
