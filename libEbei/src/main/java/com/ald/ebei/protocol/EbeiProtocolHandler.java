package com.ald.ebei.protocol;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.view.View;


import com.ald.ebei.EbeiBaseHtml5WebView;
import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.callphone.EbeiCallPhoneManager;
import com.ald.ebei.callphone.EbeiPhoneCallRequest;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.protocol.data.EbeiProtocolData;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.log.EbeiLogger;

import org.json.JSONObject;

import static com.ald.ebei.util.EbeiUIUtils.sendBroadcast;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:34
 * 描述：WebViewClient处理事件
 * 修订历史：
 */
public class EbeiProtocolHandler implements ProtocolHandler {

    @Override
    public String handleProtocol(String group, final EbeiProtocolData ebeiProtocolData) {
        if (EbeiConfig.getCurrentActivity() == null || ebeiProtocolData == null) {
            return null;
        }
        final Uri uri = ebeiProtocolData.ecaUri;
        String path = uri.getPath();
        EbeiLogger.i("Ala", "path: " + path);
        //芝麻信用
        if (EbeiCreProtocol.GROUP_ZMXY.equals(group)) {
            if (path.contains(EbeiCreProtocol.Protocol.ZMXY_KEY)) {
                //芝麻信用回调
                if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
//                    String config = ebeiProtocolData.ecaUri.getQueryParameter("config");
                    String params = ebeiProtocolData.ecaUri.getQueryParameter("params");
                    String sign = ebeiProtocolData.ecaUri.getQueryParameter("sign");
                    Intent intent=new Intent(EbeiHtml5WebView.ACTION_AUTH_ZM_FINISH);
                    intent.putExtra("respBody",params);
                    intent.putExtra("sign",sign);
                    intent.putExtra("type","zm");
                    sendBroadcast(intent);
//                    EbeiProtocolUtils.getOpenNativeHandler().onHandelOpenNative(EbeiCreProtocol.EbeiCreProtocol.ZMXY_KEY, params, sign);
                }
            }
            return null;
        } else if (EbeiCreProtocol.GROUP_MOBILE_OPERATOR.equals(group)) {
            if (path.contains(EbeiCreProtocol.Protocol.MOBILE_SUCCESS_KEY)) {
                //手机运营商提交数据
                if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
                    String config = EbeiCreProtocol.Protocol.MOBILE_SUCCESS_KEY;
                    String params = ebeiProtocolData.ecaUri.getQueryParameter("params");
                    EbeiProtocolUtils.getOpenNativeHandler()
                            .onHandelOpenNative(EbeiCreProtocol.GROUP_MOBILE_OPERATOR, params, config);
                }
            }
            if (path.contains(EbeiCreProtocol.Protocol.MOBILE_BACK_KEY)) {
                //手机运营商提交数据
                if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
                    String config = EbeiCreProtocol.Protocol.MOBILE_BACK_KEY;
                    String params = ebeiProtocolData.ecaUri.getQueryParameter("fbapiNeedRefreshCurrData");
                    EbeiProtocolUtils.getOpenNativeHandler()
                            .onHandelOpenNative(EbeiCreProtocol.GROUP_MOBILE_OPERATOR, params, config);
                }
            }
            return null;
        } else if (EbeiCreProtocol.Protocol.WUYAO_FUND_STATUS.equals(group)) { // 51公积金管家回调
            if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
                String orderSn = ebeiProtocolData.ecaUri.getQueryParameter("orderSn");
                EbeiProtocolUtils.getOpenNativeHandler().onHandelOpenNative(EbeiCreProtocol.Protocol.WUYAO_FUND_STATUS, orderSn, "");
            }
            return null;
        } else if (EbeiCreProtocol.Protocol.GXB_BACK.equals(group)) { // GXB回调
            if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
                sendBroadcast(new Intent(EbeiHtml5WebView.ACTION_AUTH_ALIP_FINISH));
//                EbeiProtocolUtils.getOpenNativeHandler().onHandelOpenNative(EbeiCreProtocol.EbeiCreProtocol.GXB_BACK, "", "");
            }
            return null;
        } else {
            if (EbeiCreProtocol.Protocol.SHOW.equals(path)) {
                String timeOut = uri.getQueryParameter("timeout");
                EbeiProtocolUtils.ProtocolData data = new EbeiProtocolUtils.ProtocolData();
                data.ecaUri = ebeiProtocolData.ecaUri;
                data.bottomWebView = ebeiProtocolData.webView;
                EbeiProtocolUtils.show(data, EbeiMiscUtils.parseInt(timeOut, 0));
                return null;
            } else if (EbeiCreProtocol.Protocol.OPEN.equals(path)) {
                EbeiProtocolUtils.ProtocolData data = new EbeiProtocolUtils.ProtocolData();
                data.ecaUri = ebeiProtocolData.ecaUri;
                data.bottomWebView = ebeiProtocolData.webView;
                EbeiProtocolUtils.open(data);
                return null;
            } else if (EbeiCreProtocol.Protocol.CLOSE.equals(path)) {
                EbeiConfig.postOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ebeiProtocolData.webView.setVisibility(View.GONE);
                    }
                });
                return null;
            } else if (EbeiCreProtocol.Protocol.DIAL_PHONE.equals(path)) {
                final String phoneNumber = uri.getQueryParameter("phone");
                final String label = uri.getQueryParameter("label");
                EbeiConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String source = "";
                        if (ebeiProtocolData.webView != null) {
                            source = ebeiProtocolData.webView.getUrl();
                        }
                        EbeiPhoneCallRequest request = new EbeiPhoneCallRequest(phoneNumber, EbeiBaseHtml5WebView.CALL_PHONE_TEL_GROUP,
                                source, label);
                        EbeiCallPhoneManager.getInstance().callPhone(request);
                    }
                });
                return null;
            } else if (EbeiCreProtocol.Protocol.SHARE.equals(path)) {
                String shareMessage = uri.getQueryParameter("message");
                String shareType = EbeiProtocolUtils.getShareType(uri.getQueryParameter("website"));
                Intent intent = new Intent(EbeiProtocolUtils.SHARE_ACTION);
                intent.putExtra(EbeiProtocolUtils.SHARE_MESSAGE, shareMessage);
                intent.putExtra(EbeiProtocolUtils.SHARE_TYPE, shareType);
                Activity currentActivity = EbeiConfig.getCurrentActivity();
                if (currentActivity != null)
                    currentActivity.sendOrderedBroadcast(intent, null);
                return null;
            } else if (EbeiCreProtocol.Protocol.OPEN_NATIVE.equals(path)) {
                String name = ebeiProtocolData.ecaUri.getQueryParameter("name");
                String params = ebeiProtocolData.ecaUri.getQueryParameter("params");
                String config = ebeiProtocolData.ecaUri.getQueryParameter("config");
                try {
                    String paramsString = "";
                    if (EbeiMiscUtils.isNotEmpty(params)) {
                        if ("APP_SHARE".equals(name)) {
                            params = new String(Base64.decode(params, Base64.URL_SAFE));
                        }
                        paramsString = new JSONObject(params).toString();
                    }
                    String configString = "";
                    if (EbeiMiscUtils.isNotEmpty(config)) {
                        configString = new JSONObject(config).toString();
                    }
                    /*
                     * 由于H5验签的问题，用户在H5页面时被踢出。所以去掉了isLand()判断
                     * */
                    if (EbeiCreProtocol.NATIVE_LOGIN_FOR_WEB.equals(name)) {
//                        return String.valueOf(uri);
//                    } else {
                        if (EbeiProtocolUtils.getOpenNativeHandler() != null) {
                            EbeiProtocolUtils.getOpenNativeHandler().onHandelOpenNative(name, paramsString, configString);
                        }
                    }
                    Intent intent = new Intent(EbeiProtocolUtils.NATIVE_ACTION);
                    intent.putExtra(EbeiProtocolUtils.NATIVE_NAME, name);
                    intent.putExtra(EbeiProtocolUtils.NATIVE_PARAM, paramsString);
                    intent.putExtra(EbeiProtocolUtils.NATIVE_CONFIG, configString);
                    EbeiConfig.getLocalBroadcastManager().sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            return String.valueOf(uri);
        }
    }
}
