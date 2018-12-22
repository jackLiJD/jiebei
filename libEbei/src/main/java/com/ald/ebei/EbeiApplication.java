package com.ald.ebei;

import android.support.multidex.MultiDexApplication;

import com.ald.ebei.config.EbeiAccountProvider;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.config.EbeiServerProvider;
import com.ald.ebei.util.EbeiBqsUtils;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiInfoUtils;
import com.ald.ebei.util.EbeiLandAccountProvider;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiMXAuthUtils;
import com.ald.ebei.util.EbeiSPUtil;
import com.ald.ebei.util.EbeiUnLandAccountProvider;
import com.ald.ebei.util.baiduloc.EbeiLocationService;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

//import cn.jpush.android.api.JPushInterface;

public class EbeiApplication extends MultiDexApplication implements EbeiAccountProvider, EbeiServerProvider {
    private static EbeiApplication app;
    private static final String appServerTest = "http://testrest.dushiebei.com/";
    //    private static final String appServerTest = "http://172.20.20.88:9001/";
    private static final String appServerPreview = EbeiConstant.S_PREVIEW;
    private static final String appServerRelease = "https://ebrest.edspay.com/";
    public static boolean isLogin = false;

    private static final String h5Server = "https://ebrestweb.edspay.com/";
    private static final String h5ServerPreview = "https://yh5.51fanbei.com/";
    private static final String h5ServerTest = "http://testweb.dushiebei.com/";

    private static final String fileServerTest = "http://testrest.dushiebei.com";
    private static final String fileServerPreview = "http://172.20.20.166:9002/";
    private static final String fileServerRelease = "https://ebrest.edspay.com";

    public EbeiLocationService ebeiLocationService;


    public static EbeiApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

//        EbeiConfig.init(this);
        EbeiConfig.setEbeiServerProvider(this);
        String token = EbeiSPUtil.getString(EbeiConstant.APP_TOKEN, "");
        if (EbeiMiscUtils.isEmpty(token)) {
            EbeiConfig.setEbeiAccountProvider(new EbeiUnLandAccountProvider());
        } else {
            EbeiConfig.setEbeiAccountProvider(new EbeiLandAccountProvider
                    .Builder()
                    .setUserToken(token)
                    .setUserName(EbeiSPUtil.getString(EbeiConstant.APP_PHONE, ""))
                    .build());
        }
        //同盾风控
        try {
            String env = FMAgent.ENV_PRODUCTION;
            if (EbeiConfig.isDebug()) {
                env = FMAgent.ENV_SANDBOX;
            }
            FMAgent.init(this, env);
        } catch (FMException e) {
            e.printStackTrace();
        }
        //白骑士初始化
        EbeiBqsUtils.initBqsSdk(this);
        EbeiMXAuthUtils.initSDK(this);
        //百度定位配置开始
        ebeiLocationService = new EbeiLocationService(getApplicationContext());
        //百度定位配置结束
        isLogin = EbeiSPUtil.getBoolean(EbeiConstant.APP_ISLOGIN, false);
    }

    @Override
    public String getUserName() {
        return EbeiMiscUtils.isEmpty(EbeiSPUtil.getString(EbeiConstant.APP_PHONE, "")) ? EbeiInfoUtils.getDeviceId() : EbeiSPUtil.getString(EbeiConstant.APP_PHONE, "");
    }

    @Override
    public String getUserToken() {
        return EbeiMiscUtils.isEmpty(EbeiSPUtil.getString(EbeiConstant.APP_TOKEN, "")) ? "" : String.valueOf(EbeiSPUtil.getValue(EbeiConstant.APP_TOKEN));
    }

    @Override
    public String getAppServer() {
        if (BuildConfig.EBEI_DEBUG) {
            return appServerTest;
        } else if (BuildConfig.PREVIEW) {
            return appServerPreview;
        }
        return appServerRelease;
    }

    @Override
    public String getImageServer() {
        if (BuildConfig.EBEI_DEBUG) {
            return fileServerTest;
        } else if (BuildConfig.PREVIEW) {
            return fileServerPreview;
        }
        return fileServerRelease;
    }

    @Override
    public String getH5Server() {
        if (BuildConfig.EBEI_DEBUG) {
            return h5ServerTest;
        } else if (BuildConfig.PREVIEW) {
            return h5ServerPreview;
        }
        return h5Server;
    }
}
