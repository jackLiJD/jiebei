package com.ald.ebei.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.EbeiBaseObserver;
import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EbeiCountDownTextView extends TextView {
    public EbeiCountDownTextView(Context context) {
        this(context, null);
    }

    private Disposable mdDisposable;

    public EbeiCountDownTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EbeiCountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private String phone;
    private Context mContext;
    //验证码类型['REGIST(注册短信)', 'LOGIN(登录)', 'FORGET_PASSWD(忘记密码验证短信)', 'PAY_HOLD(代扣支付短信)', 'RESET_ACC_PWD(重置账户密码)']
    private String mType;
    private onSuccessListener listener;

    public interface onSuccessListener {
        void onSuccess();
    }

    public void setOnSuccessListener(onSuccessListener listener) {
        this.listener = listener;
    }

    private void sendVerification() {
        JSONObject dataObj = new JSONObject();
        dataObj.put("smsType", mType);
        dataObj.put("mobile", phone);
//        JSONObject reqObj = new JSONObject();
//        reqObj.put(EbeiConstant.APP_KEY_PARAM, dataObj);

        EbeiClient
                .getService(EbeiApi.class)
                .requestSendMsgCode(dataObj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new EbeiBaseObserver<JSONObject>(mContext) {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        super.onNext(jsonObject);
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        startUi();
                    }
                });
    }

    private boolean isCountDown = false;

    private void resetUi() {
        setBackground(getResources().getDrawable(R.drawable.login_start_get_verification));
        setText(R.string.pwd_login_get_verification);
        setTextColor(getResources().getColor(R.color.white));
    }

    private void startUi() {
        mdDisposable = Flowable.intervalRange(0, 61, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        isCountDown = true;
                        setText(+(60 - aLong) + getResources().getString(R.string.find_pwd_reset));
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        isCountDown = false;
                        //倒计时完毕置为可点击状态
                        resetUi();
                        setEnabled(true);
                    }
                })
                .subscribe();

        setEnabled(false);
        setBackground(getResources().getDrawable(R.drawable.login_stop_get_verification));
        setTextColor(getResources().getColor(R.color.color_999999));
    }


    //      type  验证码类型（R：注册 L：登录 M:修改密码）
    public void startCountDown(String phone, String type) {
        if (isCountDown) {
            return;
        }
        this.phone = phone;
        this.mType = type;
        sendVerification();

    }

    public void stopCountDown() {
        if (mdDisposable != null) {
            mdDisposable.dispose();
            isCountDown = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopCountDown();
    }
}
