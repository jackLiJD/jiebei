package com.ald.ebei.util.log;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/4/8 17:02
 * <p/>
 * Description:
 */
public enum EbeiDateFormatter {
    NORMAL("yyyy-MM-dd HH:mm"),
    AA("MM月dd日 HH:mm"),
    DD("yyyy-MM-dd"),
    SS("yyyy-MM-dd HH:mm:ss"),
    TT("yyyy/MM/dd  HH:mm:ss");
    private String value;

    EbeiDateFormatter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
