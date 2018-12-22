package com.ald.ebei.util;

import com.ald.ebei.config.EbeiAccountProvider;

public class EbeiLandAccountProvider implements EbeiAccountProvider {
    private String userName;
    private String userToken;

    private EbeiLandAccountProvider() {
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    private void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getUserToken() {
        return userToken;
    }

    public static class Builder {
        EbeiLandAccountProvider ebeiLandAccountProvider;

        public Builder() {
            this.ebeiLandAccountProvider = new EbeiLandAccountProvider();
        }

        public EbeiLandAccountProvider build() {
            return ebeiLandAccountProvider;
        }

        public Builder setUserName(String userName) {
            if (EbeiMiscUtils.isEmpty(userName)) {
                throw new NullPointerException("user name must be null");
            }
            ebeiLandAccountProvider.setUserName(userName);
            return this;
        }

        public Builder setUserToken(String userToken) {
            if (EbeiMiscUtils.isEmpty(userToken)) {
                throw new NullPointerException("user token must be null");
            }
            ebeiLandAccountProvider.setUserToken(userToken);
            return this;
        }
    }
}
