package com.ald.ebei.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.config.EbeiActivityInterceptor;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.config.EbeiPermissionsResult;
import com.ald.ebei.config.EbeiStatNameProvider;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiPermissionCheck;
import com.ald.ebei.util.log.EbeiLogger;

import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述：activity都必须要继承自此类，这样就可以方便地进行一些
 * 统一的状态维护或者统一的处理。
 * 修订历史：
 */
public abstract class EbeiBaseActivity extends AppCompatActivity implements EbeiStatNameProvider, EbeiPermissionsResult {
    private static final String TAG = "EbeiBaseActivity";
    protected EbeiActivityInterceptor interceptor;
    private Dialog showingDialog;

    public EbeiBaseActivity() {
        interceptor = new EbeiActivityInterceptor(this, this);
    }

    public EbeiActivityInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(EbeiActivityInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        interceptor.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        this.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_in_to_left);
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果是就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        interceptor.onCreate(savedInstanceState);
    }


    private int title;
    private int resId;

    public void setTitle(int title) {
        this.title = title;
    }

    public void setBackImg(int resId) {
        this.resId = resId;
    }

    private void initTitleLayout() {
        ImageView imgBack = findViewById(R.id.layout_head_icon);
        if (imgBack != null) {
            if (resId != R.drawable.ic_back && resId != 0) {
                imgBack.setImageDrawable(getResources().getDrawable(resId));
            }
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        if (0 != title) {
            TextView titleTv = findViewById(R.id.layout_head_title);
            titleTv.setText(title);
        }

    }


    public void setShowingDialog(Dialog showingDialog) {
        this.showingDialog = showingDialog;
    }

    private void dialogDestroy() {
        if (showingDialog != null) {
            if (showingDialog.isShowing() && !isFinishing()) {
                showingDialog.dismiss();
            }
        }
        showingDialog = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        interceptor.onStart();
        initTitleLayout();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        interceptor.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        interceptor.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        interceptor.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        interceptor.onStop();
        dialogDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        interceptor.onDestroy();
        dialogDestroy();

    }

    @Override
    public void finish() {
        super.finish();
//        this.overridePendingTransition(R.anim.slide_out_from_left, R.anim.slide_out_to_right);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        interceptor.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        interceptor.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EbeiPermissionCheck.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    /**
     * 向Fragment 分发onActivityResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        interceptor.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0 || index >= fm.getFragments().size()) {
                Log.w(TAG, "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
        }
    }

    /**
     * 递归调用，对所有子Fragement生效
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode, Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null) {
                    handleResult(f, requestCode, resultCode, data);
                }
            }
        }
    }


    /**
     * 添加极光别名
     */
    private static final String ALIAS_TAG = "EbeiBaseActivity";//标识

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    public static boolean setAlias(String name) {
        if (TextUtils.isEmpty(name)) {
            EbeiLogger.d(ALIAS_TAG, "别名为空");
            return false;
        }
        if (!EbeiMiscUtils.isValidTagAndAlias(name)) {
            EbeiLogger.d(ALIAS_TAG, "只能是数字,英文字母和中文");
            return false;
        }

        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, name));
        return true;
    }

    private static final int MSG_SET_ALIAS = 1001;
    @SuppressLint("HandlerLeak")
    private static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(ALIAS_TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    Activity curActivity = EbeiActivityUtils.peek();
                    if (curActivity == null)
                        curActivity = EbeiConfig.getCurrentActivity();
                    break;
                default:
                    Log.i(ALIAS_TAG, "Unhandled msg - " + msg.what);
            }
        }
    };


    @Override
    public void onUIRequestPermissionsGrantedResult(int requestCode) {

    }
}
