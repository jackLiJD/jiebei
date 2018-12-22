package com.ald.ebei.config;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;


import com.ald.ebei.util.EbeiDataUtils;
import com.ald.ebei.util.EbeiInfoUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiSHAUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 12:59
 * 描述：
 * 修订历史：
 */
public class EbeiUrl {
    private static Map<String, UrlValueProvider> providerMap = new LinkedHashMap<String, UrlValueProvider>();
    public static String USER_NAME = "userName";
    public static String SIGN = "sign";

    public static void registerParam(String key, UrlValueProvider provider) {
        providerMap.put(key, provider);
    }

    private static String buildApiUrlPart(String webViewVersion, Map<String, String> externalParams,
                                          JSONObject configJson) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> map = createStandardParams(webViewVersion);
        for (Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (EbeiMiscUtils.isNotEmpty(externalParams)) {
            for (Entry<String, String> entry : externalParams.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        if (configJson != null) {
            sb.append("&_config=").append(EbeiMiscUtils.safeURLEncode(configJson.toString(), "UTF-8"));
        }
        return sb.toString();
    }

    private static String buildWebViewUrlPart(String webViewVersion, Map<String, String> externalParams, JSONObject configJson) {
        String sign = "";
        String userName = "";
        StringBuilder sb = new StringBuilder();
        if (EbeiMiscUtils.isNotEmpty(externalParams)) {
            for (Entry<String, String> entry : externalParams.entrySet()) {
                if (EbeiMiscUtils.isNotEmpty(sign) && EbeiMiscUtils.isNotEmpty(userName)) {
                    break;
                }
                if ("sign".equals(entry.getKey())) {
                    sign = entry.getValue();
                } else if ("userName".equals(entry.getKey())) {
                    userName = entry.getValue();
                }
            }
        }
        JSONObject appInfo = new JSONObject(createStandardParams(userName, sign));
        sb.append("_appInfo=").append(EbeiMiscUtils.safeURLEncode(appInfo.toString(), "UTF-8"));
        if (configJson != null) {
            sb.append("&_config=").append(EbeiMiscUtils.safeURLEncode(configJson.toString(), "UTF-8"));
        }
        return sb.toString();
    }


    /**
     * 构造url，带上基础参数。
     *
     * @param webViewVersion    协议版本号，目前为4.3
     * @param apiExternalParams 访问API时需要增加的额外参数
     * @param apiRequest        是否是访问服务器api，false则表示为webview访问网址
     * @param configJson        客户端配置信息
     */
    public static void buildJsonUrl(StringBuilder sb, String webViewVersion, Map<String, String> apiExternalParams,
                                    boolean apiRequest, JSONObject configJson) {
        if (apiRequest) {
            if (sb.indexOf("?") == -1) {
                sb.append("?");
            } else if (sb.indexOf("?") != sb.length() - 1) {
                sb.append("&");
            }
            sb.append(buildApiUrlPart(webViewVersion, apiExternalParams, configJson));
        } else {
            if (sb.indexOf("?") == -1) {
                sb.append("?");
            } else if (sb.indexOf("?") != sb.length() - 1) {
                sb.append("&");
            }
            sb.append(buildWebViewUrlPart(webViewVersion, apiExternalParams, configJson));
        }
    }

    private static String getSign(String userName, String sign, long time) {

        String shaString = "appVersion=" + EbeiInfoUtils.getVersionCode() + "&netType=" + EbeiInfoUtils.getNetworkType() +
                "&time=" + time + "&userName=" + userName + sign;
        try {
            String signKey = EbeiSHAUtil.shaEncode(shaString);
            return signKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private static HashMap<String, String> createStandardParams(String webViewVersion) {
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        String version = EbeiInfoUtils.getVersionName();
        String name = EbeiInfoUtils.getDeviceName();
        long time = System.currentTimeMillis();
        String id = EbeiInfoUtils.getRequestId(time+"");
        String net = EbeiInfoUtils.getNetworkName();
        int prid = EbeiMiscUtils.getResourcesIdentifier(EbeiConfig.getContext(), "string/product");
        String pr = EbeiConfig.getContext().getResources().getString(prid);
        String appName = EbeiInfoUtils.getAppName();
        DisplayMetrics metrics = EbeiDataUtils.getCurrentDisplayMetrics();
        map.put("id", id);
        map.put("time", String.valueOf(time));
        map.put("platform", "android");
        map.put("appName", EbeiMiscUtils.safeURLEncode(pr, "UTF-8"));
        map.put("product", EbeiMiscUtils.safeURLEncode(appName, "UTF-8"));
        map.put("appVersion", EbeiMiscUtils.safeURLEncode(version, "UTF-8"));
        map.put("systemVersion", EbeiMiscUtils.safeURLEncode(Build.VERSION.RELEASE, "UTF-8"));
        map.put("device", EbeiMiscUtils.safeURLEncode(name, "UTF-8"));
        map.put("operator", EbeiMiscUtils.safeURLEncode(net, "UTF-8"));
        map.put("netType", EbeiMiscUtils.safeURLEncode(EbeiInfoUtils.getNetworkType(), "UTF-8"));
        map.put("pkgName", EbeiMiscUtils.safeURLEncode(EbeiConfig.getPackageName(), "UTF-8"));
        map.put("screenDpi", EbeiMiscUtils.safeURLEncode(String.valueOf(metrics.density), "UTF-8"));
        map.put("screenWidth", EbeiMiscUtils.safeURLEncode(String.valueOf(metrics.widthPixels), "UTF-8"));
        map.put("screenHeight", EbeiMiscUtils.safeURLEncode(String.valueOf(metrics.heightPixels), "UTF-8"));
        map.put("launch", EbeiMiscUtils.safeURLEncode(String.valueOf(EbeiConfig.getLaunchCount()), "UTF-8"));
        map.put("webviewVersion", EbeiMiscUtils.safeURLEncode(webViewVersion, "UTF-8"));
        map.put("firstTime", EbeiMiscUtils.safeURLEncode(EbeiConfig.getFirstLaunchTime(), "UTF-8"));

        for (Entry<String, UrlValueProvider> entry : providerMap.entrySet()) {
            String value = entry.getValue().getValue();
            if (value != null) {
                map.put(entry.getKey(), EbeiMiscUtils.safeURLEncode(value, "UTF-8"));
            }
        }
        return map;
    }

    private static HashMap<String, String> createStandardParams(String userName, String sign) {
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        long time = System.currentTimeMillis();
        String id = EbeiInfoUtils.getRequestId(time+"");
        int version = EbeiInfoUtils.getVersionCode();
        map.put("id", id);
        map.put("time", String.valueOf(time));
        map.put(SIGN, getSign(userName, sign, time));
        map.put(USER_NAME, EbeiMiscUtils.safeURLEncode(userName, "UTF-8"));
        map.put("netType", EbeiMiscUtils.safeURLEncode(EbeiInfoUtils.getNetworkType(), "UTF-8"));
        map.put("appVersion", EbeiMiscUtils.safeURLEncode(String.valueOf(version), "UTF-8"));
        for (Entry<String, UrlValueProvider> entry : providerMap.entrySet()) {
            String value = entry.getValue().getValue();
            if (value != null) {
                map.put(entry.getKey(), EbeiMiscUtils.safeURLEncode(value, "UTF-8"));
            }
        }
        return map;
    }

    private static String getP() {
        String p = EbeiMiscUtils.getSharepreferenceValue("_pppp_", "pp", "");
        if (EbeiMiscUtils.isEmpty(p)) {
            p = fetchP();
            p = encodeP(p);
            if (EbeiMiscUtils.isNotEmpty(p)) {
                EbeiMiscUtils.setSharedPreferenceValue("_pppp_", "pp", p);
            }
        }
        return p;
    }

    private static String fetchP() {
        TelephonyManager telManager = (TelephonyManager) EbeiConfig.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String number = telManager.getLine1Number();
        if (EbeiMiscUtils.isEmpty(number) == false && number.length() >= 11) {
            return number;
        }
        return "";
    }

    private static String encodeP(String p) {
        if (EbeiMiscUtils.isEmpty(p)) {
            return "";
        }
        try {
            byte[] data = p.getBytes("UTF-8");
            byte[] mask = "ala.tech".getBytes("UTF-8");
            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (byte) (data[i] ^ mask[i % mask.length]);
            }
            StringBuilder dist = new StringBuilder();
            for (byte b : result) {
                dist.append(String.format("%02X", b));
            }
            p = dist.toString();
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";

    }

    /**
     * URL的值提供器，可以在运行的时候注册，最好是在应用启动的时候注册。
     */
    public static interface UrlValueProvider {
        public String getValue();

    }

}
