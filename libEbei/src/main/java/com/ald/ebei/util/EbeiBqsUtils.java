package com.ald.ebei.util;

import android.app.Application;

import com.bqs.risk.df.android.BqsDF;
import com.bqs.risk.df.android.BqsParams;

/**
 * 白骑士初始化工具类
 * Created by ywd on 2018/1/12.
 */

public class EbeiBqsUtils {
    private static final String TAG = "白骑士SDK";
    private static final String ERROR_MESSAGE = "白骑士SDK，初始化错误";

    /**
     * 初始化
     *
     * @param application
     */
    public static void initBqsSdk(Application application) {
        //必要参数：
        BqsParams params = new BqsParams();
        params.setPartnerId("fanbei");//商户合作编号
        //可选参数：
        params.setGatherCallRecord(true);//是否采集通话记录,true:采集,false：不采集(默认值)
        params.setGatherContact(true);//是否采集通讯录信息,true:采集,false：不采集(默认值)
        params.setGatherGps(true);//是否采集GPS信息,true:采集(默认值) false:不采集
        params.setGatherBaseStation(true);//是否采集基站信息,true:采集(默认值) false:不采集
        params.setTestingEnv(false);//设置是否连接到白骑士测试环境，true:测试环境，false:生产环境
        params.setGatherSensorInfo(true);//是否采集传感器信息,true：采集（默认值） false:不采集
        params.setGatherInstalledApp(true);//是否采集已安装的app名称, true：采集（默认值） false:不采集
        BqsDF.initialize(application, params);
    }
}
