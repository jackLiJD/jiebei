package com.ald.ebei.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.protocol.EbeiProtocolHandler;
import com.ald.ebei.protocol.data.EbeiProtocolData;
import com.ald.ebei.util.EbeiMiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:37
 * 描述：
 * 修订历史：
 */
public class EbeiCreWebView extends WebView {
    protected EbeiProtocolData ebeiProtocolData;
    protected EbeiProtocolHandler ebeiProtocolHandler;
    // 用于存放callback，一但里面有数据，就要通知web来取（采用开关online offline的方式来通知）
    private List<String> callbackDataList = new ArrayList<>();
    private boolean online = true;
    private String webViewId;
    private boolean show;
    private WebViewType webViewType;
    private LoadUrlListener listener;

    public EbeiCreWebView(Context context) {
        super(context);
        init();
    }

    public EbeiCreWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        webViewType = WebViewType.NORMAL;
        show = true;
        ebeiProtocolHandler = new EbeiProtocolHandler();
    }

    public void setEbeiProtocolData(EbeiProtocolData ebeiProtocolData) {
        this.ebeiProtocolData = ebeiProtocolData;
    }

    public void addWebJS(boolean netFirst) {
        EbeiMiscUtils.enableHTML5(this, netFirst);
        addJavascriptInterface(this, "ala");
        addJavascriptInterface(this, "alaWebCore");
        addJavascriptInterface(new WebViewData(), "alaAndroidWebView1");
        addJavascriptInterface(new WebViewCallbackData(), "alaAndroidWebView2");
    }

    private String getOneCallback() {
        if (EbeiMiscUtils.isNotEmpty(callbackDataList)) {
            return callbackDataList.remove(0);
        }
        return null;
    }

    private void convulsions() {
        EbeiConfig.postOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void run() {
                EbeiCreWebView.this.setNetworkAvailable(online = !online);
            }
        });
    }

    private static String addCallbackName(String callbackName, String script) {
        try {
            JSONObject object = new JSONObject();
            object.put("callback", callbackName);
            object.put("data", script);
            return object.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class WebViewData {
        @JavascriptInterface
        public String getAlaWebViewData(String url, final String callback) {
            if (EbeiMiscUtils.isEmpty(url)) {
                return "";
            }
            final Uri uri = Uri.parse(url);
            ebeiProtocolData.ecaUri = uri;
            ebeiProtocolData.callbackName = callback;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String script = ebeiProtocolHandler.handleProtocol("", ebeiProtocolData);
                    if (EbeiMiscUtils.isEmpty(script)) {
                        return;
                    }
                    script = addCallbackName(callback, script);
                    callbackDataList.add(script);
                    convulsions();
                }
            }).start();
            if ("http".equals(uri.getHost())) {
                return ebeiProtocolData.httpId;
            } else {
                return "";
            }
        }

        @JavascriptInterface
        public String getAlaWebViewData(String url) {
            if (EbeiMiscUtils.isEmpty(url)) {
                return "";
            }
            Uri uri = Uri.parse(url);
            ebeiProtocolData.ecaUri = uri;
            return ebeiProtocolHandler.handleProtocol("", ebeiProtocolData);
        }
    }

    public void handleCallback(String callbackName, String script) {
        String ret = addCallbackName(callbackName, script);
        callbackDataList.add(ret);
        convulsions();
    }

    class WebViewCallbackData {
        @JavascriptInterface
        public String getAlaWebViewCallbackData() {
            return getOneCallback();
        }
    }

    @JavascriptInterface
    public String getVersion() {
        return "4.3";
    }

    public void setWebViewId(String webViewId) {
        this.webViewId = webViewId;
    }

    public String getWebViewId() {
        return webViewId;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public WebViewType getWebViewType() {
        return webViewType;
    }

    public void setWebViewType(WebViewType webViewType) {
        this.webViewType = webViewType;
    }

    public enum WebViewType {
        NORMAL, HIDDEN
    }

    public void setListener(LoadUrlListener listener) {
        this.listener = listener;
    }

    @Override
    public void loadUrl(String url) {
        if (listener != null) {
            listener.loadUrl(url);
        }
        super.loadUrl(url);
    }

    public interface LoadUrlListener {
        public void loadUrl(String url);
    }
}
