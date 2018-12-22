package com.ald.ebei;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ald.ebei.activity.EbeiBaseActivity;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.config.EbeiUrl;
import com.ald.ebei.protocol.EbeiCreProtocol;
import com.ald.ebei.protocol.EbeiWebViewManager;
import com.ald.ebei.protocol.data.EbeiProtocolData;
import com.ald.ebei.protocol.webclient.EbeiCreWebChromeClient;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiPermissionCheck;
import com.ald.ebei.util.EbeiUIUtils;
import com.alibaba.fastjson.JSONObject;
import com.ald.ebei.help.EbeiFormInjectHelper;
import com.ald.ebei.protocol.EbeiProtocolHandler;
import com.ald.ebei.ui.EbeiCreWebView;
import com.ald.ebei.ui.EbeiInputMethodLinearLayout;
import com.ald.ebei.util.EbeiBASE64Encoder;
import com.ald.ebei.util.log.EbeiLogger;

import java.util.HashMap;
import java.util.Map;

import static com.ald.ebei.util.EbeiPermissionCheck.REQUEST_CODE_ALL;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:28
 * 描述：H5页面
 * 修订历史：
 */
public class EbeiBaseHtml5WebView extends EbeiBaseActivity implements OnClickListener {
    public static final String CALL_PHONE_TEL_GROUP = "tel_group";
    public static final String LOCAL_ERROR_PAGE_URL = "file:///android_asset/fw/error_page/error.htm";
    //    public static final String INTENT_BASE_URL = "baseURL";// 要访问的网址，不带参数的
    public static final String INTENT_BASE_URL = "urlString";// 要访问的网址，不带参数的
    public static final String INTENT_TITLE = "title";// 要访问的网址，不带参数的
    public static final String INTENT_DEFAULT_TITLE = "defaultTitle";// 默认的title
    public static final String INTENT_SHOW_PROGRESS = "showProgress";
    public static final String INTENT_ORIENTATION = "showOrientation";
    public static final String INTENT_STATISTICS_NAME = "statisticsName";// 是否需要开启页面显示时长统计
    public static final String INTENT_STATISTICS_ID = "statisticsId";// 是否需要开启页面显示时长统计
    public static final String INTENT_USER_NAME = "h5UserName";// 用户登录名称
    public static final String INTENT_USER_TOKEN = "h5UserToken";// 用户登录token
    public static final String INTENT_SHOW_HIDDEN_WEB_VIEW = "show_hidden_web_view";
    public static final String INTENT_HIDDEN_WEB_VIEW_ID = "hidden_web_view_id";
    public static final String INTENT_SHOW_TOP_PANEL_NEW = "showTopPanelNew";// 新的是否显示顶部标题栏的参数
    public static final String INTENT_SHOW_RIGHT_BUTTON = "showRightButton"; //显示顶部右侧button
    public static final String INTENT_USE_ORIGINAL_URL = "useOriginalUrl";
    public static final String ORIENTATION_VERTICAL = "portrait";
    public static final String ORIENTATION_HORIZONTAL = "landscape";
    public static final String ORIENTATION_AUTO = "auto";
    public static final String SHARE_NAME = "html5_share";
    private static final String TAG = "EbeiHtml5WebView";
    private static final int SELECT_PICTURE_REQUEST_CODE = 2017; // TODO: 2018/4/2 来自H5直接调用,WebChromeClient中回调
    private static final int SELECT_PICTURES_REQUEST_CODE = 2018; // TODO: 2018/4/2 来自H5直接调用,WebChromeClient中回调
    private static final String ORIENTATION = "orientation";//老版的屏幕显示方向的url参数
    private static final String ORIENTATION_NEW = "ala-web-orientation";//新版的屏幕显示方向的url参数
    private static final String HARDWARE = "ala-web-hardware";// 是否开启硬件加速，默认true
    private boolean offLine;
    private volatile boolean netFirst;
    private volatile boolean webViewReceivedError;
    private volatile boolean showProgress;
    private boolean lastHardwareStatus;

    private long pageStartTime;//页面开始显示的时间，用于统计用
    private StringBuilder loadBuilder;
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    //设置Schemes白名单(公信宝)
//    String[] whiteList = {"taobao://", "alipayqr://", "alipays://", "wechat://", "weixin://", "mqq://", "mqqwpa://", "openApp.jdMobile://"};

    /**
     * 分享预加载使用，使用弱引用，没有了拉倒，会重新加载的
     */
    private EbeiCreWebChromeClient ebeiCreWebChromeClient;
    private EbeiProtocolData data;
    private EbeiFormInjectHelper ebeiFormInjectHelper;

    private String hiddenWebViewId;
    private boolean showHiddenWebViewReal = false;

    private EbeiInputMethodLinearLayout rootView;
    private EbeiCreWebView webView;
    private View errorNetworkView;
    private ProgressBar progressBar;
    private TextView titleView;
    private String loadURL = "http://www.91ala.com/";
    private String pageName;
    private boolean showTopPanel;
    private EbeiProtocolHandler protocol;
    private boolean useOriginalUrl;
    private String userToken;
    private String userName;
    private boolean showRightButton;
    private String getTitle;//页面标题
    private boolean isError;

    @Override
    public String getStatName() {
        return "H5页面";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout__html5);
        rootView = findViewById(R.id.root);
        webView = findViewById(R.id.web_view);
        errorNetworkView = findViewById(R.id.ll_error_network);
        progressBar = findViewById(R.id.webview_progress);
        titleView = findViewById(R.id.top_title);

        pageName = getIntent().getStringExtra(INTENT_DEFAULT_TITLE);

        boolean showHiddenWebView = getIntent().getBooleanExtra(INTENT_SHOW_HIDDEN_WEB_VIEW, false);
        hiddenWebViewId = getIntent().getStringExtra(INTENT_HIDDEN_WEB_VIEW_ID);
        if (showHiddenWebView && EbeiWebViewManager.hasTheWebView(hiddenWebViewId)) {
            FrameLayout webViewContainer = findViewById(R.id.web_view_container);
            webViewContainer.removeView(webView);
            webView = null;
            webView = EbeiWebViewManager.getWebView(hiddenWebViewId);
            if (webView.getParent() != null && webView.getParent() instanceof FrameLayout) {
                FrameLayout webViewParent = (FrameLayout) webView.getParent();
                webViewParent.removeView(webView);
            }
            webView.setShow(true);
            webViewContainer.addView(webView);
            showHiddenWebViewReal = true;
        }


        initData();
        updateParamsFromUrl(loadURL);
        initUI();
    }

    public void addHiddenWebView(EbeiCreWebView webView) {
        FrameLayout hiddenWebViewContainer = findViewById(R.id.hidden_web_view_container);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        hiddenWebViewContainer.addView(webView, layoutParams);
    }

    public void removeHiddenWebView(WebView webView) {
        FrameLayout hiddenWebViewContainer = findViewById(R.id.hidden_web_view_container);
        hiddenWebViewContainer.removeView(webView);
    }

    public void removeAllHiddenWebView() {
        FrameLayout hiddenWebViewContainer = findViewById(R.id.hidden_web_view_container);
        hiddenWebViewContainer.removeAllViews();
    }


    public void showRightOption(OnClickListener listener) {
        if (findViewById(R.id.btn_browser_more) != null) {
            findViewById(R.id.btn_browser_more).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_browser_more).setOnClickListener(listener);
        }
    }

    public void hiddenRightOption() {
        if (findViewById(R.id.btn_browser_more) != null) {
            findViewById(R.id.btn_browser_more).setVisibility(View.INVISIBLE);
        }
        if (findViewById(R.id.tv_browser_more) != null) {
            findViewById(R.id.tv_browser_more).setVisibility(View.INVISIBLE);
        }
        if (findViewById(R.id.btn_browser_more_two) != null) {
            findViewById(R.id.btn_browser_more_two).setVisibility(View.INVISIBLE);
        }
    }

    public void showRightOption(int resId, OnClickListener listener) {
        if (findViewById(R.id.btn_browser_more) != null) {
            findViewById(R.id.btn_browser_more).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.btn_browser_more)).setImageResource(resId);
            findViewById(R.id.btn_browser_more).setOnClickListener(listener);
        }
        if (findViewById(R.id.tv_browser_more) != null) {
            findViewById(R.id.tv_browser_more).setVisibility(View.INVISIBLE);
        }
        if (findViewById(R.id.btn_browser_more_two) != null) {
            findViewById(R.id.btn_browser_more_two).setVisibility(View.INVISIBLE);
        }

    }

    public void showRightTextOption(String text, OnClickListener listener) {
        if (findViewById(R.id.tv_browser_more) != null) {
            findViewById(R.id.tv_browser_more).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_browser_more)).setText(text);
            findViewById(R.id.tv_browser_more).setOnClickListener(listener);
        }
        if (findViewById(R.id.btn_browser_more) != null) {
            findViewById(R.id.btn_browser_more).setVisibility(View.INVISIBLE);
        }
        if (findViewById(R.id.btn_browser_more_two) != null) {
            findViewById(R.id.btn_browser_more_two).setVisibility(View.INVISIBLE);
        }
    }

    public void showRightOptionTwo(int resId, OnClickListener listener) {
        if (findViewById(R.id.btn_browser_more_two) != null) {
            findViewById(R.id.btn_browser_more_two).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.btn_browser_more_two)).setImageResource(resId);
            findViewById(R.id.btn_browser_more_two).setOnClickListener(listener);
        }
        if (findViewById(R.id.tv_browser_more) != null) {
            findViewById(R.id.tv_browser_more).setVisibility(View.INVISIBLE);
        }
    }

    public void hideTitleBar() {
        if (findViewById(R.id.top_panel) != null) {
            findViewById(R.id.top_panel).setVisibility(View.GONE);
        }
    }

    public void showTitleBar() {
        if (findViewById(R.id.top_panel) != null) {
            findViewById(R.id.top_panel).setVisibility(View.VISIBLE);
        }
    }


    private Uri updateParamsFromUrl(String url) {
        if (EbeiMiscUtils.isEmptyOrLiterallyNull(url)) {
            return null;
        }
        try {
            Uri uri = Uri.parse(url);
            if (uri == null) {
                return null;
            }
            initHardware(uri);
            initOrientation(uri);
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initHardware(Uri uri) {
        String param = uri.getQueryParameter(HARDWARE);
        boolean hardware = lastHardwareStatus;
        if (EbeiMiscUtils.isNotEmpty(param)) {
            hardware = Boolean.parseBoolean(param);
            lastHardwareStatus = hardware;
        }
        if (hardware) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }


    private void initOrientation(Uri uri) {
        //先获取新版的屏幕方向的参数，如果没有则尝试获取老版的参数，最后做相应处理
        String param = uri.getQueryParameter(ORIENTATION_NEW);
        if (EbeiMiscUtils.isEmpty(param)) {
            param = uri.getQueryParameter(ORIENTATION);
        }
        if (EbeiMiscUtils.isNotEmpty(param)) {
            if (ORIENTATION_VERTICAL.equals(param)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (ORIENTATION_HORIZONTAL.equals(param)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (ORIENTATION_AUTO.equals(param)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private boolean isCurrentOrientationLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void dismissSmallBackBtn() {
        findViewById(R.id.html_small_back_btn).setVisibility(View.GONE);
    }

    private void showSmallBackBtn() {
        findViewById(R.id.html_small_back_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.html_small_back_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        ebeiFormInjectHelper = new EbeiFormInjectHelper();
        ebeiFormInjectHelper.init();
        protocol = new EbeiProtocolHandler();
        if (showHiddenWebViewReal) {
            loadURL = webView.getUrl();
            loadBuilder = new StringBuilder(loadURL);
            updateTitle(webView.getTitle());
        } else {
            loadURL = getIntent().getStringExtra(INTENT_BASE_URL);
            //url去空格操作
            if (loadURL == null) {
                loadURL = EbeiConfig.getEbeiServerProvider().getH5Server();
            }
            loadURL = loadURL.trim();
            getTitle = getIntent().getStringExtra(INTENT_TITLE);
            while (loadURL.startsWith("　")) {
                loadURL = loadURL.substring(1, loadURL.length()).trim();
            }
            while (loadURL.endsWith("　")) {
                loadURL = loadURL.substring(0, loadURL.length() - 1).trim();
            }

            userToken = getIntent().getStringExtra(INTENT_USER_TOKEN);
            userName = getIntent().getStringExtra(INTENT_USER_NAME);
            useOriginalUrl = getIntent().getBooleanExtra(INTENT_USE_ORIGINAL_URL, false);
            loadBuilder = new StringBuilder(loadURL);
            //如果不是文件协议(或者使用的人说不加)才加后面一坨，否则不加
            addAppInfo();
        }
        ebeiFormInjectHelper.setLoadURL(loadURL);
        offLine = EbeiMiscUtils.getSharepreferenceValue(SHARE_NAME, loadURL, true);
        netFirst = EbeiMiscUtils.getSharepreferenceValue(SHARE_NAME, loadURL, true);
        showTopPanel = getIntent().getBooleanExtra(INTENT_SHOW_TOP_PANEL_NEW, true);
        showProgress = getIntent().getBooleanExtra(INTENT_SHOW_PROGRESS, true);
        showRightButton = getIntent().getBooleanExtra(INTENT_SHOW_RIGHT_BUTTON, true);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initUI() {
        if (showRightButton) {
            findViewById(R.id.btn_browser_option).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_browser_option).setVisibility(View.GONE);
        }
        if (showTopPanel) {
            findViewById(R.id.top_panel).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.top_panel).setVisibility(View.GONE);
        }
        String currentOrientation = getIntent().getStringExtra(INTENT_ORIENTATION);
        if (ORIENTATION_HORIZONTAL.equals(currentOrientation)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            findViewById(R.id.top_panel).setVisibility(View.GONE);
            showSmallBackBtn();
        }
        findViewById(R.id.btn_browser_back).setOnClickListener(this);
        findViewById(R.id.btn_browser_close).setOnClickListener(this);
        findViewById(R.id.btn_browser_option).setOnClickListener(this);
        findViewById(R.id.tv_error_retry).setOnClickListener(this);
        if (!EbeiMiscUtils.isEmptyOrLiterallyNull(pageName)) {
            updateTitle(pageName);
        }
        /*
         * 初始化进度条
         */
        initProgressBar();

        webView.addWebJS(netFirst);
        webView.addJavascriptInterface(ebeiFormInjectHelper, "alaInject");

        data = new EbeiProtocolData();
        data.webView = webView;
        data.shareName = SHARE_NAME;
        data.showTitleBar = showTopPanel;
        data.stateMap = new HashMap<>();
        data.webUrl = loadBuilder.toString();
        webView.setEbeiProtocolData(data);
        ebeiCreWebChromeClient = getAlaWebChromeClient(data);
        webView.setWebChromeClient(ebeiCreWebChromeClient);

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                EbeiMiscUtils.goToSite(EbeiBaseHtml5WebView.this, url);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                EbeiLogger.i(TAG, "onPageStarted url" + url);
                overrideUrlLoading(view, url);
                if (!offLine || showProgress) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                EbeiLogger.i(TAG, "onLoadResource url=" + url);
                ebeiFormInjectHelper.doInjectIfNeed(url, "");
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                EbeiLogger.i(TAG, "onPageFinished url=" + url);
                //这里用完全匹配，因为任何本地网页都是file:///开头的，只用file:///匹配会拦截所有本地网页
                if (LOCAL_ERROR_PAGE_URL.equals(url)) {
                    webViewReceivedError = true;
                } else if (webViewReceivedError) {
                    webViewReceivedError = false;
                    view.clearHistory();
                }
                if (EbeiMiscUtils.isSimpleEmpty(getTitle)) {
                    updateTitle(view.getTitle());
                } else {
                    updateTitle(getTitle);
                }
                if (!offLine || showProgress) {
                    progressBar.setVisibility(View.GONE);
                }
                if (isError) {
                    return;
                }
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
                if (errorNetworkView != null) {
                    errorNetworkView.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回true不需要在当前webView中处理,表示事件已经消耗了
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                Uri uri = updateParamsFromUrl(url);
                //如果不是文件协议(或者使用的人说不加)才加后面一坨，否则不加
                if (null != uri && (uri.getHost().contains(EbeiCreProtocol.ROOT_H5_HOST) || uri.getHost().contains(EbeiCreProtocol.ROOT_IP)) && !useOriginalUrl) {
                    StringBuilder urlBuilder = new StringBuilder(url);
                    Map<String, String> map = new HashMap<>();
                    userName = EbeiConfig.getEbeiAccountProvider().getUserName();
                    map.put(EbeiUrl.USER_NAME, userName);

                    if (EbeiConfig.isLand()) {
                        userToken = EbeiConfig.getEbeiAccountProvider().getUserToken();
                        map.put(EbeiUrl.SIGN, userToken);
                    }
                    EbeiUrl.buildJsonUrl(urlBuilder, "4.3", map, false, null);
                    url = urlBuilder.toString();
                    if (interceptFanBeiUrl(url)) return true;
                }
                if (handleAlaProtocol(url)) {
                    return true;
                } else if (url.startsWith("tel:")) {
                    String mobile = url.substring(url.lastIndexOf("/") + 1);
                    Uri telUri = Uri.parse("tel:" + mobile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, telUri);
                    startActivity(intent);
                    //返回true不需要在当前webView中处理
                    return true;
                } else if (url.startsWith("sms:")) {
                    String mobile = url.substring(url.lastIndexOf("/") + 1);
                    Uri smsUri = Uri.parse("sms:" + mobile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                    startActivity(intent);
                    return true;
                } else if (url.contains(EbeiCreProtocol.Protocol.GXB_BACK) && !url.contains("gxbAuthBackUrl&token")) {
                    data.ecaUri = Uri.parse(url);
                    protocol.handleProtocol(EbeiCreProtocol.Protocol.GXB_BACK, data);
                    return true;
                }
                //如果不是文件协议(或者使用的人说不加)才加后面一坨，否则不加
                Uri uri1 = Uri.parse(loadBuilder.toString());
                if ((uri1.getHost().contains(EbeiCreProtocol.ROOT_H5_HOST) || uri1.getHost().contains(EbeiCreProtocol.ROOT_IP)) && !useOriginalUrl) {
                    //重新load,让后台捕捉appInfo
                    webView.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(webView, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed(); // 接受网站证书
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                if (webView != null) {
                    loadBlank();
                    webView.setVisibility(View.GONE);
                }
                if (errorNetworkView != null) {
                    errorNetworkView.setVisibility(View.VISIBLE);
                }
            }


        });
        if (!showHiddenWebViewReal) {
            webView.loadUrl(loadBuilder.toString());
        }
    }

    private boolean interceptFanBeiUrl(String url) {
        if (url.contains(EbeiCreProtocol.Protocol.WUYAO_FUND_STATUS)) {
            data.ecaUri = Uri.parse(url);
            protocol.handleProtocol(EbeiCreProtocol.Protocol.WUYAO_FUND_STATUS, data);
            return true;
        }
        return false;
    }

    private void loadBlank() {
        if (webView != null) {
            String data = " ";
            webView.loadUrl("javascript:document.body.innerText=\"" + data + "\"");
        }
    }


    /**
     * 重定向URL
     */
    protected boolean overrideUrlLoading(WebView view, String url) {
        Uri uri = updateParamsFromUrl(url);
        //如果不是文件协议(或者使用的人说不加)才加后面一坨，否则不加
        if (null != uri && (uri.getHost().contains(EbeiCreProtocol.ROOT_H5_HOST) || uri.getHost().contains(EbeiCreProtocol.ROOT_IP)) && !useOriginalUrl) {
            StringBuilder urlBuilder = new StringBuilder(url);
            Map<String, String> map = new HashMap<>();
            userName = EbeiConfig.getEbeiAccountProvider().getUserName();
            map.put(EbeiUrl.USER_NAME, userName);
            if (EbeiConfig.isLand()) {
                userToken = EbeiConfig.getEbeiAccountProvider().getUserToken();
                map.put(EbeiUrl.SIGN, userToken);
            }
            EbeiUrl.buildJsonUrl(urlBuilder, "4.3", map, false, null);
            url = urlBuilder.toString();
        }
        if (handleAlaProtocol(url)) {
            return true;
        } else if (url.startsWith("tel:")) {
            String mobile = url.substring(url.lastIndexOf("/") + 1);
            Uri telUri = Uri.parse("tel:" + mobile);
            Intent intent = new Intent(Intent.ACTION_VIEW, telUri);
            startActivity(intent);
            //返回true不需要在当前webView中处理
            return true;
        }
        return false;
    }


    private boolean handleAlaProtocol(String url) {
        Uri uri = Uri.parse(url);
        if (url.contains(EbeiCreProtocol.Protocol.ZMXY_KEY)) {
            data.ecaUri = uri;
            protocol.handleProtocol(EbeiCreProtocol.Protocol.ZMXY_KEY, data);
            return true;
        } else if (url.contains(EbeiCreProtocol.Protocol.MOBILE_SUCCESS_KEY)) {
            //手机运营商提交数据
            data.ecaUri = uri;
            protocol.handleProtocol(EbeiCreProtocol.GROUP_MOBILE_OPERATOR, data);
            return false;
        } else if (url.contains(EbeiCreProtocol.Protocol.MOBILE_BACK_KEY)) {
            //手机运营商提交后的返回
            data.ecaUri = uri;
            protocol.handleProtocol(EbeiCreProtocol.GROUP_MOBILE_OPERATOR, data);
            return true;
        } else {
            if (!url.startsWith("file:")) {
                if (url.contains(EbeiCreProtocol.ROOT_WEB_PATH)) {
                    data.ecaUri = uri;
                    String resultUrl = protocol.handleProtocol(EbeiCreProtocol.GROUP_SELF, data);
                    return EbeiMiscUtils.isEmpty(resultUrl);
                } else // TODO: 2017/2/15 使用自定义界面启动器来尝试启动activity
                    return url.contains(EbeiCreProtocol.ROOT_WEB_PATH);
            } else {
                return false;
            }
        }
    }

    /**
     * EbeiCreWebChromeClient
     */
    private EbeiCreWebChromeClient getAlaWebChromeClient(EbeiProtocolData data) {
        return new EbeiCreWebChromeClient(data, progressBar) {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                boolean isAppAlert = onJsAlertResult(view, url, message, result);
                return isAppAlert || super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                      JsPromptResult promptResult) {
                if (EbeiMiscUtils.isNotEmpty(defaultValue)) {
                    Uri uri = Uri.parse(defaultValue);
                    if (EbeiCreProtocol.Protocol4Js.CLOSE.equals(uri.getPath())) {
                        promptResult.confirm("");
                        finish();
                        return true;
                    }
                }
                return super.onJsPrompt(view, url, message, defaultValue, promptResult);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (EbeiMiscUtils.isEmptyOrLiterallyNull(EbeiBaseHtml5WebView.this.pageName)) {
                    updateTitle(title);
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                startSelectFiles(filePathCallback, fileChooserParams);
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                if ("image/*".equals(acceptType) || acceptType.startsWith("image/")) {
                    startSelectFile(uploadFile);
                } else {
                    uploadFile.onReceiveValue(null);
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
                if ("image/*".equals(acceptType) || acceptType.startsWith("image/")) {
                    startSelectFile(uploadFile);
                } else {
                    uploadFile.onReceiveValue(null);
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadFile) {
                startSelectFile(uploadFile);
            }

            private void startSelectFile(ValueCallback<Uri> uploadFile) {
                EbeiBaseHtml5WebView.this.uploadFile = uploadFile;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE_REQUEST_CODE);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            private void startSelectFiles(ValueCallback<Uri[]> uploadFiles, WebChromeClient.FileChooserParams fileChooserParams) {
                if (EbeiBaseHtml5WebView.this.uploadFiles != null) {
                    EbeiBaseHtml5WebView.this.uploadFiles.onReceiveValue(null);
                    EbeiBaseHtml5WebView.this.uploadFiles = null;
                }
                EbeiBaseHtml5WebView.this.uploadFiles = uploadFiles;
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, SELECT_PICTURES_REQUEST_CODE);
            }
        };
    }

    /**
     * js交互实现
     */
    protected boolean onJsAlertResult(WebView view, String url, String message, JsResult result) {
        return false;
    }

    /**
     * 返回
     */
    private void doBack() {
        if (webView.canGoBack() && !webViewReceivedError && !webView.getUrl().contains("goBack=false")) {
            if (findViewById(R.id.btn_browser_close).getVisibility() != View.VISIBLE) {
                findViewById(R.id.btn_browser_close).setVisibility(View.VISIBLE);
            }
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.goBack();
        } else {
            if (!TextUtils.isEmpty(webView.getUrl())) {
                //如果当前页面是支付结果页，则同时关闭订单详情页
                if (webView.getUrl().contains("/h5/asj/asjBrand/payResult.html?javaurl=ctestapp.51fanbei.com&port=80&httptype=http")) {
                    try {
                        Class clazz = Class.forName("com.ald.eca.user.ui.OrderDetailActivity");
                        EbeiActivityUtils.finish(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent();
            setResult(RESULT_OK);
            EbeiActivityUtils.pop(this, intent);
        }
    }

    /**
     * 初始化进度条
     */

    private void initProgressBar() {
        if (offLine) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新title标题
     */
    private void updateTitle(String titleContent) {
        if (EbeiMiscUtils.isSimpleEmpty(titleContent)) {
            return;
        }
        titleView.setText(titleContent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
            if (uploadFile != null) {
                uploadFile.onReceiveValue((resultCode != RESULT_OK || data == null) ? null : data.getData());
            }
        } else if (requestCode == SELECT_PICTURES_REQUEST_CODE) {
            if (uploadFiles != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    uploadFiles.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    uploadFiles = null;
                }
            }
        } else if (requestCode == 0x0101) {
            //h5跳转扫描页面，扫描结果解析 BundleKey.REQUEST_CODE_MIAN_SCAN=0x0101
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String content = bundle.getString("result_content");
                if (EbeiMiscUtils.isNotEmpty(content)) {
                    Intent intent = new Intent();
                    Activity context = EbeiConfig.getCurrentActivity();
                    if (context == null)
                        context = EbeiActivityUtils.peek();
                    try {
                        String decodeContent = EbeiBASE64Encoder.decodeString(content);
                        JSONObject jsonObject = JSONObject.parseObject(decodeContent);
                        String decodeType = jsonObject.getString("type");
                        String tradeUrl = jsonObject.getString("url");

                        if ("TRADE".equals(decodeType)) {
                            String host = EbeiConfig.getEbeiServerProvider().getAppServer();
                            String url = host.substring(0, host.length()) + tradeUrl;
                            intent.putExtra(EbeiBaseHtml5WebView.INTENT_BASE_URL, url);
                            EbeiActivityUtils.push(context, EbeiBaseHtml5WebView.class, intent);
                        } else {
                            intent.putExtra(EbeiBaseHtml5WebView.INTENT_BASE_URL, tradeUrl);
                            EbeiActivityUtils.push(context, EbeiBaseHtml5WebView.class, intent);
                        }
                    } catch (Exception ex) {
                        EbeiUIUtils.showToast("商家信息获取失败，请重新扫描");
                        if (EbeiPermissionCheck.getInstance().checkPermission(context, Manifest.permission.CAMERA)) {
                            Class clazz = null;
                            try {
                                clazz = Class.forName("com.ald.eca.main.ui.QRCodeScanActivity");
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            Intent intentScan = new Intent(context, clazz);
                            EbeiActivityUtils.push(clazz, intentScan, 0x0101);
                        } else {
                            EbeiPermissionCheck.getInstance()
                                    .askForPermissions(context, new String[]{Manifest.permission.CAMERA},
                                            REQUEST_CODE_ALL);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
        if (uploadFile != null) {
            uploadFile = null;
        }
        if (pageStartTime == 0) {
            pageStartTime = System.currentTimeMillis();
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }

    public void destroyWebView() {
        rootView.removeAllViews();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView = null;
            EbeiWebViewManager.destroyAlaWebView(hiddenWebViewId);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_browser_back) {
            doBack();
            return;
        }
        if (id == R.id.btn_browser_close) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (id == R.id.btn_browser_option) {
//            showSettingPanel();
        }
        if (id == R.id.tv_error_retry) {
            if (webView != null) {
                loadBlank();
                webView.setVisibility(View.VISIBLE);
            }
            refreshWebView();
            if (errorNetworkView != null) {
                errorNetworkView.setVisibility(View.GONE);
            }
        }
    }

    public EbeiCreWebView getWebView() {
        return webView;
    }

    public EbeiCreWebChromeClient getEbeiCreWebChromeClient() {
        return ebeiCreWebChromeClient;
    }


    /**
     * 刷新h5
     */
    public void refreshWebView() {
        isError = false;
        String url = webView.getUrl();
        if (EbeiMiscUtils.isNotEmpty(url)) {
            String cutUrl;
            if (url.contains("?_appInfo")) cutUrl = url.substring(0, url.indexOf("?_appInfo"));
            else if (url.contains("&_appInfo")) cutUrl = url.substring(0, url.indexOf("&_appInfo"));
            else cutUrl = url;
            loadBuilder = new StringBuilder(cutUrl);
            //如果不是文件协议(或者使用的人说不加)才加后面一坨，否则不加
            addAppInfo();
            if (!showHiddenWebViewReal) {
                webView.loadUrl(loadBuilder.toString());
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (webView != null) {
                            webView.clearHistory();
                            updateTitle(webView.getTitle());
                        }
                    }
                }, 1000);
            }
        }
    }

    private void addAppInfo() {
        Uri uri = Uri.parse(loadBuilder.toString());
        if (((uri.getHost() != null && uri.getHost().contains(EbeiCreProtocol.ROOT_H5_HOST))
                || (uri.getHost() != null && uri.getHost().contains(EbeiCreProtocol.ROOT_IP))) && !useOriginalUrl) {
            Map<String, String> map = new HashMap<>();
            userName = EbeiConfig.getEbeiAccountProvider().getUserName();
            map.put(EbeiUrl.USER_NAME, userName);
            if (EbeiConfig.isLand()) {
                userToken = EbeiConfig.getEbeiAccountProvider().getUserToken();
                map.put(EbeiUrl.SIGN, userToken);
            }
            if (!EbeiApplication.isLogin) {
                map.put("isLogin", "false");
            }
            EbeiUrl.buildJsonUrl(loadBuilder, "4.3", map, false, null);
        }

        /*if ((loadBuilder.toString().contains(EbeiCreProtocol.ROOT_H5) || loadBuilder.toString().contains(EbeiCreProtocol.ROOT_WEB_PATH) || loadBuilder.toString().contains(EbeiCreProtocol.ROOT_APP_GOODS))
                || loadBuilder.toString().contains(EbeiCreProtocol.ROOT_H5_HOST) && !useOriginalUrl) {
            Map<String, String> map = new HashMap<>();
            userName = EbeiConfig.getEbeiAccountProvider().getUserName();
            map.put(EbeiUrl.USER_NAME, userName);
            if (EbeiConfig.isLand()) {
                userToken = EbeiConfig.getEbeiAccountProvider().getUserToken();
                map.put(EbeiUrl.SIGN, userToken);
            }
            EbeiUrl.buildJsonUrl(loadBuilder, "4.3", map, false, null);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}