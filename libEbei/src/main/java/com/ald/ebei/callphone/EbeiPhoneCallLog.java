package com.ald.ebei.callphone;

import java.io.Serializable;

/**
 * Created by Jacky on 2016/5/28.
 */
public class EbeiPhoneCallLog extends EbeiPhoneCallRequest implements Serializable {
    //don't have a chance yet to examine this call's duration;
    public static final int DURATION_CALL_LOG_NOT_EXAMINED = -2;
    // this status is client side status, server side cannot have this status
    public static final int DURATION_CALL_LOG_NOT_FOUND = -1;
    public static final int DURATION_CALL_NOT_ANSWERED = 0;

    public static final int CONFIRMED_NOT_SET = -1;
    public static final int CONFIRMED_YES = 1;
    public static final int CONFIRMED_NO = 0;

    private int duration = DURATION_CALL_LOG_NOT_FOUND;

    private int confirmed;   //是否被用户confirmed？ 配合needConfirm一起使用。 -1表示未设置，1表示确认，0表示取消。
    private long nowTime;


    //this constructor is only for FastJson
    public EbeiPhoneCallLog() {
    }

    public EbeiPhoneCallLog(EbeiPhoneCallRequest request) {
        this(request, DURATION_CALL_LOG_NOT_EXAMINED);
    }

    public EbeiPhoneCallLog(EbeiPhoneCallRequest request, int duration) {
        super(request);
        this.duration = duration;
        this.confirmed = CONFIRMED_NOT_SET;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EbeiPhoneCallLog)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        EbeiPhoneCallLog that = (EbeiPhoneCallLog) o;

        return duration == that.duration;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + duration;
        return result;
    }
}
