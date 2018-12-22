package com.ald.ebei.network;

import com.ald.ebei.BuildConfig;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.network.converter.EbeiFWFastJsonConverterFactory;
import com.ald.ebei.network.interceptor.EbeiAppealInterceptor;
import com.ald.ebei.network.interceptor.EbeiBasicParamsInterceptor;
import com.ald.ebei.network.interceptor.EbeiHttpLoggingInterceptor;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.log.EbeiLogger;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述：网络请求client
 * 修订历史：
 */
public class EbeiClient {
    // 网络请求超时时间值(s)
    private static final int DEFAULT_TIMEOUT = 120;
    // retrofit实例
    private static Retrofit retrofit;


    public static Retrofit getRetrofit() {
        if (instance == null)
            instance = new EbeiClient("");
        return retrofit;
    }

    /**
     * 私有化构造方法
     */
    private EbeiClient(String user) {
        if (EbeiMiscUtils.isEmpty(user))
            updateRetrofit(new EbeiBasicParamsInterceptor.Builder().build());
        else {
            //替换header
            updateRetrofit(new EbeiAppealInterceptor.Builder(user).build());
        }
    }


    /**
     * 调用单例对象
     */
    private static EbeiClient instance;

    private static EbeiClient appealInstance;


    public static EbeiClient getInstance() {
        if (instance == null)
            instance = new EbeiClient("");

        return instance;
    }

    public static EbeiClient getInstance(String userName) {
        if (appealInstance == null) {
            appealInstance = new EbeiClient(userName);
        }
        return appealInstance;
    }

    public void updateRetrofit(Interceptor interceptor) {
        // 创建一个OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置网络请求超时时间
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(2, TimeUnit.MINUTES);
        builder.writeTimeout(2, TimeUnit.MINUTES);
//        // 添加签名等基础参数
        builder.addInterceptor(interceptor);
        // 打印参数
        EbeiHttpLoggingInterceptor ebeiHttpLoggingInterceptor = new EbeiHttpLoggingInterceptor();
        if (BuildConfig.LOG_DEBUG) {
            ebeiHttpLoggingInterceptor.setLevel(EbeiHttpLoggingInterceptor.Level.BODY);
        } else {
            ebeiHttpLoggingInterceptor.setLevel(EbeiHttpLoggingInterceptor.Level.NONE);
        }
//        if (BuildConfig.DEBUG) {
//            builder.addInterceptor(new EbeiLoggingInterceptor());
//        }
//
        builder.addInterceptor(ebeiHttpLoggingInterceptor);


        retrofit = new Retrofit.Builder()
                .baseUrl(EbeiConfig.getEbeiServerProvider().getAppServer())
                .client(builder.build())
                .addConverterFactory(EbeiFWFastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        getServiceMap().clear();
    }


    ///////////////////////////////////////////////////////////////////////////
    // service
    ///////////////////////////////////////////////////////////////////////////
    private static TreeMap<String, Object> serviceMap;

    private static TreeMap<String, Object> getServiceMap() {
        EbeiLogger.d("logger", "logger++1" + serviceMap);
        if (serviceMap == null)
            serviceMap = new TreeMap<>();
        return serviceMap;
    }

    /**
     * @return 指定service实例
     */
    public static <T> T getService(Class<T> clazz) {
        EbeiLogger.d("logger", "logger++2" + getServiceMap().containsKey(clazz.getSimpleName()));
        if (getServiceMap().containsKey(clazz.getSimpleName())) {
            return (T) getServiceMap().get(clazz.getSimpleName());
        }
        T service = EbeiClient.getInstance().retrofit.create(clazz);
        getServiceMap().put(clazz.getSimpleName(), service);
        return service;
    }

    public static <T> T getNewService(Class<T> clazz) {
        // 创建一个OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置网络请求超时时间
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(2, TimeUnit.MINUTES);
        builder.writeTimeout(2, TimeUnit.MINUTES);
//        // 添加签名等基础参数
        builder.addInterceptor(new EbeiBasicParamsInterceptor.Builder().build());
        // 打印参数
        EbeiHttpLoggingInterceptor ebeiHttpLoggingInterceptor = new EbeiHttpLoggingInterceptor();
        if (BuildConfig.LOG_DEBUG) {
            ebeiHttpLoggingInterceptor.setLevel(EbeiHttpLoggingInterceptor.Level.BODY);
        } else {
            ebeiHttpLoggingInterceptor.setLevel(EbeiHttpLoggingInterceptor.Level.NONE);
        }
//        if (BuildConfig.DEBUG) {
//            builder.addInterceptor(new EbeiLoggingInterceptor());
//        }
//
        builder.addInterceptor(ebeiHttpLoggingInterceptor);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.156.91:9001/")
                .client(builder.build())
                .addConverterFactory(EbeiFWFastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        EbeiLogger.d("logger", "logger++2" + getServiceMap().containsKey(clazz.getSimpleName()));
        if (getServiceMap().containsKey(clazz.getSimpleName())) {
            return (T) getServiceMap().get(clazz.getSimpleName());
        }
        T service = retrofit.create(clazz);
        getServiceMap().put(clazz.getSimpleName(), service);
        return service;
    }


    /**
     * @return 指定service实例(免登)
     */
    public static <T> T getServiceLogout(Class<T> clazz, String userName) {
        EbeiLogger.d("logger", "logger++2" + getServiceMap().containsKey(clazz.getSimpleName()));
        T service = EbeiClient.getInstance(userName).retrofit.create(clazz);
        getServiceMap().put(clazz.getSimpleName(), service);
        return service;
    }


}
