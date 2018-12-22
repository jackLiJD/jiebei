package com.ald.ebei;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.JsResult;
import android.webkit.WebView;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.jsmethod.EbeiJSToJava;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.entity.ApiResponseEbei;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.protocol.EbeiCreProtocol;
import com.ald.ebei.protocol.EbeiProtocolUtils;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.alibaba.fastjson.JSONObject;
import com.ald.ebei.jsmethod.EbeiJavaToJS;

import java.lang.ref.WeakReference;

/*
 * Created by sean yu on 2017/6/3.
 */

public class EbeiHtml5WebView extends EbeiBaseHtml5WebView implements EbeiProtocolUtils.OpenNativeHandler {
    public static final String ACTION_REFRESH = "__action_refresh__";
    public static final String ACTION_AUTH_FACE_FINISH = "__action_auth_face_finish__";
    public static final String ACTION_AUTH_ZM_FINISH = "__action_auth_zm_finish__";
    public static final String ACTION_AUTH_ALIP_FINISH = "__action_auth_alip_finish__";
    public static final String ACTION_SHARE_ONSTART = "action_share_onstart";
    public static final String ACTION_SHARE_SUCCESS = "action_share_success";
    public static final String INTENT_MX_ORDER_NO = "mxOrderNo";//魔蝎的orderno
    public static final String INTENT_SHOW_TITLE_BAR = "showTitleBar";//是否显示标题栏
    private InnerReceiver receiver;
    private ShareReceiver shareReceiver;
    private EbeiJSToJava ebeiJsToJava;
    private boolean isZmCallback = false;
    private boolean isShowTitleBar = false;
    private String orderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWebView().addWebJS(true);
        getWebView().addJavascriptInterface(ebeiJsToJava = new EbeiJSToJava(getWebView()), "alaAndroid");
        //内部广播接收器
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter(ACTION_REFRESH);
        filter.addAction(ACTION_AUTH_FACE_FINISH);
        filter.addAction(ACTION_AUTH_ZM_FINISH);
        filter.addAction(ACTION_AUTH_ALIP_FINISH);
        registerReceiver(receiver, filter);
        Intent intent = getIntent();
        orderNo = intent.getStringExtra(INTENT_MX_ORDER_NO);
        isShowTitleBar = intent.getBooleanExtra(INTENT_SHOW_TITLE_BAR, false);

        shareReceiver = new ShareReceiver(this);
        IntentFilter shareFilter = new IntentFilter();
        shareFilter.addAction(ACTION_SHARE_ONSTART);
        shareFilter.addAction(ACTION_SHARE_SUCCESS);
        EbeiConfig.getLocalBroadcastManager().registerReceiver(shareReceiver, shareFilter);

        setOpenNativeHandler(this);
//        UpdateManager.getInstance().onApplicationStart(this);
    }


    public void setOpenNativeHandler(EbeiProtocolUtils.OpenNativeHandler openNativeHandler) {
        EbeiProtocolUtils.setOpenNativeHandler(openNativeHandler);
    }

    @Override
    protected boolean overrideUrlLoading(WebView view, String url) {
        hiddenRightOption();
        hideTitleBar();
        if (url.contains("showTitle=true") || isShowTitleBar) {
            showTitleBar();
        } else {
            hideTitleBar();
        }
        return super.overrideUrlLoading(view, url);
    }

    private void statisticsShareClick(String share_media) {
        try {
            if (getWebView() != null) {
                getWebView().loadUrl("javascript:postshareex('" + share_media + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void statisticsShareSuccess(String share_media) {
        try {
            if (getWebView() != null) {
                getWebView().loadUrl("javascript:postshareaf('" + share_media + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean onJsAlertResult(WebView view, String url, String message, JsResult result) {
        if (isJsonFormatter(message)) {
            try {
                /*去反斜杆操作*/
                JSONObject object = JSONObject.parseObject(message);
                if (EbeiMiscUtils.isNotEmpty(object.getString("type"))) {
                    result.confirm();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onJsAlertResult(view, url, message, result);
    }

    private boolean isJsonFormatter(String message) {
        return message.startsWith("{") && message.endsWith("}");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        EbeiConfig.getLocalBroadcastManager().unregisterReceiver(shareReceiver);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (null != ebeiJsToJava)
            ebeiJsToJava.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onHandelOpenNative(String name, String data, String otherData) {
        if (EbeiCreProtocol.Protocol.ZMXY_KEY.equals(name)) {
            Intent intent = new Intent();
            intent.putExtra("respBody", data);
            intent.putExtra("sign", otherData);
            intent.putExtra("type", "zm");
            setResult(RESULT_OK, intent);
            EbeiActivityUtils.pop();
        } else if (EbeiCreProtocol.GROUP_MOBILE_OPERATOR.equals(name)) {
            if (EbeiCreProtocol.Protocol.MOBILE_BACK_KEY.equals(otherData)) {
                EbeiActivityUtils.finish(EbeiHtml5WebView.class);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String status = jsonObject.getString("refreshStatus");
                JSONObject respObj = new JSONObject();
                respObj.put("type", "mobile");
                if (status.equals("1")) {
                    getWebView().loadUrl(String.format(EbeiJavaToJS.KITKAT_JS.RECEIVE_AUTH_FINISH, respObj.toJSONString()));
                }
            }
        } else if (EbeiCreProtocol.Protocol.GXB_BACK.equals(name)) {
            JSONObject respObj = new JSONObject();
            respObj.put("type", "alip");
            getWebView().loadUrl(String.format(EbeiJavaToJS.KITKAT_JS.RECEIVE_AUTH_FINISH, respObj.toJSONString()));
        }
    }


    /**
     * 内部类的广播接收器
     */
    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                refreshWebView();
            } else if (ACTION_AUTH_FACE_FINISH.equals(action)) {
                getWebView().loadUrl("javascript:getFaceMsg()");
            } else if (ACTION_AUTH_ZM_FINISH.equals(action) && !isZmCallback) {
                isZmCallback = true;
                String strData = intent.getStringExtra("respBody");
                String otherData = intent.getStringExtra("sign");
                String type = intent.getStringExtra("type");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("respBody", strData);
                jsonObject.put("sign", otherData);
                jsonObject.put("type", type);
                getWebView().loadUrl(String.format(EbeiJavaToJS.KITKAT_JS.RECEIVE_AUTH_FINISH, jsonObject.toJSONString()));
                if (getWebView().getUrl().contains("auth/verify")) {
                    EbeiActivityUtils.pop();
                }
            } else if (ACTION_AUTH_ALIP_FINISH.equals(action)) {
                JSONObject respObj = new JSONObject();
                respObj.put("type", "zm");
                if (EbeiMiscUtils.isNotEmpty(orderNo)) {
                    respObj.put("orderno", orderNo);
                    getWebView().loadUrl(String.format(EbeiJavaToJS.KITKAT_JS.RECEIVE_AUTH_FINISH, respObj.toJSONString()));
                    thirdFinished("ZM");
                }
                thirdFinished("ZM");
                if (!getWebView().getUrl().contains("gxbAuthBackUrl&token")) {
                    EbeiActivityUtils.pop();
                }
                if (getWebView().getUrl().contains("Fgxbdata")) {
                    EbeiActivityUtils.pop();
                }
            }
        }
    }

    private void thirdFinished(String type) {
        JSONObject reqObj = new JSONObject();
        reqObj.put("authType", type);
        EbeiClient.getService(EbeiApi.class)
                .thirdFinished(reqObj)
                .compose(EbeiRxUtils.<ApiResponseEbei>io_main())
                .subscribe(new EbeiBaseObserver<ApiResponseEbei>() {
                    @Override
                    public void onNext(ApiResponseEbei apiResponseEbei) {
                        super.onNext(apiResponseEbei);
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }

    private static class ShareReceiver extends BroadcastReceiver {

        private WeakReference<EbeiHtml5WebView> weakWebView;

        public ShareReceiver(EbeiHtml5WebView webView) {
            weakWebView = new WeakReference<>(webView);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || weakWebView == null || weakWebView.get() == null) {
                return;
            }
            final String action = intent.getAction();
            final String platform = intent.getStringExtra("share_media");
            if (ACTION_SHARE_ONSTART.equals(action)) { // 点击平台按钮分享开始
                weakWebView.get().statisticsShareClick(platform);
            } else if (ACTION_SHARE_SUCCESS.equals(action)) {    // 分享成功
                weakWebView.get().statisticsShareSuccess(platform);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getWebView().getUrl().contains("upScore")) {
            getWebView().loadUrl("javascript:updatePage('')");
        }
    }
}
