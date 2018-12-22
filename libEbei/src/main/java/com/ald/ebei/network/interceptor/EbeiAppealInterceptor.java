package com.ald.ebei.network.interceptor;


import com.ald.ebei.R;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.log.EbeiLogger;
import com.alibaba.fastjson.JSONObject;
import com.ald.ebei.util.EbeiInfoUtils;
import com.ald.ebei.util.EbeiSHAUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by luckyliang on 2017/11/24.
 */

public class EbeiAppealInterceptor implements Interceptor {
    public static final String APPLICATION_X_WWW_JSON_URLENCODED = "application/json; charset=utf-8";
    public static final List<String> apiConfigList = Arrays
            .asList(EbeiConfig.getContext().getResources().getStringArray(R.array.api51FbConfig)); // TODO: 2017/10/19 运行时进的是app项目中的数组 api51FbConfig

    private String mUserName = "";


    /**
     * headerLines 参数List
     */
    private List<String> headerLinesList = new ArrayList<>();
    /**
     * header 参数Map
     */
    private Map<String, String> headerParamsMap = new HashMap<>();
    /**
     * header sign
     */
    private String headerSign = "";
    /**
     * url 参数Map
     */
    private Map<String, String> queryParamsMap = new HashMap<>();
    /**
     * body 参数Map
     */
    private Map<String, String> paramsMap = new HashMap<>();
    /**
     * 动态参数加入
     */
    private EbeiIBasicDynamic ebeiIBasicDynamic;

    /**
     * 私有参数map对象
     */
//    private Map map = null;
    public EbeiAppealInterceptor(String userName) {
        mUserName = userName;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        //////////////////////////////////////////////9/////////////////////////////
        // process header params inject
        ///////////////////////////////////////////////////////////////////////////
        Headers.Builder headerBuilder = request.headers().newBuilder();
        if (headerLinesList.size() > 0) {
            for (String line : headerLinesList) {
                headerBuilder.add(line);
            }
        }
        //读取配置参数
        String fwId = EbeiConfig.getContext().getResources().getString(R.string.fw_app_id);
        String fwTime = EbeiConfig.getContext().getResources().getString(R.string.fw_app_time);
        String fwVersion = EbeiConfig.getContext().getResources().getString(R.string.fw_app_version);
        String fwNetType = EbeiConfig.getContext().getResources().getString(R.string.fw_app_network_type);
        String fwUserName = EbeiConfig.getContext().getResources().getString(R.string.fw_app_user_name);
        String fwSign = EbeiConfig.getContext().getResources().getString(R.string.fw_app_sign);
        //获取当前时间
        String ts = String.valueOf(System.currentTimeMillis());
        String requestId = EbeiInfoUtils.getRequestId(ts);
        //组装基础参数
        headerBuilder.add(fwId, requestId);
        headerBuilder.add(fwTime, ts);
        headerBuilder.add(fwVersion, String.valueOf(EbeiInfoUtils.getVersionCode()));
        headerBuilder.add(fwNetType, EbeiInfoUtils.getNetworkType());
        headerBuilder.add(fwUserName, getUserName());
        // 签名规则
        String signRule = getSignRule(fwVersion, fwNetType, fwTime, fwUserName, ts);
        if (EbeiMiscUtils.isNotEmpty(apiConfigList)) {
            String requestUrlPath = request.url().encodedPath();
            //需要token参数加密
            if (!apiConfigList.contains(requestUrlPath)) {
                signRule += getToken();
            }
        } else {
            //需要token参数加密
            signRule += getToken();
        }
        String postBodyJsonString = bodyToString(request.body());
        //如果请求没有参数
        if (EbeiMiscUtils.isEmpty(postBodyJsonString) || (request.body() != null && request
                .body() instanceof MultipartBody)) {
            postBodyJsonString = new JSONObject().toJSONString();
        } else {
            Map paramsMap = JSONObject.parseObject(postBodyJsonString);
            if (paramsMap != null && paramsMap.size() > 0) {
                Collections.synchronizedMap(paramsMap);
                List<String> paramList = new ArrayList<String>();
                paramList.addAll(paramsMap.keySet());
                if (EbeiMiscUtils.isNotEmpty(paramList)) {
                    Collections.sort(paramList);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String param : paramList) {
                        stringBuilder.append("&" + param + "=").append(paramsMap.get(param));
                    }
                    signRule += stringBuilder.toString();
                } else {
                    EbeiLogger.e("base param", "parse param list err");
                }
            } else {
                EbeiLogger.e("base param", "parse object err");
            }
        }
        EbeiLogger.d("base param", "the request id = " + requestId + " rule = " + signRule);
        try {
            String shaSignValue = EbeiSHAUtil.getDigestStr(signRule);
            EbeiLogger.d("base param", "the sign =>" + shaSignValue);
            headerBuilder.add(fwSign, shaSignValue);
        } catch (Exception e) {
            e.printStackTrace();
            EbeiLogger.e("base param", "the sign is err");
        }
        headerBuilder.add("User-Agent", getUserName());
        requestBuilder.headers(headerBuilder.build());

        ///////////////////////////////////////////////////////////////////////////
        // process queryParams inject whatever it's GET or POST
        ///////////////////////////////////////////////////////////////////////////
        if (queryParamsMap.size() > 0) {
            injectParamsIntoUrl(request, requestBuilder, queryParamsMap);
        }

        ///////////////////////////////////////////////////////////////////////////
        // process post body inject
        ///////////////////////////////////////////////////////////////////////////
        if (null != ebeiIBasicDynamic) {
            ebeiIBasicDynamic.dynamicParams(paramsMap);
        }
        if (request.method().equals("POST")) {
            if (null != request.body() && request.body() instanceof MultipartBody) {
                requestBuilder.post(request.body());
            } else {
                MediaType mediaType = MediaType.parse(APPLICATION_X_WWW_JSON_URLENCODED);
                RequestBody requestBody = RequestBody.create(mediaType, postBodyJsonString);
                requestBuilder.post(requestBody);
            }
        } else {
            // if can't inject into body, then inject into url
            injectParamsIntoUrl(request, requestBuilder, paramsMap);
        }
        request = requestBuilder.build();
        return chain.proceed(request);
    }

    // func to inject params into url
    private void injectParamsIntoUrl(Request request, Request.Builder requestBuilder, Map<String, String> paramsMap) {
        HttpUrl.Builder httpUrlBuilder = request.url().newBuilder();
        if (paramsMap.size() > 0) {
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
        }
        requestBuilder.url(httpUrlBuilder.build());
    }


    /**
     * 获取Token
     */
    private String getToken() {
        return EbeiConfig.getEbeiAccountProvider().getUserToken();
    }

    /**
     * 获取userName
     */
    private String getUserName() {
        return mUserName;
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            buffer.clear();
            if (request != null) {
                request.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

    public static class Builder {
        EbeiAppealInterceptor interceptor;
        String name = "";

        public Builder(String userName) {
            interceptor = new EbeiAppealInterceptor(userName);
        }

        public Builder addUserName(String userName) {
            name = userName;
            return this;
        }

        public Builder addParam(String key, String value) {
            interceptor.paramsMap.put(key, value);
            return this;
        }

        public Builder addParamsMap(Map<String, String> paramsMap) {
            interceptor.paramsMap.putAll(paramsMap);
            return this;
        }

        public Builder addHeaderParam(String key, String value) {
            interceptor.headerParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
            interceptor.headerParamsMap.putAll(headerParamsMap);
            return this;
        }

        public Builder addHeaderLine(String headerLine) {
            int index = headerLine.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + headerLine);
            }
            interceptor.headerLinesList.add(headerLine);
            return this;
        }

        public Builder addHeaderLinesList(List<String> headerLinesList) {
            for (String headerLine : headerLinesList) {
                int index = headerLine.indexOf(":");
                if (index == -1) {
                    throw new IllegalArgumentException("Unexpected header: " + headerLine);
                }
                interceptor.headerLinesList.add(headerLine);
            }
            return this;
        }

        public Builder addSignHeader(String signHeader) {
            interceptor.headerSign = signHeader;
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            interceptor.queryParamsMap.put(key, value);
            return this;
        }

        public Builder addQueryParamsMap(Map<String, String> queryParamsMap) {
            interceptor.queryParamsMap.putAll(queryParamsMap);
            return this;
        }

        public EbeiAppealInterceptor build() {
            return interceptor;
        }

    }

    /**
     * @return 获取基础参数
     */
    public Map<String, String> getBaseHeaderMap() {
        Map<String, String> map = new HashMap<>();
        // 时间戳
        String ts = String.valueOf(System.currentTimeMillis());
        //读取配置参数
        String fwId = EbeiConfig.getContext().getResources().getString(R.string.fw_app_id);
        String fwTime = EbeiConfig.getContext().getResources().getString(R.string.fw_app_time);
        String fwVersion = EbeiConfig.getContext().getResources().getString(R.string.fw_app_version);
        String fwNetType = EbeiConfig.getContext().getResources().getString(R.string.fw_app_network_type);
        String fwUserName = EbeiConfig.getContext().getResources().getString(R.string.fw_app_user_name);
        String fwSign = EbeiConfig.getContext().getResources().getString(R.string.fw_app_sign);
        map.put(fwId, EbeiInfoUtils.getRequestId(ts));
        map.put(fwTime, ts);
        map.put(fwVersion, String.valueOf(EbeiInfoUtils.getVersionCode()));
        map.put(fwNetType, EbeiInfoUtils.getNetworkType());
        map.put(fwUserName, getUserName());
        // 签名sha256前的规则
        String signRule = getSignRule(fwVersion, fwNetType, fwTime, fwUserName, ts);
        map.put(fwSign, signRule);
        return map;
    }

    /**
     * 接口签名生成规则
     *
     * @param ts 时间戳
     */
    public String getSignRule(String fwVersion, String fwNetType, String fwTime, String userName, String ts) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fwVersion + "=").append(EbeiInfoUtils.getVersionCode()).append("&" + fwNetType + "=")
                .append(EbeiInfoUtils.getNetworkType()).append("&" + fwTime + "=").append(ts).append("&" + userName + "=")
                .append(getUserName());
        return stringBuilder.toString();

    }

    /**
     * @return 获取 query 和 post 参数map
     */
    public Map<String, String> getParamsMap() {
        Map<String, String> map = new HashMap<>();
        map.putAll(queryParamsMap);
        map.putAll(paramsMap);
        return map;
    }

    public EbeiIBasicDynamic getIBasicDynamic() {
        return ebeiIBasicDynamic;
    }

    public void setIBasicDynamic(EbeiIBasicDynamic ebeiIBasicDynamic) {
        this.ebeiIBasicDynamic = ebeiIBasicDynamic;
    }
}
