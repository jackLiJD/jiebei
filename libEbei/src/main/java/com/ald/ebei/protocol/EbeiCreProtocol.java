package com.ald.ebei.protocol;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 14:31
 * 描述：自定义web和Android交互协议
 * 修订历史：
 */
public class EbeiCreProtocol {
    public static final String GROUP_MOBILE_OPERATOR = "/fanbei-mobile";//手机运营商认证
    public static final String GROUP_ZMXY = "/app/zmxy/notify";
    public static final String GROUP_SELF = "/fanbei-self";
    public static final String ROOT_WEB_PATH = "/fanbei-web";
    public static final String ROOT_APP_GOODS = "/app/goods";
    public static final String ROOT_H5 = "/h5";
    public static final String ROOT_H5_HOST = "ebei";//包含E呗域名的
    public static final String ROOT_IP = "192.168";//包含E呗本地域名的
    public static final String ROOT_APP_PATH = "/fanbei-app";
    public final static String NATIVE_LOGIN_FOR_WEB = "APP_LOGIN";
    public final static String NATIVE_H5_ACTIVITY = "GG"; // H5_URL配置页面统一跳转前缀

    public interface Protocol4Js {
        String HOST_INFO = ROOT_WEB_PATH + "/hostinfo";
        String APP_ROOT_STORAGE = ROOT_WEB_PATH + "approot.storage";
        String APPLET_CHECK = ROOT_WEB_PATH + "/applet/check";
        String APPLET_INSTALL = "/applet/install";
        String APPLET_START = ROOT_WEB_PATH + "/applet/start";
        String SHOW = ROOT_WEB_PATH + "/show";
        String OPEN = ROOT_WEB_PATH + "/open";
        String CLOSE = ROOT_WEB_PATH + "/close";
        String DESTROY = ROOT_WEB_PATH + "/destory";
        String CHANGE_MODE = ROOT_WEB_PATH + "/changemode";
        String NETWORK_MODE = ROOT_WEB_PATH + "/networkmode";
        String CALL_PHONE = ROOT_WEB_PATH + "/callphone";
        String ALERT = ROOT_WEB_PATH + "/alert";
        String TOAST = ROOT_WEB_PATH + "/toast";
        String DIALOG = ROOT_WEB_PATH + "/dialog";
        String DIAL_PHONE = ROOT_WEB_PATH + "/dialphone";
        String GO_BACK = ROOT_WEB_PATH + "/goback";
        String TOOL_BAR = ROOT_WEB_PATH + "/toolbar";
        String SHARE = ROOT_WEB_PATH + "/share";
        String OPEN_NATIVE = ROOT_WEB_PATH + "/opennative";
    }

    public interface Protocol {
        String SHOW = ROOT_WEB_PATH + "/show";
        String OPEN = ROOT_WEB_PATH + "/open";
        String CLOSE = ROOT_WEB_PATH + "/close";
        String DESTROY = ROOT_WEB_PATH + "/destory";
        String CALL_PHONE = ROOT_WEB_PATH + "/callphone";
        String SHARE = ROOT_WEB_PATH + "/share";
        String OPEN_NATIVE = ROOT_WEB_PATH + "/opennative";

        String HOST_INFO = ROOT_WEB_PATH + "/hostinfo";
        String APP_ROOT_STORAGE = ROOT_WEB_PATH + "approot.storage";
        String APPLET_CHECK = ROOT_WEB_PATH + "/applet/check";
        String APPLET_INSTALL = ROOT_WEB_PATH + "/applet/install";
        String APPLET_START = ROOT_WEB_PATH + "/applet/start";
        String CHANGE_MODE = ROOT_WEB_PATH + "/changemode";
        String NETWORK_MODE = ROOT_WEB_PATH + "/networkmode";
        String ALERT = ROOT_WEB_PATH + "/alert";
        String TOAST = ROOT_WEB_PATH + "/toast";
        String DIALOG = ROOT_WEB_PATH + "/dialog";
        String DIAL_PHONE = ROOT_WEB_PATH + "/dialphone";
        String GO_BACK = ROOT_WEB_PATH + "/goback";
        String TOOL_BAR = ROOT_WEB_PATH + "/toolbar";
        //芝麻信用接口的回调参数
        String ZMXY_KEY = "/app/zmxy/notify";
        //手机运营商提交数据
        String MOBILE_SUCCESS_KEY = "/app/sys/mobileOperator";
        //手机运营商提交数据返回
        String MOBILE_BACK_KEY = "/app/sys/authBack";
        // 51公积金
        String WUYAO_FUND_STATUS = "/newFund/giveBack";
        //公信宝标识
        String GXB_BACK = "gxbAuthBackUrl";

    }


}
