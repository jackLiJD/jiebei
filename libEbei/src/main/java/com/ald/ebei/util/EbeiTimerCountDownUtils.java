package com.ald.ebei.util;

import android.os.CountDownTimer;

/**
 * 最简单的倒计时工具类,实现了官方的CountDownTimer类(没有特殊要求的话可以使用)
 * 因为创建了后台线程,即使退出activity,倒计时还能进行
 * 有onTick、onFinish、cancel和start方法
 * <p>
 * Created by yaowenda on 17/3/27.
 */

public abstract class EbeiTimerCountDownUtils extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public EbeiTimerCountDownUtils(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    public abstract String toClock(long millisUntilFinished);

    @Override
    public void onFinish() {

    }

    public String toClock(long millisUntilFinished, String txt) {
        long hour = millisUntilFinished / (60 * 60 * 1000);
        long minute = (millisUntilFinished - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (millisUntilFinished - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;

        if (second >= 60) {
            second = second % 60;
            minute += second / 60;
        }

        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }

        String showHour = "";
        String showMinute = "";
        String showSecond = "";

        if (hour < 10) {
            showHour = "0" + String.valueOf(hour);
        } else {
            showHour = String.valueOf(hour);
        }

        if (minute < 10) {
            showMinute = "0" + String.valueOf(minute);
        } else {
            showMinute = String.valueOf(minute);
        }

        if (second < 10) {
            showSecond = "0" + String.valueOf(second);
        } else {
            showSecond = String.valueOf(second);
        }
        return showSecond + txt;
    }

    /**
     * 商圈订单倒计时
     *
     * @param millisUntilFinished
     * @return
     */
    public String toClockForTrade(long millisUntilFinished) {
        long hour = millisUntilFinished / (60 * 60 * 1000);
        long minute = (millisUntilFinished - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (millisUntilFinished - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;

        if (second >= 60) {
            second = second % 60;
            minute += second / 60;
        }

        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }

        String showHour = "";
        String showMinute = "";
        String showSecond = "";

        if (hour < 10) {
            showHour = "0" + String.valueOf(hour);
        } else {
            showHour = String.valueOf(hour);
        }

        if (minute < 10) {
            showMinute = "0" + String.valueOf(minute);
        } else {
            showMinute = String.valueOf(minute);
        }

        if (second < 10) {
            showSecond = "0" + String.valueOf(second);
        } else {
            showSecond = String.valueOf(second);
        }
        return "请在" + showHour + ":" + showMinute + ":" + showSecond + "内支付";
    }

}
