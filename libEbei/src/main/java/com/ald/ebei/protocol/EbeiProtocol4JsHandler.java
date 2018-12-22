package com.ald.ebei.protocol;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;


import com.ald.ebei.EbeiBaseHtml5WebView;
import com.ald.ebei.callphone.EbeiCallPhoneManager;
import com.ald.ebei.callphone.EbeiPhoneCallRequest;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.protocol.data.EbeiProtocolData;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.log.EbeiLogger;

import org.json.JSONObject;

import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:34
 * 描述：AlaWebChromeClient处理事件
 * 修订历史：
 */
public class EbeiProtocol4JsHandler implements ProtocolHandler {
//    private final static String TAG = "EbeiProtocol4JsHandler";

    @Override
    public String handleProtocol(String group, final EbeiProtocolData ebeiProtocolData) {
        if (EbeiConfig.getCurrentActivity() == null || ebeiProtocolData == null) {
            return null;
        }
        final Uri uri = ebeiProtocolData.ecaUri;
        String path = uri.getPath();
        String script = uri.getQueryParameter("callback");
        EbeiLogger.i("Sevn", "path: " + path);
        if (EbeiCreProtocol.Protocol4Js.HOST_INFO.equals(path)) {
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
        if (EbeiCreProtocol.Protocol4Js.APP_ROOT_STORAGE.equals(uri.getAuthority())) {
            // 存储数据
            if ("/set".equals(path)) {
                String storageKey = uri.getQueryParameter("key");
                String storageValue = uri.getQueryParameter("value");
                EbeiMiscUtils.setSharedPreferenceValue(ebeiProtocolData.shareName, storageKey, storageValue);
            }
        } else if (EbeiCreProtocol.Protocol4Js.APPLET_CHECK.equals(path)) {
            script = EbeiProtocolUtils.handleCheckInstallUri(uri, ebeiProtocolData.stateMap);
        } else if (EbeiCreProtocol.Protocol4Js.APPLET_INSTALL.equals(uri.getPath())) {
            EbeiProtocolUtils.handleDownloadUri(uri, ebeiProtocolData.stateMap, ebeiProtocolData.webView);
            script = "";
        } else if (EbeiCreProtocol.Protocol4Js.APPLET_START.equals(uri.getPath())) {
            EbeiProtocolUtils.handleStartUri(uri);
            script = "";
        } else if (EbeiCreProtocol.Protocol4Js.SHOW.equals(path)) {
            String timeOut = uri.getQueryParameter("timeout");
            EbeiProtocolUtils.ProtocolData data = new EbeiProtocolUtils.ProtocolData();
            data.ecaUri = ebeiProtocolData.ecaUri;
            data.bottomWebView = ebeiProtocolData.webView;
            EbeiProtocolUtils.show(data, EbeiMiscUtils.parseInt(timeOut, 0));
        } else if (EbeiCreProtocol.Protocol4Js.OPEN.equals(path)) {
            EbeiProtocolUtils.ProtocolData data = new EbeiProtocolUtils.ProtocolData();
            data.ecaUri = ebeiProtocolData.ecaUri;
            data.bottomWebView = ebeiProtocolData.webView;
            EbeiProtocolUtils.open(data);
        } else if (EbeiCreProtocol.Protocol4Js.CLOSE.equals(path)) {
            EbeiConfig.postOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ebeiProtocolData.webView.setVisibility(View.GONE);
                }
            });
        } else if (EbeiCreProtocol.Protocol4Js.DESTROY.equals(path)) {
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null)
                currentActivity.finish();
        } else if (EbeiCreProtocol.Protocol4Js.CHANGE_MODE.equals(path)) {
            String mode = uri.getQueryParameter("mode");
            if (ebeiProtocolData.offLine != null) {
                if ("online".equals(mode)) {
                    if (ebeiProtocolData.offLine) {
                        ebeiProtocolData.offLine = false;
                    }
                } else if ("offline".equals(mode)) {
                    if (!ebeiProtocolData.offLine) {
                        ebeiProtocolData.offLine = true;
                    }
                }
                if (EbeiMiscUtils.isNotEmpty(ebeiProtocolData.shareKey) && EbeiMiscUtils.isNotEmpty(ebeiProtocolData.shareName)) {
                    EbeiMiscUtils.setSharedPreferenceValue(ebeiProtocolData.shareName, ebeiProtocolData.shareKey,
                            ebeiProtocolData.offLine);
                }
            }
        } else if (EbeiCreProtocol.Protocol4Js.NETWORK_MODE.equals(path)) {
            String mode = uri.getQueryParameter("mode");
            boolean netFirst;
            netFirst = "networkfirst".equals(mode);
            if (EbeiMiscUtils.isNotEmpty(ebeiProtocolData.netWorkFirstKey) && EbeiMiscUtils.isNotEmpty(ebeiProtocolData.shareName)) {
                EbeiMiscUtils.setSharedPreferenceValue(ebeiProtocolData.shareName, ebeiProtocolData.netWorkFirstKey, netFirst);
            }
        } else if (EbeiCreProtocol.Protocol4Js.CALL_PHONE.equals(path)) {
            final String title = uri.getQueryParameter("title");
            final String event = uri.getQueryParameter("event");
            final List<String> labels = uri.getQueryParameters("label");
            final List<String> phones = uri.getQueryParameters("phone");
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null && !currentActivity.isFinishing()) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String currentUrl = "";
                        if (ebeiProtocolData.webView != null) {
                            currentUrl = ebeiProtocolData.webView.getUrl();
                        }
                        EbeiProtocolUtils.showCallPhone(currentUrl, title, event, labels, phones);
                    }
                });
            }
        } else if (EbeiCreProtocol.Protocol4Js.ALERT.equals(path)) {
            final String message = uri.getQueryParameter("message");
            final String title = uri.getQueryParameter("title");
            final String script0 = script;
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null && !currentActivity.isFinishing()) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        EbeiProtocolUtils.showMyDialog(message, title, script0, ebeiProtocolData.webView);
                    }
                });
            }
        } else if (EbeiCreProtocol.Protocol4Js.TOAST.equals(path)) {
            final String message = uri.getQueryParameter("message");
            EbeiUIUtils.showToast(message);
        } else if (EbeiCreProtocol.Protocol4Js.DIALOG.equals(path)) {
            final String message = uri.getQueryParameter("message");
            final String action = uri.getQueryParameter("action");
            final String cancel = uri.getQueryParameter("cancel");
            final String title = uri.getQueryParameter("title");
            final String script0 = script;
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null && !currentActivity.isFinishing()) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        EbeiProtocolUtils
                                .showMyDialog(title, message, ebeiProtocolData.openNewWindow, action, cancel, script0,
                                        ebeiProtocolData.webView, ebeiProtocolData.animStyle);
                    }
                });
            }
        } else if (EbeiCreProtocol.Protocol4Js.DIAL_PHONE.equals(path)) {
//            final String event = uri.getQueryParameter("event");
            final String phoneNumber = uri.getQueryParameter("phone");
            final String label = uri.getQueryParameter("label");
            EbeiConfig.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String source = "";
                    if (ebeiProtocolData.webView != null) {
                        source = ebeiProtocolData.webView.getUrl();
                    }
                    EbeiPhoneCallRequest request = new EbeiPhoneCallRequest(phoneNumber, EbeiBaseHtml5WebView.CALL_PHONE_TEL_GROUP, source,
                            label);
                    EbeiCallPhoneManager.getInstance().callPhone(request);
                }
            });

        } else if (EbeiCreProtocol.Protocol4Js.GO_BACK.equals(path)) {
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null)
                currentActivity.finish();
        } else if (EbeiCreProtocol.Protocol4Js.TOOL_BAR.equals(path)) {
            if (ebeiProtocolData.toolBar != null) {
                boolean enable = Boolean.parseBoolean(uri.getQueryParameter("enable"));
                if (enable) {
                    ebeiProtocolData.toolBar.setVisibility(View.VISIBLE);
                } else {
                    ebeiProtocolData.toolBar.setVisibility(View.GONE);
                }
            }
        } else if (EbeiCreProtocol.Protocol4Js.SHARE.equals(path)) {
            String shareMessage = uri.getQueryParameter("message");
            String shareType = EbeiProtocolUtils.getShareType(uri.getQueryParameter("website"));
            Intent intent = new Intent(EbeiProtocolUtils.SHARE_ACTION);
            intent.putExtra(EbeiProtocolUtils.SHARE_MESSAGE, shareMessage);
            intent.putExtra(EbeiProtocolUtils.SHARE_TYPE, shareType);
            Activity currentActivity = EbeiConfig.getCurrentActivity();
            if (currentActivity != null)
                currentActivity.sendOrderedBroadcast(intent, null);
        } else if (EbeiCreProtocol.Protocol4Js.OPEN_NATIVE.equals(path)) {
            if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
                String name = ebeiProtocolData.ecaUri.getQueryParameter("name");
                String params = ebeiProtocolData.ecaUri.getQueryParameter("params");
                String config = ebeiProtocolData.ecaUri.getQueryParameter("config");
                try {
                    String paramsString = "";
                    if (EbeiMiscUtils.isNotEmpty(params)) {
                        paramsString = new JSONObject(params).toString();
                    }
                    String configString = "";
                    if (EbeiMiscUtils.isNotEmpty(config)) {
                        configString = new JSONObject(config).toString();
                    }
                    EbeiProtocolUtils.getOpenNativeHandler().onHandelOpenNative(name, paramsString, configString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return script;
    }
}
