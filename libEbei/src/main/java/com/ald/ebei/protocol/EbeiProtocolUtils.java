package com.ald.ebei.protocol;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.callphone.EbeiCallPhoneManager;
import com.ald.ebei.callphone.EbeiPhoneCallRequest;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.util.EbeiDataUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.log.EbeiLogger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:36
 * 描述：AlaWebView协议事件工具
 * 修订历史：
 */
public class EbeiProtocolUtils {

    private static final int ANIM_NONE = 0;
    private static final int ANIM_STYLE_ROTATE = 1000;
    private static final int ANIM_LEFT_OUT_RIGHT_IN = 1500;

    static final String SHARE_ACTION = "share_protocol_action";
    static final String SHARE_MESSAGE = "share_protocol_message";
    static final String SHARE_TYPE = "share_protocol_type";// 分享类型
    private static final String SHARE_NAME_SINA = "share_protocol_sina";// 新浪微博
    private static final String SHARE_NAME_WEIXIN = "share_protocol_weixin";// 微信分享
    private static final String SHARE_NAME_WEIXIN_FRIEND = "share_protocol_weixin_friend";// 微信朋友圈分享
    private static final String SHARE_NAME_QZONE = "share_protocol_qzone";// QQ空间
    private static final String SHARE_NAME_QQFRIEND = "share_protocol_qqFirend";// QQ朋友
    private static final String SHARE_NAME_QQWEIBO = "share_protocol_qqFirend";// QQ微博
    private static final String STORAGE_SHARE_NAME = "ala_storage_share_name";

    public static final String NATIVE_ACTION = "native_protocol_action";
    public static final String NATIVE_NAME = "native_protocol_name";
    public static final String NATIVE_PARAM = "native_protocol_param";
    public static final String NATIVE_CONFIG = "native_protocol_config";


    private static DownloadInterceptor downloadInterceptor;
    private static OpenNativeHandler openNativeHandler;

    /**
     * 应用程序需要注册广播接收器，用以接受分享的广播，并自行处理。
     */
    private static String handleMucangUri(final ProtocolData data) {
        if (EbeiConfig.getCurrentActivity() == null) {
            return null;
        }
        Uri uri = data.ecaUri;
        String path = uri.getPath();
        String script = uri.getQueryParameter("callback");
        Activity currentActivity = EbeiConfig.getCurrentActivity();
        if ("/hostinfo".equals(path)) {
            try {
                String name = uri.getQueryParameter("name");
                if ("mucang.version".equals(name)) {
                    if (EbeiMiscUtils.isNotEmpty(script)) {
                        JSONObject json = new JSONObject();
                        json.put(name, 4.0f);
                        JSONObject callback = new JSONObject();
                        callback.put("value", json.toString());
                        script = script.replace("$context", callback.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return script;
        }
        if ("approot.storage".equals(data.ecaUri.getAuthority())) {
            // 存储数据
            if ("/set".equals(path)) {
                String storageKey = data.ecaUri.getQueryParameter("key");
                String storageValue = data.ecaUri.getQueryParameter("value");
                EbeiMiscUtils.setSharedPreferenceValue(STORAGE_SHARE_NAME, storageKey, storageValue);
            }
        } else if ("/applet/check".equals(path)) {
            script = handleCheckInstallUri(uri, data.stateMap);
        } else if ("/applet/install".equals(uri.getPath())) {
            handleDownloadUri(uri, data.stateMap, data.bottomWebView);
            script = "";
        } else if ("/applet/start".equals(uri.getPath())) {
            handleStartUri(uri);
            script = "";
        } else if ("/show".equals(path)) {
            String timeOut = uri.getQueryParameter("timeout");
            show(data, EbeiMiscUtils.parseInt(timeOut, 0));
        } else if ("/open".equals(path)) {
            open(data);
        } else if ("/close".equals(path)) {
            EbeiConfig.postOnUiThread(new Runnable() {

                @Override
                public void run() {
                    data.bottomWebView.setVisibility(View.GONE);
                }
            });
        } else if ("/destory".equals(path)) {
            Activity current = EbeiConfig.getCurrentActivity();
            if (current != null)
                current.finish();
        } else if ("/changemode".equals(path)) {
            String mode = uri.getQueryParameter("mode");
            if (data.offLine != null) {
                if ("online".equals(mode)) {
                    if (data.offLine) {
                        data.offLine = false;
                    }
                } else if ("offline".equals(mode)) {
                    if (!data.offLine) {
                        data.offLine = true;
                    }
                }
                if (EbeiMiscUtils.isNotEmpty(data.onlineShareKey) && EbeiMiscUtils.isNotEmpty(data.saveShareName)) {
                    EbeiMiscUtils.setSharedPreferenceValue(data.saveShareName, data.onlineShareKey, data.offLine);
                }
            }
        } else if ("/networkmode".equals(path)) {
            String mode = uri.getQueryParameter("mode");
            boolean netFirst;
            netFirst = "networkfirst".equals(mode);
            if (EbeiMiscUtils.isNotEmpty(data.netWorkFirstKey) && EbeiMiscUtils.isNotEmpty(data.saveShareName)) {
                EbeiMiscUtils.setSharedPreferenceValue(data.saveShareName, data.netWorkFirstKey, netFirst);
            }
        } else if ("/callphone".equals(path)) {
            final String title = uri.getQueryParameter("title");
            final String event = uri.getQueryParameter("event");
            final List<String> labels = uri.getQueryParameters("label");
            final List<String> phones = uri.getQueryParameters("phone");
            if (currentActivity != null && !currentActivity.isFinishing()) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String currentUrl = "";
                        if (data.bottomWebView != null) {
                            currentUrl = data.bottomWebView.getUrl();
                        }
                        showCallPhone(currentUrl, title, EbeiHtml5WebView.CALL_PHONE_TEL_GROUP, labels, phones);
                    }
                });
            }
        } else if ("/alert".equals(path)) {
            final String message = uri.getQueryParameter("message");
            final String title = uri.getQueryParameter("title");
            final String script0 = script;
            Activity current = EbeiConfig.getCurrentActivity();
            if (current != null && !current.isFinishing()) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMyDialog(message, title, script0, data.bottomWebView);
                    }
                });
            }
        } else if ("/toast".equals(path)) {
            final String message = uri.getQueryParameter("message");
            EbeiUIUtils.showToast(message);
        } else if ("/dialog".equals(path)) {
            final String message = uri.getQueryParameter("message");
            final String action = uri.getQueryParameter("action");
            final String cancel = uri.getQueryParameter("cancel");
            final String title = uri.getQueryParameter("title");
            final String script0 = script;
            if (currentActivity != null && !currentActivity.isFinishing()) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMyDialog(title, message, data.openNewWindow, action, cancel, script0, data.bottomWebView,
                                data.animStyle);
                    }
                });
            }
        } else if ("/dialphone".equals(path)) {
            final String event = uri.getQueryParameter("event");
            final String phoneNumber = uri.getQueryParameter("phone");
            final String label = uri.getQueryParameter("label");
            String source = "";
            if (data.bottomWebView != null) {
                source = data.bottomWebView.getUrl();
            }
            EbeiPhoneCallRequest request = new EbeiPhoneCallRequest(phoneNumber, EbeiHtml5WebView.CALL_PHONE_TEL_GROUP, source,
                    label);
            EbeiCallPhoneManager.getInstance().callPhone(request);
        } else if ("/goback".equals(path)) {
            if (currentActivity != null)
                currentActivity.finish();
        } else if ("/toolbar".equals(path)) {
            if (data.toolBar != null) {
                boolean enable = Boolean.parseBoolean(data.ecaUri.getQueryParameter("enable"));
                if (enable) {
                    data.toolBar.setVisibility(View.VISIBLE);
                } else {
                    data.toolBar.setVisibility(View.GONE);
                }
            }
        } else if ("/share".equals(path)) {
            String shareMessage = data.ecaUri.getQueryParameter("message");
            String shareType = getShareType(data.ecaUri.getQueryParameter("website"));
            Intent intent = new Intent(SHARE_ACTION);
            intent.putExtra(SHARE_MESSAGE, shareMessage);
            intent.putExtra(SHARE_TYPE, shareType);
            if (currentActivity != null)
                currentActivity.sendOrderedBroadcast(intent, null);
        } else if ("/opennative".equals(path)) {
            if (openNativeHandler != null) {
                String name = data.ecaUri.getQueryParameter("name");
                String params = data.ecaUri.getQueryParameter("params");
                String config = data.ecaUri.getQueryParameter("config");
                try {
                    String paramsString = "";
                    if (EbeiMiscUtils.isNotEmpty(params)) {
                        paramsString = new JSONObject(params).toString();
                    }
                    String configString = "";
                    if (EbeiMiscUtils.isNotEmpty(config)) {
                        configString = new JSONObject(config).toString();
                    }
                    openNativeHandler.onHandelOpenNative(name, paramsString, configString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return script;
    }

    public static void open(ProtocolData data) {
        Uri uri = data.ecaUri;
        String openUrl = uri.getQueryParameter("url");
        String target = uri.getQueryParameter("target");
        boolean appHandelUrl = false;//app是否拦截处理了该url
        if (data.interceptorUrlAsDownload && downloadInterceptor != null) {
            appHandelUrl = downloadInterceptor.onHandelDownload(openUrl);
        }
        if (!appHandelUrl && data.openNewWindow || (target != null && "_blank".equals(target))) {
            String title = uri.getQueryParameter("title");
            Intent intent = new Intent(EbeiConfig.getContext(), EbeiHtml5WebView.class);
            intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, openUrl);
            intent.putExtra(EbeiHtml5WebView.INTENT_DEFAULT_TITLE, title);
            intent.putExtra(EbeiHtml5WebView.INTENT_SHOW_PROGRESS, true);
            intent.putExtra(EbeiHtml5WebView.INTENT_STATISTICS_ID, data.statisticsEventId);
            intent.putExtra(EbeiHtml5WebView.INTENT_STATISTICS_NAME, data.statisticsEventName);
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null)
                currentActivity.startActivity(intent);
        }
    }

    static String getShareType(String website) {
        String shareType = "";
        if (EbeiMiscUtils.isNotEmpty(website)) {
            if ("weixin-friend".equals(website)) {
                shareType = SHARE_NAME_WEIXIN;
            } else if ("weixin-quan".equals(SHARE_NAME_WEIXIN_FRIEND)) {
                shareType = SHARE_NAME_WEIXIN_FRIEND;
            } else if ("sina-weibo".equals(website)) {
                shareType = SHARE_NAME_SINA;
            } else if ("tenc-weibo".equals(website)) {
                shareType = SHARE_NAME_QQWEIBO;
            } else if ("qq-friend".equals(website)) {
                shareType = SHARE_NAME_QQFRIEND;
            } else if ("qq-space".equals(website)) {
                shareType = SHARE_NAME_QZONE;
            }
        }
        return shareType;
    }

    public static String getStorageValue(String key) {
        String value = "";
        if (EbeiMiscUtils.isNotEmpty(key)) {
            value = EbeiMiscUtils.getSharepreferenceValue(STORAGE_SHARE_NAME, key, "");
        }
        return value;
    }

    public static boolean checkAssetsFileExists(String path) {
        AssetManager manager = EbeiConfig.getContext().getAssets();
        InputStream is = null;
        try {
            is = manager.open(path);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            EbeiDataUtils.close(is);
        }
    }

    public static void show(final ProtocolData data, final long timeOut) {
        if (EbeiConfig.getCurrentActivity() == null) {
            return;
        }
        EbeiConfig.postOnUiThread(new Runnable() {

            @Override
            public void run() {
                Activity currentActivity = EbeiConfig.getCurrentActivity();
                if (currentActivity != null && !currentActivity.isFinishing()) {
                    // 底部的webView为不可见状态，则翻转
                    if (data.bottomWebView.getVisibility() != View.VISIBLE) {
                        showBottomWebView(data);
                    }
                    if (timeOut > 0) {
                        EbeiConfig.postDelayOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (data.bottomWebView != null) {
                                    try {
                                        data.bottomWebView.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, timeOut);
                    }
                }
            }
        });
    }

    private static void showBottomWebView(final ProtocolData data) {
//        if (data.viewGroup == null || data.animStyle == ANIM_NONE) {
//            if (data.bottomWebView != null) {
//                data.bottomWebView.setVisibility(View.VISIBLE);
//            }
//            if (data.upWebView != null) {
//                data.upWebView.setVisibility(View.GONE);
//            }
//            return;
//        }
//        if (data.animStyle == ANIM_STYLE_ROTATE) {
//            RotateAnimation anim = new RotateAnimation(data.viewGroup.getWidth() / 2, data.viewGroup.getHeight() / 2,
//                    RotateAnimation.ROTATE_DECREASE);
//            anim.setInterpolatedTimeListener(new RotateAnimation.InterpolatedTimeListener() {
//
//                private boolean startedAnimation;
//
//                @Override
//                public void interpolatedTime(float interpolatedTime) {
//                    if (interpolatedTime > 0.5f && !startedAnimation) {
//                        EbeiLogger.i("info", "start anim~~");
//                        startedAnimation = true;
//                        data.upWebView.setVisibility(View.GONE);
//                        data.bottomWebView.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//            data.viewGroup.startAnimation(anim);
//        } else if (data.animStyle == ANIM_LEFT_OUT_RIGHT_IN) {
//            data.bottomWebView.setVisibility(View.VISIBLE);
//            data.upWebView
//                    .startAnimation(AnimationUtils.loadAnimation(EbeiConfig.getContext(), R.anim.slide_out_from_left));
//            data.bottomWebView
//                    .startAnimation(AnimationUtils.loadAnimation(EbeiConfig.getContext(), R.anim.slide_in_from_right));
//            EbeiConfig.postDelayOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    data.upWebView.setVisibility(View.GONE);
//                }
//            }, 1500);
//        }
    }


    static void showCallPhone(final String webViewUrl, String title, final String event, List<String> labels,
                              List<String> phones) {
//        if (EbeiConfig.getCurrentActivity() == null) {
//            return;
//        }
//        final List<EbeiDataUtils.Pair<String, String>> list = new ArrayList<EbeiDataUtils.Pair<String, String>>();
//        for (int i = 0; i < labels.size(); i++) {
//            String label = labels.get(i);
//            String phone = phones.get(i);
//            EbeiDataUtils.Pair<String, String> pair = new EbeiDataUtils.Pair<String, String>(label, phone);
//            list.add(pair);
//        }
//        if (EbeiMiscUtils.isEmpty(list)) {
//            EbeiUIUtils.showToast("当前电话为空！");
//            return;
//        }
//        Activity currentActivity = EbeiConfig.getCurrentActivity();
//        if (currentActivity == null)
//            return;
//        final Dialog dialog = new Dialog(currentActivity, R.style.EbeiCustomDialog);
//        View view = View.inflate(EbeiConfig.getContext(), R.layout.eca__call_phone_dialog, null);
//        dialog.setContentView(view);
//        LinearLayout main = (LinearLayout) view.findViewById(R.id.call_phone_main);
//        for (final EbeiDataUtils.Pair<String, String> pair : list) {
//            View greenBtn = View.inflate(EbeiConfig.getContext(), R.layout.eca__button, null);
//            TextView titleView = (TextView) greenBtn.findViewById(R.id.daijia_dialog_driver);
//            TextView phoneView = (TextView) greenBtn.findViewById(R.id.daijia_dialog_tv);
//            titleView.setText(pair.left);
//            phoneView.setText(pair.right);
//            greenBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    String phoneNumber = pair.right.replace("-", "");
//                    EbeiPhoneCallRequest request = new EbeiPhoneCallRequest(phoneNumber, event, webViewUrl, pair.left);
//                    EbeiCallPhoneManager.getInstance().callPhone(request);
//                    dialog.dismiss();
//                }
//            });
//            main.addView(greenBtn);
//        }
//        Button cancelBtn = new Button(EbeiConfig.getCurrentActivity());
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(EbeiDensityUtils.getPxByDip(240), EbeiDensityUtils.getPxByDip(40));
//        lp.topMargin = EbeiDensityUtils.getPxByDip(20);
//        cancelBtn.setLayoutParams(lp);
//        cancelBtn.setBackgroundResource(R.drawable.rectangle_btn_normal);
//        cancelBtn.setTextColor(0xFF8E8E8E);
//        cancelBtn.setText("取消");
//        cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, EbeiDensityUtils.getPxByDip(20));
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        main.addView(cancelBtn);
//        dialog.show();
    }


    static void showMyDialog(String message, String title, final String script, final WebView webView) {
        if (EbeiConfig.getCurrentActivity() == null) {
            return;
        }
        Builder builder = new Builder(EbeiConfig.getCurrentActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadJavascript(webView, script);
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    static void showMyDialog(String title, String message, final boolean openNewWindow, final String action,
                             final String cancel, final String script, final WebView webView, final int animStyle) {
        if (EbeiConfig.getCurrentActivity() == null) {
            return;
        }
        Builder builder = new Builder(EbeiConfig.getCurrentActivity());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (EbeiMiscUtils.isNotEmpty(action) && action.startsWith("ala")) {
                    ProtocolData data = new ProtocolData();
                    data.ecaUri = Uri.parse(action);
                    data.bottomWebView = webView;
                    data.openNewWindow = openNewWindow;
                    data.animStyle = animStyle;
                    handleMucangUri(data);
                } else if (EbeiMiscUtils.isNotEmpty(action) && action.startsWith("http:")) {
                    webView.loadUrl(action);
                }
                loadJavascript(webView, script);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (EbeiMiscUtils.isNotEmpty(cancel) && cancel.startsWith("ala")) {
                    ProtocolData data = new ProtocolData();
                    data.ecaUri = Uri.parse(cancel);
                    data.bottomWebView = webView;
                    data.openNewWindow = openNewWindow;
                    data.animStyle = animStyle;
                    handleMucangUri(data);
                } else if (EbeiMiscUtils.isNotEmpty(cancel) && cancel.startsWith("http:")) {
                    webView.loadUrl(cancel);
                }
                loadJavascript(webView, script);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private static void loadJavascript(WebView webView, String javaScript) {
        if (webView != null && EbeiMiscUtils.isNotEmpty(javaScript)) {
            webView.loadUrl(javaScript);
        }
    }

    static String handleCheckInstallUri(final Uri uri, final HashMap<String, Integer> stateMap) {
        List<AppData> appList = new ArrayList<>();
        String packList = uri.getQueryParameter("pkglist");
        packList = EbeiMiscUtils.safeURLDecode(packList, "UTF-8");
        try {
            JSONArray array = new JSONArray(packList);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject json = array.getJSONObject(i);
                AppData app = new AppData();
                app.appVersion = json.optString("version");
                app.pkgName = json.optString("package");
                appList.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String script = uri.getQueryParameter("callback");
        List<String> appVersionList = new ArrayList<>(appList.size());
        for (AppData data : appList) {
            String pkgName = data.pkgName;
            if (pkgName.contains("&")) {
                pkgName = pkgName.split("&")[0];
            }
            appVersionList.add(checkPackageInstalled(pkgName));
        }
        JSONObject data = new JSONObject();
        JSONObject callBack = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        // 将获得的版本号数据组装成json数组
        try {
            for (int i = 0; i < appList.size(); i++) {
                JSONObject json = new JSONObject();
                final AppData appData = appList.get(i);
                json.put("package", appData.pkgName);
                String appVersion = appVersionList.get(i);
                json.put("version", appVersionList.get(i));
                if (EbeiMiscUtils.isNotEmpty(stateMap) && stateMap.containsKey(appData.pkgName)) {
                    int value = stateMap.get(appData.pkgName);
                    if (value == -4) {
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/Android/data/" + EbeiConfig.getContext().getPackageName() + "/files/" + appData
                                        .toString());
                        if (file.exists()) {
                            String name = appData.pkgName;
                            if (name.contains("&")) {
                                name = name.split("&")[0];
                            }
                            json.put("package", name);
                            json.put("received", file.length());
                        }
                    } else if (value == -2) {
                        // EbeiLogger.i("life", "-2--------------" + appData.pkgName);
                        String name = appData.pkgName;
                        if (name.contains("&")) {
                            name = name.split("&")[0];
                        }
                        if (EbeiMiscUtils.isNotEmpty(checkPackageInstalled(name))) {
                            value = 1;
                            stateMap.remove(appData.pkgName);
                        }
                    }
                    String name = appData.pkgName;
                    if (name.contains("&")) {
                        name = name.split("&")[0];
                    }
                    json.put("package", name);
                    json.put("status", value);
                    json.put("message", "");
                } else if (EbeiMiscUtils.isNotEmpty(appVersion)) {
                    json.put("status", 1);
                    json.put("message", "已安装");
                    deleteFileByFilter(appData.pkgName);
                } else {
                    boolean apkFileExist = false;
                    File apkFile = new File(Environment.getExternalStorageDirectory(),
                            "/Android/data/" + EbeiConfig.getContext().getPackageName() + "/files");
                    if (apkFile.isDirectory()) {
                        File[] files = apkFile.listFiles(new FilenameFilter() {

                            @Override
                            public boolean accept(File dir, String filename) {
                                return filename.startsWith(appData.pkgName);
                            }
                        });
                        if (files != null && files.length > 0) {
                            for (File file : files) {
                                String fileName = file.getName();
                                if (EbeiMiscUtils.isNotEmpty(fileName) && fileName.contains("&")) {
                                    String version = fileName.split("&")[1];
                                    if (version.contains(".apk") && version.replace(".apk", "")
                                            .equals(appData.appVersion)) {
                                        // 20天之外的删除
                                        if (getDaysBetweenDates(file.lastModified(), System.currentTimeMillis()) < 20) {
                                            apkFileExist = true;
                                        } else {
                                            file.delete();
                                        }
                                    } else {
                                        file.delete();
                                    }
                                }
                            }
                        }
                    }
                    if (apkFileExist) {
                        json.put("status", -3);
                        json.put("message", "下载未安装");
                    } else {
                        json.put("status", 0);
                        json.put("message", "未安装");
                    }
                }
                jsonArray.put(json);
            }
            data.put("data", jsonArray);
            data.put("errcode", 0);
            data.put("result", true);
            // 组装callback
            callBack.put("value", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        script = script.replace("$context", callBack.toString());
        EbeiLogger.i("back", script);
        return script;
    }

    // 判断指定包名程序是否安装，如果安装则返回版本好，如果未安装则返回空字符
    public static String checkPackageInstalled(String packageName) {
        if (EbeiMiscUtils.isEmpty(packageName)) {
            return "";
        }
        String appVersion = "";
        try {
            PackageManager packageManager = EbeiConfig.getContext().getPackageManager();// 获取packagemanager
            List<PackageInfo> pkgInfoList = packageManager
                    .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);// 获取所有已安装程序的包信息
            if (pkgInfoList != null) {
                for (int i = 0; i < pkgInfoList.size(); i++) {
                    String pn = pkgInfoList.get(i).packageName;
                    if (packageName.equals(pn)) {
                        appVersion = pkgInfoList.get(i).versionName;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    // 获取文件夹下的相应文件名的文件
    private static void deleteFileByFilter(final String fileName) {
        File apkFile = new File(Environment.getExternalStorageDirectory(),
                "/Android/data/" + EbeiConfig.getContext().getPackageName() + "/files");
        if (apkFile.isDirectory()) {
            File[] files = apkFile.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(fileName);
                }
            });
            if (files != null && files.length > 0) {
                for (File file : files) {
                    // 20天为限，删除过期文件
                    if (getDaysBetweenDates(file.lastModified(), System.currentTimeMillis()) > 20) {
                        file.delete();
                    }
                }
            }
        }
    }

    private static int getDaysBetweenDates(long data1, long data2) {
        return (int) (Math.abs(data1 - data2) / (24 * 3600 * 1000));
    }

    /**
     * 处理下载Uri。
     *
     * @param uri 必须为decode后的uri
     * @param map 存放线程下载状态的map，多个下载任务必须使用同一个map对象
     */
    static void handleDownloadUri(final Uri uri, final HashMap<String, Integer> map, final WebView webView) {
        if (EbeiConfig.getCurrentActivity() == null) {
            return;
        }
        new Thread() {

            @Override
            public void run() {
                List<AppData> appList = new ArrayList<>();
                String packList = uri.getQueryParameter("package");
                AppData app = new AppData();
                try {
                    JSONObject json = new JSONObject(packList);
                    app.appVersion = json.optString("version");
                    app.pkgName = json.optString("package");
                    appList.add(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String callBack = uri.getQueryParameter("callback");
                File apkFile = new File(Environment.getExternalStorageDirectory(),
                        "/Android/data/" + EbeiConfig.getContext().getPackageName() + "/files/" + app.toString()
                                + ".apk");
                if (apkFile.exists()) {
                    updateDownloadStatus(webView, callBack, app.toString(), "", map, app.toString(), -2);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    Activity currentActivity = EbeiConfig.getCurrentActivity();
                    if (currentActivity != null)
                        currentActivity.startActivity(intent);
                } else if ("/applet/install".equals(uri.getPath())) {
                    EbeiLogger.i("info", "start to download");
                    String downloadUrl = uri.getQueryParameter("downurl");
                    boolean appHandelDownload = false;
                    if (downloadInterceptor != null) {
                        appHandelDownload = downloadInterceptor.onHandelDownload(downloadUrl);
                    }
                    //如果app没有注册下载拦截器，或者拦截器没有消费本次下载操作，则下载之
                    if (!appHandelDownload) {
                        InputStream is = null;
                        FileOutputStream out = null;
                        File apk = null;
//                        try {
//                            updateDownloadStatus(webView, callBack, app.toString(), downloadUrl, map, app.toString(),
//                                    -4);
//                            is = AlaHttpClient.getDefault().httpGetStream(downloadUrl);
//                            File file = EbeiDataUtils.createIfNotExistsOnSDCard(app.toString());
//                            file.createNewFile();
//                            out = new FileOutputStream(file);
//                            byte[] data = new byte[8192];
//                            int len = 0;
//                            while ((len = is.read(data)) != -1) {
//                                out.write(data, 0, len);
//                            }
//                            apk = EbeiDataUtils.createIfNotExistsOnSDCard(app.toString() + ".apk");
//                            apk.createNewFile();
//                            EbeiDataUtils.copy(file, apk);
//                            file.delete();
//                            updateDownloadStatus(null, null, null, null, map, app.toString(), -3);
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//                            EbeiConfig.getCurrentActivity().startActivity(intent);
//                            updateDownloadStatus(null, null, null, null, map, app.toString(), -2);
//                        } catch (Exception e) {
//                            EbeiUIUtils.showToast("下载失败，请查看网络连接！");
//                            updateDownloadStatus(webView, callBack, app.toString(), downloadUrl, map, app.toString(),
//                                    -1);
//                            if (apk != null && apk.exists()) {
//                                apk.delete();
//                            }
//                            e.printStackTrace();
//                        } finally {
//                            EbeiDataUtils.close(is);
//                            EbeiDataUtils.close(out);
//                        }
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Activity currentActivity = EbeiConfig.getCurrentActivity();
                    if (currentActivity != null)
                        currentActivity.startActivity(intent);
                }
            }
        }.start();
    }


    private static void updateDownloadStatus(final WebView webView, String callBack, String packageName, String downUrl,
                                             HashMap<String, Integer> map, String key, Integer status) {
        if (map == null || key == null || status == null) {
            return;
        }
        try {
            if (webView != null) {
                JSONObject data = new JSONObject();
                data.put("package", packageName);
                data.put("downurl", downUrl);
                data.put("status", status);
                data.put("message", "");
                JSONObject json = new JSONObject();
                json.put("result", true);
                json.put("errcode", 0);
                json.put("data", data);
                // 回调数据
                JSONObject backJson = new JSONObject();
                backJson.put("value", json);
                final String script = "javascript:" + callBack.replace("$context", backJson.toString());
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        webView.loadUrl(script);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (map) {
            EbeiLogger.i("info", "change state");
            map.put(key, status);
        }
    }

    static void handleStartUri(Uri uri) {
        if (EbeiConfig.getCurrentActivity() == null) {
            return;
        }
        String packageName = uri.getQueryParameter("package");
        packageName = EbeiMiscUtils.safeURLDecode(packageName, "UTF-8");
        if (EbeiMiscUtils.isNotEmpty(packageName)) {
            Intent intent = EbeiConfig.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null && intent != null) {
                currentActivity.startActivity(intent);
            }
        }
    }

    public static DownloadInterceptor getDownloadInterceptor() {
        return downloadInterceptor;
    }

    public static void setDownloadInterceptor(DownloadInterceptor downloadInterceptor) {
        EbeiProtocolUtils.downloadInterceptor = downloadInterceptor;
    }

    public static void setOpenNativeHandler(OpenNativeHandler openNativeHandler) {
        EbeiProtocolUtils.openNativeHandler = openNativeHandler;
    }

    static OpenNativeHandler getOpenNativeHandler() {
        return openNativeHandler;
    }

    public static interface DownloadInterceptor {
        public boolean onHandelDownload(String downloadUrl);
    }

    public static interface OpenNativeHandler {
        public void onHandelOpenNative(String name, String data, String otherData);
    }

    public static class ProtocolData {
        Uri ecaUri;
        WebView upWebView;
        WebView bottomWebView;
        public ViewGroup viewGroup;
        boolean openNewWindow;
        Boolean offLine;
        int animStyle;
        String saveShareName;
        String netWorkFirstKey;
        String onlineShareKey;
//        public boolean openWindowWithAnim;// open协议打开新activity时是否要显示动画
        HashMap<String, Integer> stateMap;// 检查本地安装软件时要用到的map
        View toolBar;// 工具栏对象
        /**
         * open协议时，是否需要将该url视为下载url，并通过app拦截处理，默认是不拦截。
         */
        boolean interceptorUrlAsDownload;
        /**
         * 是否需要开启页面显示时长统计。
         */
        public boolean needStatisticsPageTime;
        /**
         * 事件统计的id。
         */
        String statisticsEventId;
        /**
         * 事件统计的名称。
         */
        String statisticsEventName;
    }

    private static class AppData {

        String pkgName = "";
        String appVersion = "";

        // 获得的字符串为临时文件夹的名字，由于当下载时，传递给服务器的包名是 pkgName + "&" + appVersion的形式，
        // 在网页检查下载进度是，返回的包名也是 pkgName + "&" + appVersion，所以就会产生 pkgName + "&" + appVersion + "&"的错误文件名，
        // 所以要做特殊处理
        @Override
        public String toString() {
            if (EbeiMiscUtils.isNotEmpty(appVersion)) {
                return pkgName + "&" + appVersion;
            } else {
                return pkgName;
            }
        }
    }
}
