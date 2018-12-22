package com.ald.ebei.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ald.ebei.BuildConfig;
import com.ald.ebei.R;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.model.EbeiLoginModel;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.network.interceptor.EbeiBasicParamsInterceptor;
import com.ald.ebei.receiver.EbeiApiReceiver;
import com.ald.ebei.receiver.EbeiGestureLockWatcher;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiBqsUtils;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiMXAuthUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiInfoUtils;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiSPUtil;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.baiduloc.EbeiLocationService;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/3
 * 描述：
 * 此类必须在程序刚刚启动的时候注册，这样在程序的
 * 运行中就可以通过此类来获取全局的Application了
 * 最好的实现是自定义一个Application的子类，然后在此子类
 * 的初始化中注册。
 * 修订历史：
 * Modified By ywd on 2018-06-13
 */
public class EbeiConfig {
    public static final String ACTION_API_OPEN = "com.ald.eca.api.err.open";
    public static final String EXTRA_ERR_CODE = "__extra__api_err_code__";
    public static final String EXTRA_ERR_MSG = "__extra__api_err_msg__";
    private static final String ECA_CORE_SHARED_PREFERENCE_DATA = "_sp.fw.core.config_";
    private static final String TAG_INIT = "ECA_INIT_ERR";//初始化失败TAG
    private static WeakReference<Activity> currentActivity;// 当前正在显示的Activity
    private static boolean debug = true;//是否在调试模式下
    private static Context context;// 必须需要显式设置
    // 系统全局的线程池，Application启动的时候创建，不需要销毁
    private static ExecutorService es;
    private static Handler handler;// 主线程的handler，用于方便post一些事情做主线程去做
    private static EbeiActivityLeavedLongListener ebeiActivityLeavedLongListener;
    private static EbeiUserCityProvider ebeiUserCityProvider;
    private static EbeiServerProvider ebeiServerProvider;
    private static EbeiAccountProvider ebeiAccountProvider;
    private static EbeiInitListener ebeiInitListener;
    private static LocalBroadcastManager localBroadcastManager;
    //用于监听APP是否到后台
    private static EbeiGestureLockWatcher watcher;
    //用户是否登录
    private static boolean isLand = false;
    //是否已经初始化
    private static boolean isInit = false;

    private static final String appServerTest = "http://testrest.dushiebei.com/";
    //    private static final String appServerTest = "http://172.20.20.88:9001/";
    private static final String appServerPreview = EbeiConstant.S_PREVIEW;
    private static final String appServerRelease = "https://ebrest.edspay.com/";
    public static boolean isLogin = false;
    private static boolean sdkDebug = false;

    private static final String h5Server = "https://ebrestweb.edspay.com/";
    private static final String h5ServerPreview = "https://yh5.51fanbei.com/";
    private static final String h5ServerTest = "http://testweb.dushiebei.com/";

    private static final String fileServerTest = "http://testrest.dushiebei.com";
    private static final String fileServerPreview = "http://172.20.20.166:9002/";
    private static final String fileServerRelease = "https://ebrest.edspay.com";

    private static EbeiLocationService ebeiLocationService;


    /**
     * 初始化
     *
     * @param context 上下文,必传
     */
    public static void init(@NonNull Context context, @NonNull String phoneNum) {
        if (context == null) {
            Log.e(TAG_INIT, "Ebei初始化失败,上下文参数不能为空");
            return;
        }
        EbeiConfig.context = context.getApplicationContext();
        if (!EbeiAppUtils.isPhoneNo(phoneNum)) {
            Log.e(TAG_INIT, EbeiConfig.getContext().getResources().getString(R.string.ebei_err_init_phone));
            return;
        }
        setDebug(BuildConfig.EBEI_DEBUG);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
//        // 首先是生成线程池，最多10个线程,最少1个，闲置1分钟后线程退出
        es = Executors.newFixedThreadPool(10);
//        // 调用此方法触发保存的动作
        getFirstLaunchTime();

        handler = new Handler(Looper.getMainLooper());
        getLocalBroadcastManager().registerReceiver(new EbeiApiReceiver(),
                EbeiApiReceiver.INTENT_FILTER_OPEN);

        //同盾风控
        try {
            String env = FMAgent.ENV_PRODUCTION;
            if (EbeiConfig.isDebug()) {
                env = FMAgent.ENV_SANDBOX;
            }
            FMAgent.init(EbeiConfig.context, env);
        } catch (FMException e) {
            e.printStackTrace();
        }
        //白骑士初始化
        EbeiBqsUtils.initBqsSdk((Application) EbeiConfig.context);
        EbeiMXAuthUtils.initSDK((Application) EbeiConfig.context);
        //百度定位配置开始
        ebeiLocationService = new EbeiLocationService(EbeiConfig.context);

        setEbeiServerProvider(new EbeiServerProvider() {
            @Override
            public String getAppServer() {
                if (isDebug() || sdkDebug) {
                    return appServerTest;
                }
                return appServerRelease;
            }

            @Override
            public String getImageServer() {
                if (isDebug() || sdkDebug) {
                    return fileServerTest;
                }
                return fileServerRelease;
            }

            @Override
            public String getH5Server() {
                if (BuildConfig.EBEI_DEBUG) {
                    return h5ServerTest;
                }
                return h5Server;
            }
        });

        setEbeiAccountProvider(new EbeiAccountProvider() {
            @Override
            public String getUserName() {
                return phoneNum;
            }

            @Override
            public String getUserToken() {
                return "";
            }
        });

//        JSONObject reqObj = new JSONObject();
//        reqObj.put("mobile", phoneNum);
//        reqObj.put("osType", "android_" + Build.VERSION.RELEASE);
//        reqObj.put("phoneType", Build.MODEL);
//        Call<EbeiLoginModel> call = EbeiClient.getService(EbeiApi.class).inForEds(reqObj);
//        EbeiNetworkUtil.showCutscenes(EbeiConfig.getContext(), call);
//        call.enqueue(new EbeiRequestCallBack<EbeiLoginModel>() {
//            @Override
//            public void onSuccess(Call<EbeiLoginModel> call, Response<EbeiLoginModel> response) {
//                if (response.body() != null) {
//
//                } else {
//                    EbeiUIUtils.showToast(EbeiConfig.getContext().getResources().getString(R.string.ebei_credit_promote_phone_err));
//                }
//            }
//        });

        JSONObject reqObj = new JSONObject();
        reqObj.put("mobile", phoneNum);
        reqObj.put("osType", "android_" + Build.VERSION.RELEASE);
        reqObj.put("phoneType", Build.MODEL);
        EbeiClient.getService(EbeiApi.class)
                .inForEds(reqObj)
                .compose(EbeiRxUtils.<EbeiLoginModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiLoginModel>() {
                    @Override
                    public void onNext(EbeiLoginModel loginModel) {
                        super.onNext(loginModel);
                        if (loginModel != null) {
                            String token = loginModel.getToken();
                            setEbeiAccountProvider(new EbeiAccountProvider() {
                                @Override
                                public String getUserName() {
                                    return phoneNum;
                                }

                                @Override
                                public String getUserToken() {
                                    return token;
                                }
                            });
                        }
                        EbeiConfig.updateLand(true);
                        setIsInit(true);
                        if (getEbeiInitListener() != null) {
                            ebeiInitListener.success();
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                        EbeiConfig.updateLand(false);
                        setIsInit(false);
                    }
                });
    }

    /**
     * 清空用户信息
     */
    public static void clearUserInfo() {
        setEbeiAccountProvider(new EbeiAccountProvider() {
            @Override
            public String getUserName() {
                return "";
            }

            @Override
            public String getUserToken() {
                return "";
            }

        });
        setIsInit(false);
    }

    public static Activity getCurrentActivity() {
        return currentActivity != null ? currentActivity.get() : null;
    }

    public static void setCurrentActivity(Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    public static LocalBroadcastManager getLocalBroadcastManager() {
        return localBroadcastManager;
    }

    public static <T> Future<T> submit(Callable<T> call) {
        return es.submit(call);
    }

    public static void postOnUiThread(Runnable task) {
        handler.post(task);
    }

    public static void postDelayOnUiThread(Runnable task, long delay) {
        handler.postDelayed(task, delay);
    }

    public static void execute(Runnable task) {
        es.execute(task);
    }

    public static void removeCallbacks(Runnable task) {
        handler.removeCallbacks(task);
    }

    public static int addLaunchVersionCount() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        String key = "lc" + EbeiInfoUtils.getVersionCode();

        int count = prefs.getInt(key, 0) + 1;
        Editor editor = prefs.edit();
        editor.putInt(key, count);
        editor.commit();
        return count;
    }

    public static int getLaunchVersionCount() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        String key = "lc" + EbeiInfoUtils.getVersionCode();
        return prefs.getInt(key, 0);
    }

    public static int addLaunchCount() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        int count = prefs.getInt("lc", 0) + 1;
        Editor editor = prefs.edit();
        editor.putInt("lc", count);
        editor.commit();
        return count;
    }

    public static int getLaunchCount() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        return prefs.getInt("lc", 0);
    }

    public static long getLastAdTime() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        return prefs.getLong("lastATime", -1L);
    }

    public static void updateLastAdTime() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong("lastATime", System.currentTimeMillis());
        editor.commit();
    }

    public static long getLastPauseTime() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        return prefs.getLong("lastPauseTime", -1L);
    }

    public static void updateLastPauseTime() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong("lastPauseTime", System.currentTimeMillis());
        editor.commit();
    }

    /**
     * 获取第一次启动时间，此方法永远不会返回null
     * ，返回的格式是 yyyy-MM-dd HH:mm:ss
     */
    public static String getFirstLaunchTime() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        String firstTime = prefs.getString("ft", "");
        if (EbeiMiscUtils.isEmpty(firstTime)) {
            firstTime = EbeiMiscUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            Editor editor = prefs.edit();
            editor.putString("ft", firstTime);
            editor.commit();
        }
        return firstTime;
    }

    public static boolean getTagAlias() {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        boolean state = prefs.getBoolean("tagalias", false);
        return state;
    }

    /**
     * 设置推送的别名和Tag是否设置成功状态
     *
     * @param state
     */
    public static void setTagAlias(boolean state) {
        SharedPreferences prefs = context.getSharedPreferences(ECA_CORE_SHARED_PREFERENCE_DATA, Application.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean("tagalias", state);
        editor.commit();
    }

    public static boolean isDebug() {
        return debug;
    }

    private static void setDebug(boolean debug) {
        EbeiConfig.debug = debug;
    }

    public static String getPackageName() {
        Context context = getContext();
        if (context != null) {
            return context.getPackageName();
        }
        return null;
    }

    public static Context getContext() {
        return context;
    }

    public static EbeiGestureLockWatcher getWatcher() {
        return watcher;
    }

    public static EbeiActivityLeavedLongListener getEbeiActivityLeavedLongListener() {
        return ebeiActivityLeavedLongListener;
    }

    public static void setEbeiActivityLeavedLongListener(EbeiActivityLeavedLongListener ebeiActivityLeavedLongListener) {
        EbeiConfig.ebeiActivityLeavedLongListener = ebeiActivityLeavedLongListener;
    }

    public static EbeiUserCityProvider getEbeiUserCityProvider() {
        return ebeiUserCityProvider;
    }

    public static void setEbeiUserCityProvider(EbeiUserCityProvider ebeiUserCityProvider) {
        EbeiConfig.ebeiUserCityProvider = ebeiUserCityProvider;
    }

    public static EbeiServerProvider getEbeiServerProvider() {
        return ebeiServerProvider;
    }

    public static void setEbeiServerProvider(EbeiServerProvider ebeiServerProvider) {
        EbeiConfig.ebeiServerProvider = ebeiServerProvider;
    }

    public static EbeiAccountProvider getEbeiAccountProvider() {
        return ebeiAccountProvider;
    }

    public static void setEbeiAccountProvider(EbeiAccountProvider ebeiAccountProvider) {
        EbeiConfig.ebeiAccountProvider = ebeiAccountProvider;
        //EbeiClient.getInstance().updateRetrofit(new EbeiBasicParamsInterceptor.Builder().build());
    }

    public static EbeiInitListener getEbeiInitListener() {
        return ebeiInitListener;
    }

    public static void setEbeiInitListener(EbeiInitListener ebeiInitListener) {
        EbeiConfig.ebeiInitListener = ebeiInitListener;
    }

    /**
     * 更新登录状态
     * 必须在 SharedPreferences 之后
     * isLand  true 表示登录， false 表示未登录
     */
    public static void updateLand(boolean isLand) {
        setLand(isLand);
        EbeiClient.getInstance().updateRetrofit(new EbeiBasicParamsInterceptor.Builder().build());
    }

    public static boolean isLand() {
        return isLand || EbeiSPUtil.getBoolean(EbeiConstant.APP_ISLOGIN, false);
    }

    public static void setLand(boolean land) {
        isLand = land;
        EbeiSPUtil.setValue(EbeiConstant.APP_ISLOGIN, land);
        if (!isLand) {
            setTagAlias(false);
        }
    }

    public static boolean isInit() {
        return isInit;
    }

    private static void setIsInit(boolean isInit) {
        EbeiConfig.isInit = isInit;
    }

    public static void debug(boolean debug) {
        sdkDebug = debug;
    }
}
