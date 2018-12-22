package com.ald.ebei.dushi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.ui.EbeiValidateCodeDialog;
import com.ald.ebei.ui.EbeiVerifyCodeView;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiRxUtils;
import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 验证码输入界面
 * Created by ywd on 2018/11/30.
 */

public class EbeiVerifyCodeActivity extends Activity implements View.OnClickListener {
    private EditText validateCode;
    private Context context;
    private TextView tvTitle;
    private ImageView ivClose;
    private TextView tvTipWithPhone;
    private EbeiNoDoubleClickButton btnResend;
    private TextView tvCountDown;
    private EbeiVerifyCodeView ebeiVerifyCodeView;
    private EbeiValidateCodeDialog.Builder.OnFinishListener onFinishListener;
    private Disposable mdDisposable;

    private String mobile;
    private String smsType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_dialog);
        context = this;
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        smsType = intent.getStringExtra("smsType");
        initView();
        initData();
        setListener();
        countDown();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        ivClose = findViewById(R.id.iv_close);
        tvTipWithPhone = findViewById(R.id.tv_tip_with_phone);
        btnResend = findViewById(R.id.btn_resend);
        tvCountDown = findViewById(R.id.tv_count_down);
        ebeiVerifyCodeView = findViewById(R.id.vc_code);
    }

    private void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("verify_title");
        if (EbeiMiscUtils.isNotEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    private void setListener() {
        ivClose.setOnClickListener(this);
        tvTipWithPhone.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        ebeiVerifyCodeView.setInputCompleteListener(new EbeiVerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                //Toast.makeText(context, "inputComplete: " + verifyCodeView.getEditContent(), Toast.LENGTH_SHORT).show();
                if (onFinishListener != null) {
                    onFinishListener.onNext(ebeiVerifyCodeView.getEditContent());
                }
                Intent intent = new Intent();
                intent.putExtra("smsCode", ebeiVerifyCodeView.getEditContent());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void invalidContent() {

            }
        });
    }

    private void sendSms(final String mobile, String type) {
        JSONObject dataObj = new JSONObject();
        dataObj.put("mobile", mobile);
        dataObj.put("smsType", type);
        EbeiClient.getService(EbeiApi.class)
                .requestSendMsgCode(dataObj)
                .compose(EbeiRxUtils.<JSONObject>io_main())
                .subscribe(new EbeiBaseObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        super.onNext(jsonObject);
                        tvTipWithPhone.setText("已发送验证码至手机号" + mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                        btnResend.setEnabled(false);
                        btnResend.setVisibility(View.GONE);
                        countDown();
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        tvTipWithPhone.setText("手机号不正确" + mobile);
                        btnResend.setEnabled(true);
                        btnResend.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void countDown() {
        mdDisposable = Flowable.intervalRange(0, 61, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        btnResend.setVisibility(View.GONE);
                        tvCountDown.setVisibility(View.VISIBLE);
                        tvCountDown.setText(+(60 - aLong) + "s 后可重新发送");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        btnResend.setVisibility(View.VISIBLE);
                        tvCountDown.setVisibility(View.GONE);
                    }
                })
                .subscribe();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_close) {
            finish();
        } else if (id == R.id.btn_resend) {
            sendSms(mobile, smsType);
        }
    }
}
