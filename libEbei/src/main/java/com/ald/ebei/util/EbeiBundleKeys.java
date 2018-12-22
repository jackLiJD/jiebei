package com.ald.ebei.util;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/10 13:30
 * 描述：intent传值时的key
 * 修订历史：
 */
public class EbeiBundleKeys {
    public static final int REQUEST_CODE_LOGIN = 0x1001;
    public static final int REQUEST_CODE_STAGE_RR_IDF_FRONT = 0x0401;
    public static final int REQUEST_CODE_STAGE_RR_IDF_BACK = 0x0402;
    public static final int REQUEST_CODE_BANK_CARD_ADD = 0x1026;
    public static final int REQUEST_CODE_BANK_CARD_EDIT = 0x1027;
    public static final int REQUEST_CODE_BANK_SUPPORT_BANK = 0x1028;
    public static final int REQUEST_CODE_BANK_SELECT = 0x1029;
    public static final int REQUEST_CODE_SEND_SMS = 0x2019;
    public static final int REQUEST_CODE_AUTH = 0x2020;
    public static final int REQUEST_CODE_LOAN = 0x2021;
    public static final int REQUEST_CODE_REPAYMENT = 0x2022;
    public static final int REQUEST_CODE_REPAYMENT_SUCCESS = 0x2023;
    //主页面跳转tab
    public static final String MAIN_DATA_TAB = "main_data_tab";

    //实名认证逻辑、设置支付密码、添加银行卡结束时跳转
    public static final String STAGE_JUMP = "stage_jump";
    //意图身份证信息
    public static final String RR_IDF_MODEL = "rr_idf_model";

    public static final String INTENT_KEY_CREDIT_PROMOTE_SCENE = "credit_promote_scene";//认证场景

}
