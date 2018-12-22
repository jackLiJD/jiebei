package com.ald.ebei.util;

import com.ald.ebei.config.EbeiAccountProvider;

public class EbeiUnLandAccountProvider implements EbeiAccountProvider {
    @Override
    public String getUserName() {
        return EbeiInfoUtils.getDeviceId();
    }

    @Override
    public String getUserToken() {
        return "";
    }
}
