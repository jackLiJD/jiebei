package com.ald.ebei.util;

/**
 * 枚举类
 * Created by ywd on 2018/11/23.
 */

public enum EbeiModelEnum {
    Y("Y", 1, "true"), //已认证
    N("N", 0, "false"),//未认证
    P("P", 2, "default"),//认证场景：认证中

    T("T", 1, "true"),
    F("F", 0, "false"),
    A("A", 3, "normal"),//认证场景：未认证

    W("W", 1, "认证中"),

    Q("Q", 2, "等待查询"),

    E("E", 3, "已过期"),

    R("R", 4, "重新提交"),//cash场景补充认证

    S("S", 5, "禁止期"),//cash场景补充认证，X天后重新认证

    //借款审核状态
    NO_LOAN("NO_LOAN", 0, "无借款"),
    REVIEW("REVIEW", 1, "审核中"),
    WAIT_LOAN("WAIT_LOAN", 2, "待放款"),
    AGREE("AGREE", 3, "审核通过"),

    //借款状态
    WAIT_REPAY("WAIT_REPAY", 0, "待还款"),
    WAIT_PROCE("WAIT_PROCE", 1, "还款处理中"),
    OVERDUE("OVERDUE", 2, "已逾期"),
    CURR_FINISH("CURR_FINISH", 3, "本期已结清"),

    REGIST("REGIST", 0, "注册短信"),
    LOGIN("LOGIN", 1, "登录"),
    FORGET_PASSWD("FORGET_PASSWD", 2, "忘记密码验证短信"),
    RESET_ACC_PWD("RESET_ACC_PWD", 3, "重置账户密码"),
    LOAN_CONFIRM("LOAN_CONFIRM", 4, "借款确认短信"),
    REPAY_CONFIRM("REPAY_CONFIRM", 5, "还款确认短信");

    EbeiModelEnum(int value) {
        this.value = value;
    }


    private String model;
    private int value;
    private String desc;

    EbeiModelEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    EbeiModelEnum(String model, int value, String desc) {
        this.model = model;
        this.value = value;
        this.desc = desc;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 通过Desc获取到model
     *
     * @param desc 描述
     */
    public static String DescToModel(String desc) {
        for (EbeiModelEnum ebeiModelEnum : EbeiModelEnum.values()) {
            if (ebeiModelEnum != null && EbeiMiscUtils.isEquals(ebeiModelEnum.getDesc(), desc)) {
                return ebeiModelEnum.getModel();
            }
        }
        return EbeiModelEnum.Y.getModel();
    }
}
