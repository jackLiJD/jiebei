package com.ald.ebei.network.exception;


import com.ald.ebei.util.EbeiMiscUtils;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/13 20:13
 * 描述：
 * 修订历史：
 */
public enum EbeiApiExceptionEnum {

    EMPTY("EMPTY", 998, "empty", "对象为空"),
    // SERVICE 9999
    SYSTEM_ERROR("SYSTEM_ERROR", 9999, "system error", "服务器操作错误"),
    SUCCESS("SUCCESS", 1000, "success", "成功"),

    // PARAM_CODE 1001-1099
    TOKEN_INVALID("TOKEN_INVALID", 1001, "token invalid", "token失效"),
    REQUEST_PARAM_NOT_EXIST("REQUEST_PARAM_NOT_EXIST", 1002, "request param is invalid", "请求参数缺失"),
    REQUEST_PARAM_METHOD_NOT_EXIST("REQUEST_PARAM_METHOD_NOT_EXIST", 1003, "request method is invalid", "请求方法不存在"),
    REQUEST_PARAM_TOKEN_ERROR("REQUEST_PARAM_TOKEN_ERROR", 1004, "token is invalid", "您未登录，请登录"),
    REQUEST_INVALID_SIGN_ERROR("REQUEST_INVALID_SIGN_ERROR", 1005, "sign is invalid", "您的账号可能在其他设备上登录\n请重新登录"),
    REQUEST_PARAM_ERROR("REQUEST_PARAM_ERROR", 1006, "request param error", "请求参数不正确"),
    REQUEST_PARAM_METHOD_ERROR("REQUEST_PARAM_METHOD_ERROR", 1007, "request method param error", "请求方法不正确"),
    REQUEST_PARAM_SYSTEM_NOT_EXIST("REQUEST_PARAM_SYSTEM_NOT_EXIST", 1008, "system param is invalid", "系统参数缺失"),
    CALCULATE_SHA_256_ERROR("CALCULATE_SHA_256_ERROR", 1009, "cal sha 265 error", "系统错误"),
    SYSTEM_REPAIRING_ERROR("SYSTEM_REPAIRING_ERROR", 1010, "system repairing", "系统维护中"),

    // user mode code from 1100 - 1199
    USER_NOT_EXIST_ERROR("USER_NOT_EXIST_ERROR", 1100, "user not exist error", "用户不存在"),
    USER_INVALID_MOBILE_NO("USER_INVALID_MOBILE_NO", 1101, "invalid mobile number", "无效手机号"),
    USER_HAS_REGIST_ERROR("USER_HAS_REGIST_ERROR", 1102, "user has been regist", "该号码已经注册"),
    USER_PASSWORD_ERROR("USER_PASSWORD_ERROR", 1103, "user or password error", "用户名或密码不正确"),
    USER_PASSWORD_ERROR_GREATER_THAN5("USER_PASSWORD_ERROR_GREATER_THAN5", 1104, "user password error count to max", "密码错误次数超过限制锁定2小时"),
    USER_REGIST_SMS_NOTEXIST("USER_REGIST_SMS_NOTEXIST", 1105, "user regist sms not exist", "验证码不正确"), // 验证码错误,请重新输入
    USER_REGIST_SMS_ERROR("USER_REGIST_SMS_ERROR", 1106, "user regist sms error", "验证码不正确"),
    USER_REGIST_SMS_ALREADY_ERROR("USER_REGIST_SMS_ALREADY_ERROR", 1110, "user regist sms already error", "验证码已验证"),
    USER_PWD_INPUT_ERROR("USER_PWD_INPUT_ERROR", 8003, "pwd error", "密码输入有误，剩余次数x"),
    USER_PWD_FORBID("USER_PWD_FORBID", 8004, "pwd error", "密码冻结"),

    USER_REGIST_SMS_OVERDUE("USER_REGIST_SMS_OVERDUE", 1107, "user regist sms overdue", "验证码已经过期"), // 您的验证码已超时,请重新获取验证码
    USER_REGIST_ACCOUNT_EXIST("USER_REGIST_ACCOUNT_EXIST", 1108, "user regist account exist", "用户已存在"),

    USER_SEND_SMS_ERROR("USER_SEND_SMS_ERROR", 1109, "user send sms error", "用户发送验证码失败"),

    USER_DUPLICATE_INVITE_CODE("USER_DUPLICATE_INVITE_CODE", 1111, "user duplicate commit invite code", "用户已经重复输入邀请码"),
    COMMIT_INVITE_CODE_EXPIRE_TIME("COMMIT_INVITE_CODE_EXPIRE_TIME", 1112, "commit invite code expire time", "输入邀请码时限已经超过72小时"),
    LIMIT_INVITE_EACH_OTHER("LIMIT_INVITE_EACH_OTHER", 1113, "limit invite each other", "限制互相推荐"),
    CODE_NOT_EXIST("CODE_NOT_EXIST", 1114, "code not exist", "您输入的邀请码不存在"),
    LIMIT_INVITE_SELF("LIMIT_INVITE_SELF", 1115, "limit invite self", "您不能邀请自己"),
    CAN_NOT_APPLY_CASHED("CAN_NOT_APPLY_CASHED", 1116, "can not apply cashed", "系统维护中，暂不能申请提现"),
    APPLY_CASHED_AMOUNT_ERROR("APPLY_CASHED_AMOUNT_ERROR", 1117, "apply cashed amount invalid", "申请的金额无效"),
    USER_ACCOUNT_NOT_EXIST_ERROR("USER_ACCOUNT_NOT_EXIST_ERROR", 1118, "user not exist error", "账户不存在"),
    USER_ACCOUNT_IDNUMBER_INVALID_ERROR("USER_ACCOUNT_IDNUMBER_INVALID_ERROR", 1119, "id number error", "身份证输入有误"),
    USER_PAY_PASSWORD_INVALID_ERROR("USER_PAY_PASSWORD_INVALID_ERROR", 1120, "pay password error", "支付密码输入有误"),
    APPLY_CASHED_AMOUNT_MORE_ACCOUNT("APPLY_CASHED_AMOUNT_MORE_ACCOUNT", 1121, "apply cash amount more than account money", "申请金额大于账户可提现金额"),
    APPLY_CASHED_BANK_ERROR("APPLY_CASHED_BANK_ERROR", 1122, "apply cash bank id error", "该卡不支持提现"),
    APPLY_CASHED_ZHIFUBAO_ERROR("APPLY_CASHED_ZHIFUBAO_ERROR", 1123, "apply cash zhifubao error", "支付宝账号输入有误"),

    USER_FROZEN_ERROR("USER_FROZEN_ERROR", 1124, "user frozen error", "用户冻结中"),

    USER_PASSWORD_ERROR_FIRST("USER_PASSWORD_ERROR_FIRST", 1131, "user password error first", "密码输入有误,剩余次数(5)"),
    USER_PASSWORD_ERROR_SECOND("USER_PASSWORD_ERROR_SECOND", 1132, "user password error second", "密码输入有误,剩余次数(4)"),
    USER_PASSWORD_ERROR_THIRD("USER_PASSWORD_ERROR_THIRD", 1133, "user password error third", "密码输入有误,剩余次数(3)"),
    USER_PASSWORD_ERROR_FOURTH("USER_PASSWORD_ERROR_FOURTH", 1134, "user password error fourth", "密码输入有误,剩余次数(2)"),
    USER_PASSWORD_ERROR_FIFTH("USER_PASSWORD_ERROR_FIFTH", 1135, "user password error fifth", "密码输入有误,剩余次数(1)"),
    USER_PASSWORD_ERROR_ZERO("USER_PASSWORD_ERROR_FIFTH", 1136, "user password error fifth", "密码输入有误,剩余次数(6)"),
    USER_PASSWORD_OLD_ERROR("USER_PASSWORD_OLD_ERROR", 1137, "user password error fifth", "旧密码输入有误"),

    USER_GET_COUPON_ERROR("USER_GET_COUPON_ERROR", 1200, "user coupon error ", "优惠券已领取"),
    USER_COUPON_ERROR("USER_COUPON_ERROR", 1201, "user coupon error ", "优惠券不可用"),
    USER_SIGNIN_AGAIN_ERROR("USER_SIGNIN_AGAIN_ERROR", 1210, "user coupon error ", "今日已签到"),
    USER_COUPON_NOT_EXIST_ERROR("USER_COUPON_NOT_EXIST_ERROR", 1211, "user coupon error ", "优惠券不存在"),
    USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR("USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR", 1212, "user coupon error ", "优惠券个数超过最大领券个数"),
    USER_COUPON_PICK_OVER_ERROR("USER_COUPON_PICK_OVER_ERROR", 1213, "pick coupon over error ", "优惠券已领取完"),

    USER_CASH_MONEY_ERROR("USER_CASH_MONEY_ERROR", 1300, "user cash money error", "取现金额超过上限"),
    USER_MAIN_BANKCARD_NOT_EXIST_ERROR("USER_MAIN_BANKCARD_NOT_EXIST_ERROR", 1301, "user main bankcard not exist error", "您未绑定主卡"),
    USER_BANKCARD_NOT_EXIST_ERROR("USER_BANKCARD_NOT_EXIST_ERROR", 1302, "user bankcard not exist error", "用户银行卡不存在"),
    USER_FACE_AUTH_ERROR("USER_FACE_AUTH_ERROR", 1303, "user face auth error", "用户未通过人脸识别"),
    USER_BANKCARD_EXIST_ERROR("USER_BANKCARD_EXIST_ERROR", 1304, "user bankcard exist error", "用户银行卡已被绑定"),
    // third mode code 1500-1599
    JPUSH_ERROR("JPUSH_ERROR", 1500, "jpush error", "推送失败"),

    ZM_ERROR("ZM_ERROR", 1510, "zm error", "调用芝麻信用失败"),
    ZM_AUTH_ERROR("ZM_AUTH_ERROR", 1511, "zm auth error", "芝麻信用授权失败"),
    //    ZM_CREDIT_WATCHLISTII_ERROR("ZM_CREDIT_WATCHLISTII_ERROR",1511,"zm credit watchlistii error","调用芝麻行业关注名单失败"),
//    ZM_CREDIT_SCORE_GET_ERROR("ZM_CREDIT_SCORE_GET_ERROR",1512,"zm credit score get error","调用芝麻信用评分失败"),
    DEALWITH_YOUDUN_NOTIFY_ERROR("DEALWITH_YOUDUN_NOTIFY_ERROR", 1520, "dealwith youdun notify error", "有盾回调出错"),

    SMS_MOBILE_NO_ERROR("SMS_MOBILE_NO_ERROR", 1530, "invalid mobile", "无效手机号"),
    SMS_MOBILE_COUNT_TOO_MANAY("SMS_MOBILE_COUNT_TOO_MANAY", 1531, "too manay mobiles", "手机号太多"),
    SMS_MOBILE_ERROR("SMS_MOBILE_ERROR", 1532, "too manay mobiles", "手机号有误"),


    AUTH_REALNAME_ERROR("AUTH_REALNAME_ERROR", 1540, "auth realname error", "实名认证失败"),
    AUTH_CARD_ERROR("AUTH_CARD_ERROR", 1541, "auth card error", "银行卡认证失败"),
    AUTH_BINDCARD_ERROR("AUTH_BINDCARD_ERROR", 1542, "bind card error", "绑定银行卡失败"),

    UPS_AUTH_BF_SIGN_ERROR("UPS_AUTH_BF_SIGN_ERROR", 1550, "bao fu auth error", "银行卡认证失败"),
    UPS_AUTH_YSB_SIGN_ERROR("UPS_AUTH_YSB_SIGN_ERROR", 1551, "bao fu auth error", "银行卡认证失败"),
    UPS_DELEGATE_PAY_ERROR("UPS_DELEGATE_PAY_ERROR", 1552, "ups delegate pay error", "单笔代付失败"),
    UPS_AUTH_PAY_ERROR("UPS_AUTH_PAY_ERROR", 1553, "ups auth pay error", "认证支付失败"),
    UPS_AUTH_PAY_CONFIRM_ERROR("UPS_AUTH_PAY_CONFIRM_ERROR", 1554, "ups auth pay confirm error", "支付认证确认失败"),
    UPS_QUERY_TRADE_ERROR("UPS_QUERY_TRADE_ERROR", 1555, "ups query trade error", "单笔交易查询失败"),
    UPS_AUTH_SIGN_ERROR("UPS_AUTH_SIGN_ERROR", 1556, "ups auth sign error", "签约失败"),
    UPS_AUTH_SIGN_VALID_ERROR("UPS_AUTH_SIGN_VALID_ERROR", 1557, "ups auth sign valid error", "签约短信验证失败"),
    UPS_QUERY_AUTH_SIGN_ERROR("UPS_QUERY_AUTH_SIGN_ERROR", 1558, "ups query auth sign error", "查询签约验证失败"),
    UPS_SIGN_DELAY_ERROR("UPS_SIGN_DELAY_ERROR", 1559, "ups sign delay error", "协议延期失败"),
    UPS_COLLECT_ERROR("UPS_COLLECT_ERROR", 1560, "ups collect error", "单笔代收失败"),
    UPS_ORDERNO_BUILD_ERROR("UPS_ORDERNO_BUILD_ERROR", 1561, "ups order build error", "构建订单错误"),
    REQ_WXPAY_ERR("REQ_WXPAY_ERR", 1562, "request wx error", "微信app支付失败"),
    BANK_CARD_PAY_ERR("BANK_CARD_PAY_ERR", 1563, "bank card pay error", "银行卡支付失败"),
    BANK_CARD_PAY_SMS_ERR("BANK_CARD_PAY_SMS_ERR", 1564, "bank card pay sms error", "银行卡支付短信验证失败"),
    REFUND_ERR("REFUND_ERR", 1565, "refund error", "退款失败"),
    UPS_BATCH_DELEGATE_ERR("UPS_BATCH_DELEGATE_ERR", 1566, "ups batch delegate error", "批量代付失败"),
    AUTH_BINDCARD_SMS_ERROR("AUTH_BINDCARD_SMS_ERROR", 1567, "auth bindcard sms error", "短信验证码获取失败,请重试"),
    SIGN_RELEASE_ERROR("SIGN_RELEASE_ERROR", 1568, "sign release error", "银行卡解绑失败"),
    //order model 1600-1699
    USER_ORDER_NOT_EXIST_ERROR("USER_ORDER_NOT_EXIST_ERROR", 1600, "user order not exist error", "用户订单不存在"),
    GOODS_NOT_EXIST_ERROR("GOODS_NOT_EXIST_ERROR", 1601, "goods not exist error", "商品不存在"),
    GOODS_COLLECTION_ALREADY_EXIST_ERROR("GOODS_COLLECTION_ALREADY_EXIST_ERROR", 1602, "goods not exist error", "商品已经收藏"),

    //borrow model 1700-1799
    BORROW_CONSUME_NOT_EXIST_ERROR("BORROW_CONSUME_NOT_EXIST_ERROR", 1701, "borrow consume not exist error", "分期未配置"),
    BORROW_CONSUME_MONEY_ERROR("BORROW_CONSUME_MONEY_ERROR", 1702, "borrow consume money error", "分期金额超过上限"),
    BORROW_BILL_NOT_EXIST_ERROR("BORROW_BILL_NOT_EXIST_ERROR", 1703, "borrow bill not exist error", "账单不存在"),
    BORROW_BILL_UPDATE_ERROR("BORROW_BILL_UPDATE_ERROR", 1704, "borrow bill update error", "用户账单已更新"),
    BORROW_DETAIL_NOT_EXIST_ERROR("BORROW_DETAIL_NOT_EXIST_ERROR", 1705, "borrow detail not exist error", "借款详情不存在"),
    REPAYMENT_DETAIL_NOT_EXIST_ERROR("REPAYMENT_DETAIL_NOT_EXIST_ERROR", 1706, "repayment detail not exist error", "还款详情不存在"),

    //h5 1800-1900
    RESOURES_H5_ERROR("RESOURES_H5_ERROR", 1800, "resoures h5 not exist error", "信息不存在，请联系管理员"),

    REPAYMENT_LOAN_ERROR("REPAYMENT_LOAN_ERROR", 2004, "repayment info error", "还有一笔还款正在处理中"),

    SMS_LOGIN_EXCEED_TIME("获取短信次数超限,请尝试账号密码登录", 1145),
    USER_REGIST_SMS_LESSDUE("您已获取过验证码,请稍后重试", 1147),
    EXIST_MOBILE("手机号已存在", 4003),
    EMPTY_TOKEN("未授权/授权过期", 1001),
    CREDIT_PROMOT_OVERDUE("有逾期数据", 7007),
    SIGN_EEOR("签名错误", 9009);


    /**
     * 错误
     */
    private String exception;

    /**
     * 错误编号
     */
    private int errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误描述
     */
    private String desc;

    EbeiApiExceptionEnum(String errorMsg, int errorCode) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    private EbeiApiExceptionEnum(String code, int errorCode, String errorMsg, String desc) {
        this.exception = code;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.desc = desc;
    }

    public static EbeiApiExceptionEnum getByCode(String code) {
        if (EbeiMiscUtils.isEmpty(code)) {
            return null;
        }
        EbeiApiExceptionEnum[] errorCodes = values();

        for (EbeiApiExceptionEnum acsErrorCode : errorCodes) {
            if (code.equals(acsErrorCode.getException())) {
                return acsErrorCode;
            }
        }

        return null;
    }

    public String getException() {
        return exception;
    }

    public String getDesc() {
        return desc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
