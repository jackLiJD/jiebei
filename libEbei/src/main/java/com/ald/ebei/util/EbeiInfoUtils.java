package com.ald.ebei.util;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.util.encryption.EbeiMD5Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述：info工具类
 * 修订历史：
 */
public class EbeiInfoUtils {

    private static Bundle applicationInfoMetadata;
    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static UUID uuid;
    private static String versionName;

    public static String getQudao() {
        return String.valueOf(getApplicationInfoMetadata().getString("qudao"));
    }

    public static String getRenyuan() {
        return String.valueOf(getApplicationInfoMetadata().getString("renyuan"));
    }

    public static String getAppName() {
        try {
            ApplicationInfo info = EbeiConfig.getContext().getPackageManager()
                    .getApplicationInfo(EbeiConfig.getContext().getPackageName(), PackageManager.GET_META_DATA);
            CharSequence name = info.loadLabel(EbeiConfig.getContext().getPackageManager());
            return String.valueOf(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "alasoft";
    }

    /**
     * 获取APP的版本名
     */
    public static String getVersionName() {
        try {
            versionName = EbeiConfig.getContext().getPackageManager()
                    .getPackageInfo(EbeiConfig.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 先取保存的版本号,如果取不到，则取系统的版本号
     */
    public static int getVersionCode() {
        int versionCode = 100;
        try {
            String versionName = EbeiConfig.getContext().getPackageManager()
                    .getPackageInfo(EbeiConfig.getContext().getPackageName(), 0).versionName;
            if (EbeiMiscUtils.isNotEmpty(versionName)) {
                String[] version = versionName.split("\\.");
                StringBuilder versionString = new StringBuilder();
                for (int i = 0; i < version.length; i++) {
                    versionString.append(version[i]);
                }
                if (EbeiMiscUtils.isNotEmpty(versionString.toString())) {
                    versionCode = Integer.valueOf(versionString.toString());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return versionCode;
        }
    }


    /**
     * 获取手机系统版本号
     */
    public static String getDeviceVersion() {
        return "android" + Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceType() {
        return Build.MODEL;
    }

    /**
     * 获取手机系统版本号
     */
    public static int getCompilingVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getSystem() {
        return Build.ID;
    }

    public static String getDeviceName() {
        return Build.MODEL;
    }

    /**
     * 返回当前的网络类型，可能是wifi,g2,g3,none
     */
    public static String getNetworkType() {
        String netWorkInfo = "unknown";
        if (EbeiInfoUtils.isConnectAvailable()) {
            ConnectivityManager manager = (ConnectivityManager) EbeiConfig.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    netWorkInfo = "WIFI";
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN: {
                            netWorkInfo = "2G";
                        }
                        break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                        case TelephonyManager.NETWORK_TYPE_UMTS: {
                            netWorkInfo = "3G";
                        }
                        break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            netWorkInfo = "4G";
                            break;
                        default: {
                            netWorkInfo = "2G";
                        }
                    }
                }
            }
        }
        return netWorkInfo;
    }

    /**
     * 判断当前是否有网络
     */
    public static boolean isConnectAvailable() {
        ConnectivityManager cm = (ConnectivityManager) EbeiConfig.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        }
        return false;
    }

    /**
     * 获取手机运营商类型
     */
    public static String getNetworkName() {
        TelephonyManager phone = (TelephonyManager) EbeiConfig.getContext().getSystemService(TELEPHONY_SERVICE);
        String key = phone.getNetworkOperatorName();
        if (key != null) {
            key = key.toUpperCase(Locale.ENGLISH);
        } else {
            return "UNKOWN";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("CHINA MOBILE", "M");
        map.put("中国移动", "M");
        map.put("CMCC", "M");
        map.put("CHINA UNICOM", "C");
        map.put("中国联通", "C");
        map.put("CHINA TELECOM", "T");
        map.put("中国电信", "T");
        String net = map.get(key);
        if (net == null) {
            String imsi = phone.getSubscriberId();
            if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                    net = "M";
                } else if (imsi.startsWith("46001")) {
                    net = "C";
                } else if (imsi.startsWith("46003")) {
                    net = "T";
                }
            }
            if (net == null) {
                net = key;
            }
        }
        return net;

    }


    /**
     * 获取唯一消息ID
     * 同后台约定好的格式
     */
    public static String getRequestId() {
        return "a_" + getDeviceId() + "_" + System.currentTimeMillis();
    }


    /**
     * 获取唯一消息ID
     * 同后台约定好的格式
     */
    public static String getRequestId(String ts) {
        String requestId = "a_" + getDeviceId() + "_" + ts + "_";
        String packageName = EbeiConfig.getContext().getPackageName();
        if (EbeiMiscUtils.isNotEmpty(packageName)) {
            String mark = packageName.substring(packageName.lastIndexOf(".")).replace(".", "");
            return requestId + "app_" + mark;
        }
        return requestId + "app_" + packageName;
    }

    /**
     * 唯一的设备ID GSM手机的 IMEI 和 CDMA手机的 MEID.
     */
    public static String getDeviceId() {
        String deviceId = "";
        TelephonyManager tm = (TelephonyManager) EbeiConfig.getContext().getSystemService(TELEPHONY_SERVICE);
        try {
            /*deviceId = tm.getDeviceId();
            if (EbeiMiscUtils.isEmpty(deviceId) || deviceId.length() <= 5) {
                final SharedPreferences prefs = EbeiConfig.getContext().getSharedPreferences(PREFS_FILE, 0);
                final String id = prefs.getString(PREFS_DEVICE_ID, null);
                if (EbeiMiscUtils.isNotEmpty(id)) {
                    return id;
                } else {
                    deviceId = generateUniqueDeviceId();
                    prefs.edit().putString(PREFS_DEVICE_ID, deviceId).commit();
                }
                return deviceId;
            } else {
                //某些手机获取的deviceId为000000000000
                if (EbeiMiscUtils.isNumeric(deviceId)) {
                    if (Long.valueOf(deviceId).longValue() == 0) {
                        deviceId = generateUniqueDeviceId();
                    }
                }
                return deviceId;
            }*/
            final SharedPreferences prefs = EbeiConfig.getContext().getSharedPreferences(PREFS_FILE, 0);
            final String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (EbeiMiscUtils.isNotEmpty(id)) {
                return id;
            }
            deviceId = generateUniqueDeviceId();
            prefs.edit().putString(PREFS_DEVICE_ID, deviceId).apply();
        } catch (Exception e) {
            deviceId = generateUniqueDeviceId();
        }
        return deviceId;
    }


    private static Bundle getApplicationInfoMetadata() {
        if (applicationInfoMetadata == null) {
            try {
                ApplicationInfo info = EbeiConfig.getContext().getPackageManager()
                        .getApplicationInfo(EbeiConfig.getContext().getPackageName(), PackageManager.GET_META_DATA);
                applicationInfoMetadata = info.metaData;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (applicationInfoMetadata == null) {
                applicationInfoMetadata = new Bundle();
            }
        }
        return applicationInfoMetadata;
    }

    /**
     * 友盟测试时deviceInfo信息
     */

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();
            // wjy: 此方法获取mac地址不兼容6.0
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成设备唯一标识：IMEI、AndroidId、macAddress 三者拼接再 MD5
     */
    public static String generateUniqueDeviceId() {
        Context context = EbeiConfig.getContext();
        String imei = "";
        String androidId = "";
        String macAddress = "";

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        try {
            if (telephonyManager != null) {
                imei = telephonyManager.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (wifiManager != null) {
                macAddress = wifiManager.getConnectionInfo().getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hardwareId = getHardwareDeviceId();
        StringBuilder longIdBuilder = new StringBuilder();
        if (EbeiMiscUtils.isNotEmpty(imei)) {
            longIdBuilder.append(imei);
        }
        if (EbeiMiscUtils.isNotEmpty(androidId)) {
            longIdBuilder.append(androidId);
        }
        if (EbeiMiscUtils.isNotEmpty(macAddress)) {
            longIdBuilder.append(macAddress);
        }
        if (EbeiMiscUtils.isNotEmpty(hardwareId)) {
            longIdBuilder.append(hardwareId);
        }
        String deviceId = EbeiMD5Util.getMD5Str(longIdBuilder.toString());
        if (EbeiMiscUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString().replace("-", "").replace(" ", "");
        }
        return deviceId;
    }


    //获得HardwareDeviceId
    public static String getHardwareDeviceId() {
        String serial = "";
        String hardwareDeviceId;
        String devIDShort = "35" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            hardwareDeviceId = new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
            return hardwareDeviceId.replace("-", "");
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        hardwareDeviceId = new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
        return hardwareDeviceId.replace("-", "");
    }


    /**
     * 友盟测试时deviceInfo信息
     */

    public static String getAndroidId(Context context) {
        String device_id = "";
        if (TextUtils.isEmpty(device_id)) {
            device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return device_id;
    }


    /**
     * 得到当前手机的IMEI
     */
    public static String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) EbeiConfig.getContext()
                .getSystemService(TELEPHONY_SERVICE);
        String imei = null;
        try {
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(imei)) {
            imei = "123";// 如果取不到，则以123这个测试的IMEI为准
        }
        return imei;
    }

    // ===================  获取连接wifi之后的mac地址,兼容6.0以上  ==============================
    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    public static String getAdresseMAC(Application context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if (wifiInf != null && marshmallowMacAddress.equals(wifiInf.getMacAddress())) {
            String result = null;
            try {
                result = getAdressMacByInterface();
                if (result != null) {
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (IOException e) {
                Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
            }
        } else {
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }
    // ====================================================================================================

}
