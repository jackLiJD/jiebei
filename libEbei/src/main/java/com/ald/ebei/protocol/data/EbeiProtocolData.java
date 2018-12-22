package com.ald.ebei.protocol.data;

import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ald.ebei.ui.EbeiCreWebView;

import java.util.HashMap;
import java.util.UUID;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:35
 * 描述：EbeiProtocolData
 * 修订历史：
 */
public class EbeiProtocolData {

    public Dialog dialog;
    public ProgressBar progressBar;
    public boolean[] isWebviewShowed;
    public boolean showTitleBar;
    public Uri ecaUri;
    public EbeiCreWebView webView;
    public ViewGroup viewGroup;
    public boolean openNewWindow;
    public Boolean offLine;
    public int animStyle;
    public String shareName;
    public String shareKey;
    public EbeiShareData ebeiShareData;
    public String netWorkFirstKey;
    public String callbackName;
    public boolean openWindowWithAnim;// open协议打开新activity时是否要显示动画
    public HashMap<String, Integer> stateMap;// 检查本地安装软件时要用到的map
    public View toolBar;// 工具栏对象
    public String httpId = UUID.randomUUID().toString().replaceAll("-", "");
    public String webUrl;
    /**
     * open协议时，是否需要将该url视为下载url，并通过app拦截处理，默认是不拦截
     */
    public boolean interceptorUrlAsDownload;
    /**
     * 是否需要开启页面显示时长统计
     */
    public boolean needStatisticsPageTime;
    /**
     * 事件统计的id
     */
    public String statisticsEventId;
    /**
     * 事件统计的名称
     */
    public String statisticsEventName;

}

