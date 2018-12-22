package com.ald.ebei.util;

import com.ald.ebei.BuildConfig;
import com.ald.ebei.config.EbeiConfig;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/28 19:33
 * 描述：
 * 修订历史：
 */
public class EbeiConstant {
    public final static int DIALOG_MAX_DP_HEIGHT = 438;
    public final static String H5_URL_HELP = "fanbei-web/app/help";//帮助中心
    public final static String H5_URL_WIN_RANK = "fanbei-web/app/inviteLastWinRank";//邀请有礼上月中奖排行
    public static final String APP_PREFERENCE_NAME = "ala_51fanbei";
    public static final String AGREEMENT_BILL_TYPE = "agreement_bill_type";
    public static final String AGREEMENT_BILL_ID = "agreement_bill_id";
    public static final String S_TEST_CHENWU = "http://192.168.154.88:1112/app/open/";
    public static final String S = "https://eca.51fanbei.com/app/open/";
    public static final String S_PREVIEW = "https://yeca.51fanbei.com/app/open/";
    public static final String S_TEST = "http://192.168.156.91:9001/";
    public static final String S_TEST_xieqiang = "http://192.168.107.148:1112";
    public static final String S_TEST_wujun = "http://192.168.107.227:1112";
    public static final String S_TEST_renchunlei = "http://192.168.107.234:1112";
    public static final String S_TEST_zhourui = "http://192.168.107.87:1112";
    public static final String S_TEST_zhujunwei = "http://192.168.155.118:1112/app/open/";
    public static final String S_TEST_WB_INDEX = BuildConfig.EBEI_DEBUG ? "http://192.168.110.121:8080/#/appIndex" : "https://ecaweb.51fanbei.com/#/appIndex";
    public static final String S_TEST_WB_PROTOCOL = EbeiConfig.getEbeiServerProvider().getH5Server()+"register_protocol?type=close";
    //H5Url
    public static final String S_MENTH_HISTORY = EbeiConfig.getEbeiServerProvider().getH5Server() + "loan_history";
    public static final String HELP_CENTER = EbeiConfig.getEbeiServerProvider().getH5Server() + "help";
    public static final String REPAY_HISTORY = EbeiConfig.getEbeiServerProvider().getH5Server() + "repay_history";
    public static final String REPAY_OTHER = EbeiConfig.getEbeiServerProvider().getH5Server() + "repay_other";
    public static final String URL_AUTH = EbeiConfig.getEbeiServerProvider().getH5Server() + "auth";
    public static final String URL_ABOUT_US = EbeiConfig.getEbeiServerProvider().getH5Server() + "AboutUs";

    public static final String APP_SECRET = "c1dce5aa9e194e70";
    public static final String APP_KEY_PARAM = "params";
    public static final String APP_ID = "dsed";
    //存储手机号码字段
    public static final String APP_PHONE = "phone";
    //存储token字段
    public static final String APP_TOKEN = "token";
    public static final String APP_ISLOGIN = "isLogin";
    public static final String BROADCAST_ACTION_LOGIN="broadcast_action_login";


    //帮助中心问题id
    public static int QUESTION_ID;


    //埋点相关
    public static final String APP_KEY_POINT_PAGE = "page";
    public static final String APP_KEY_POINT_EVENT = "event";
    public static final String APP_PAGE_ID_CARD_SCAN = "id_card_scan";
    public static final String APP_PAGE_FACE_LIVENESS = "face_liveness";
    public static final String APP_PAGE_ASK_PERMISSION_CAMERA = "ask_permission_camera";
    public static final String APP_ASK_PERMISSION_CAMERA_SUCCESS = "permission_camera_allow";
    public static final String APP_ASK_PERMISSION_CAMERA_FAILED = "permission_camera_refuse";
    public static final String APP_PAGE_ASK_PERMISSION_LOCATION = "ask_permission_location";
    public static final String APP_ASK_PERMISSION_LOCATION_SUCCESS = "permission_location_allow";
    public static final String APP_ASK_PERMISSION_LOCATION_FAILED = "permission_location_refuse";
    public static final String APP_PAGE_ASK_PERMISSION_CONTACTS = "ask_permission_contacts";
    public static final String APP_ASK_PERMISSION_CONTACTS_SUCCESS = "permission_contacts_allow";
    public static final String APP_ASK_PERMISSION_CONTACTS_FAILED = "permission_contacts_refuse";

    public static final String APP_KEY_BANK_CARD_MANAGER_SELECT_TYPE = "select_type";

    //借款相关
    public static final String APP_LOAN_AMOUNT = "loan_amount";
    public static final String APP_LOAN_PERIOD = "loan_period";
    public static final String APP_LOAN_MONTH_AMOUNT = "loan_month_amount";
    public static final String APP_LOAN_BANK_NAME = "loan_bank_name";
    public static final String APP_LOAN_HAD_BANK_CARD = "loan_had_bank_card";
    public static final String APP_LOAN_BANK_CARD_NO = "loan_month_bank_card_no";
}
