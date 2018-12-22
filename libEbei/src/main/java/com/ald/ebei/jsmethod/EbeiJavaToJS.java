package com.ald.ebei.jsmethod;

/*
 * Created by sean yu on 2017/5/23.
 */

public class EbeiJavaToJS {
    static String RECEIVE_LOCATION = "javascript:getLocationMsg('" + "%1$s" + "')";
    static String RECEIVE_LOCATION_WITHOUT_PARAM = "javascript:getLocationMsg()";
    static String RECEIVE_DEVICEINFO = "javascript:getDeviceInfoMsg('" + "%1$s" + "')";
    static String RECEIVE_CONTACTS = "javascript:getContactsMsg('" + "%1$s" + "')";
    static String RECEIVE_CONTACTS_WITHOUT_PARAM = "javascript:getContactsMsg()";
    static String RECEIVE_FORWARD = "javascript:getForwardInfoMsg('" + "%1$s" + "')";


    public static class KITKAT_JS {
        public static String GET_SHARE_DATA = "javascript:alert(alaShareData())";
        public static String RECEIVE_AUTH_FINISH = "javascript:updateAuthStatus('" + "%1$s" + "')";
    }
}
