package com.ald.ebei.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiSPUtil;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.EbeiUnLandAccountProvider;
import com.alibaba.fastjson.JSONObject;

public class SettingActivityEbei extends EbeiBaseActivity implements View.OnClickListener {

    private EbeiNoDoubleClickButton mBtnAboutUs;
    private EbeiNoDoubleClickButton mBtnSetNewLoginPwd;
    private EbeiNoDoubleClickButton mBtnLoginOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(R.string.setting_title);
        initView();
        initLisenter();
    }

    private void initView() {
        mBtnSetNewLoginPwd = findViewById(R.id.btn_set_login_pwd);
        mBtnAboutUs = findViewById(R.id.btn_about_us);
        mBtnLoginOut = findViewById(R.id.btn_login_out);
    }

    private void initLisenter() {
        mBtnSetNewLoginPwd.setOnClickListener(this);
        mBtnAboutUs.setOnClickListener(this);
        mBtnLoginOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_set_login_pwd) {
            //EbeiActivityUtils.push(ModifyPwdInputMsgCodeActivity.class);
        } else if (id == R.id.btn_about_us) {
            Intent intent = new Intent();
            intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, EbeiConstant.URL_ABOUT_US);
            EbeiActivityUtils.push(EbeiHtml5WebView.class, intent);
        } else if (id == R.id.btn_login_out) {
            logout();
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        EbeiClient.getService(EbeiApi.class)
                .logout()
                .compose(EbeiRxUtils.<JSONObject>io_main())
                .subscribe(new EbeiBaseObserver<JSONObject>() {
                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }

                    @Override
                    public void onNext(JSONObject apiResponseEca) {
                        super.onNext(apiResponseEca);
                        EbeiSPUtil.setValue(EbeiConstant.APP_TOKEN, "");
                        EbeiSPUtil.setValue(EbeiConstant.APP_PHONE, "");
                        EbeiUIUtils.showToast("退出成功");
                        EbeiConfig.setEbeiAccountProvider(new EbeiUnLandAccountProvider());
                        EbeiConfig.updateLand(false);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
    }


    @Override
    public String getStatName() {
        return getClass().getSimpleName();
    }


}
