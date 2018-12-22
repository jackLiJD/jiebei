package com.ald.ebei.callphone;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:46
 * 描述：
 * 修订历史：
 */
public class EbeiPhoneCallRequest implements Serializable {
    private String phone;
    private String group;
    private String source;
    private String label;
    private long callTime;
    private String extra;
    private boolean needConfirm;    //是否需要弹窗确认拨打电话？
    private boolean tryCallFirst;   // 是否直接拨打电话，还是跳转到拨号app？（上屏）

    //this constructor is only for FastJson
    protected EbeiPhoneCallRequest() {
    }

    public EbeiPhoneCallRequest(String phone, String group, String source) {
        this(phone, group, source, null);
    }

    public EbeiPhoneCallRequest(String phone, String group, String source, String label) {
        this(phone, group, source, label, null);
    }

    public EbeiPhoneCallRequest(String phone, String group, String source, String label, String extra) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(group) || TextUtils.isEmpty(source)) {
            throw new IllegalArgumentException("phonenumber, group and source must not be NULL");
        }
        this.phone = phone;
        this.group = group;
        this.source = source;
        this.label = label;
        this.extra = extra;

        this.callTime = System.currentTimeMillis();
    }

    public EbeiPhoneCallRequest(EbeiPhoneCallRequest from) {
        this.phone = from.phone;
        this.group = from.group;
        this.source = from.source;
        this.label = from.label;
        this.callTime = from.callTime;
        this.extra = from.extra;
        this.needConfirm = from.needConfirm;
        this.tryCallFirst = from.tryCallFirst;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPhone() {
        return phone;
    }

    public String getGroup() {
        return group;
    }

    public String getLabel() {
        return label;
    }

    public long getCallTime() {
        return callTime;
    }

    public String getExtra() {
        return extra;
    }

    public boolean isNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(boolean needConfirm) {
        this.needConfirm = needConfirm;
    }

    public boolean isTryCallFirst() {
        return tryCallFirst;
    }

    public void setTryCallFirst(boolean tryCallFirst) {
        this.tryCallFirst = tryCallFirst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EbeiPhoneCallRequest)) {
            return false;
        }

        EbeiPhoneCallRequest request = (EbeiPhoneCallRequest) o;

        if (!phone.equals(request.phone)) {
            return false;
        }
        if (!group.equals(request.group)) {
            return false;
        }
        if (source != null ? !source.equals(request.source) : request.source != null) {
            return false;
        }
        if (label != null ? !label.equals(request.label) : request.label != null) {
            return false;
        }
        if (callTime != request.callTime) {
            return false;
        }
        return !(extra != null ? !extra.equals(request.extra) : request.extra != null);

    }

    @Override
    public int hashCode() {
        int result = phone.hashCode();
        result = 31 * result + group.hashCode();
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + Long.valueOf(callTime).hashCode();
        result = 31 * result + (extra != null ? extra.hashCode() : 0);
        return result;
    }
}
