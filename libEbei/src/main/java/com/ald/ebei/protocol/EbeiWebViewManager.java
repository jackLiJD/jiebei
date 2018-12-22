package com.ald.ebei.protocol;

import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ald.ebei.EbeiBaseHtml5WebView;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.config.EbeiUrl;
import com.ald.ebei.ui.EbeiCreWebView;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.log.EbeiLogger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:32
 * 描述：
 * 修订历史：
 */
public class EbeiWebViewManager {
    private final static String TAG = "EbeiWebViewManager";
    public final static int HIDDEN_WEB_VIEW_COUNT = 2;

    private static Map<String, EbeiCreWebView> staticWebViews = new ConcurrentHashMap<>(2);

    public static String newAlaWebView(final Activity activity, final String loadURL) {
        if (staticWebViews.size() >= HIDDEN_WEB_VIEW_COUNT) {
            return "false";
        }
        if (!(activity instanceof EbeiBaseHtml5WebView)) {
            return "false";
        }
        final EbeiBaseHtml5WebView currentActivity = (EbeiBaseHtml5WebView) activity;
        final String webviewId = UUID.randomUUID().toString().replaceAll("-", "");
        EbeiConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                EbeiCreWebView webView = new EbeiCreWebView(activity);
                currentActivity.addHiddenWebView(webView);
                webView.setWebViewId(webviewId);
                webView.setWebViewType(EbeiCreWebView.WebViewType.HIDDEN);
                webView.setShow(false);
                boolean netFirst = EbeiMiscUtils.getSharepreferenceValue(EbeiBaseHtml5WebView.SHARE_NAME, loadURL, true);
                webView.addWebJS(netFirst);

                final StringBuilder loadBuilder = new StringBuilder(loadURL);
                //如果不是文件协议(或者使用的人说不加)才加后面一坨，否则不加
                if (!loadBuilder.toString().startsWith("file:")) {
                    EbeiUrl.buildJsonUrl(loadBuilder, "4.3", null, false, null);
                }

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        currentActivity.removeHiddenWebView(view);
                    }
                });

                webView.loadUrl(loadBuilder.toString());

                staticWebViews.put(webviewId, webView);
            }
        });
        return webviewId;
    }

    public static void destroyAlaWebView(final String webviewId) {
        EbeiConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (EbeiMiscUtils.isNotEmpty(webviewId)) {
                        final EbeiCreWebView webView = staticWebViews.get(webviewId);
                        if (webView != null) {
                            webView.destroy();
                            staticWebViews.remove(webviewId);
                        }
                    }
                } catch (Exception e) {
                    EbeiLogger.w(TAG, "webviewId: " + webviewId, e);
                }
            }
        });
    }

    public static void destroyAlaWebView(final EbeiCreWebView webView) {
        EbeiConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!webView.isShow()) {
                    webView.destroy();
                    staticWebViews.remove(webView.getWebViewId());
                }
            }
        });
    }

    public static void destroyAllAlaWebView() {
        EbeiConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (final EbeiCreWebView ebeiCreWebView : staticWebViews.values()) {
                    ebeiCreWebView.destroy();
                }
                staticWebViews.clear();
            }
        });
    }

    public static boolean hasTheWebView(String webviewId) {
        return webviewId != null && staticWebViews.containsKey(webviewId);
    }

    public static EbeiCreWebView getWebView(String webviewId) {
        return staticWebViews.get(webviewId);
    }
}
