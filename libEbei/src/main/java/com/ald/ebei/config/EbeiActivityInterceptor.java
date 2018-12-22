package com.ald.ebei.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.ald.ebei.R;
import com.ald.ebei.util.EbeiActivityUtils;


/**
 * Created by Jacky Yu on 2015/11/17.
 */
public class EbeiActivityInterceptor {

    protected Activity activity;
    private EbeiStatNameProvider ebeiStatNameProvider;
    private EbeiSystemBarTintManager tintManager;
    private ILeftCycleListener listener;

    public EbeiActivityInterceptor(Activity activity, EbeiStatNameProvider ebeiStatNameProvider) {
        this.activity = activity;
        this.ebeiStatNameProvider = ebeiStatNameProvider;
    }

    //沉浸式状态栏的兼容api19时
    public void tintMangerInterceptor() {
        tintManager = new EbeiSystemBarTintManager(activity);
        tintManager.setStatusBarTintColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
    }

    public void onCreate(Bundle savedInstanceState) {
        EbeiActivityUtils.push(activity);
        EbeiConfig.setCurrentActivity(activity);
        //极光推送华为小米联盟
    }

    public void setListener(ILeftCycleListener listener) {
        this.listener = listener;
    }

    public void onNewIntent(Intent intent) {
        EbeiConfig.setCurrentActivity(activity);
    }

    public void onStart() {
    }

    public void onRestart() {
    }

    public void onResume() {
        EbeiConfig.setCurrentActivity(activity);

        EbeiActivityLeavedLongListener listener = EbeiConfig.getEbeiActivityLeavedLongListener();
        if (listener != null) {
            long lastPauseTime = EbeiConfig.getLastPauseTime();
            long lastAdTime = EbeiConfig.getLastAdTime();
            long now = System.currentTimeMillis();
            if (now - lastAdTime > 200) {

            }
        }
        if (this.listener != null) {
            this.listener.onResume();
        }
    }

    public void onPause() {
        EbeiConfig.updateLastPauseTime();
    }

    public void onStop() {
    }

    public void onDestroy() {
        EbeiActivityUtils.remove(activity);
        if (activity.equals(EbeiConfig.getCurrentActivity())) {
            EbeiConfig.setCurrentActivity(null);
        }

    }

    public void finish() {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /**
     * 生命周期回调监听
     */
    public interface ILeftCycleListener {
        void onResume();
    }
}
