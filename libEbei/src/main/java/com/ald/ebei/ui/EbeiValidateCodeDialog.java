package com.ald.ebei.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.util.EbeiMiscUtils;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by ywd on 2018/11/20.
 */

public class EbeiValidateCodeDialog extends Dialog {

    protected EbeiValidateCodeDialog(Context context, int theme) {
        super(context, theme);
    }

    public void updateLocation(float height) {
        View decorView = this.getWindow().getDecorView().findViewById(R.id.cl_msg);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) decorView.getLayoutParams();
        layoutParams.bottomMargin = (int) (height + decorView.getHeight());
        decorView.setLayoutParams(layoutParams);
    }

    public static class Builder {
        private EbeiValidateCodeDialog dialog;
        private EditText validateCode;
        private Context context;
        private String phoneNumber = "13000000000";//预留手机号
        private OnFinishListener onFinishListener;
        //参数
        private JSONObject jsonObject;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder setPhone(String phone) {
            this.phoneNumber = phone;
            return this;
        }

        public Builder setOnFinishListener(OnFinishListener onFinishListener) {
            this.onFinishListener = onFinishListener;
            return this;
        }

        public Builder setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }


        public EbeiValidateCodeDialog create() {
            dialog = new EbeiValidateCodeDialog(context, R.style.BottomValidateDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            window.setContentView(R.layout.dialog_verify_code);
            View layout = window.getDecorView();
            layout.setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM; // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setWindowAnimations(R.style.Animation_BottomSelect);
            window.setSoftInputMode(lp.SOFT_INPUT_ADJUST_RESIZE);

            TextView phone = layout.findViewById(R.id.tv_tip_with_phone);
            if (EbeiMiscUtils.isCellphone(phoneNumber)) {
                phone.setText("已发送验证码至手机号" + phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            } else {
                //非正常手机号
                phone.setText("手机号不正确" + phoneNumber);
            }
            ImageView close = layout.findViewById(R.id.iv_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            /**
             * 倒计时
             */
            EbeiNoDoubleClickButton validateGainCode = layout.findViewById(R.id.btn_resend);
//            countDown(validateGainCode);
//            confirmListener(confirm);
//            editTextListener(validateCode, confirm);
            final EbeiVerifyCodeView ebeiVerifyCodeView = layout.findViewById(R.id.vc_code);
            ebeiVerifyCodeView.setInputCompleteListener(new EbeiVerifyCodeView.InputCompleteListener() {
                @Override
                public void inputComplete() {
                    //Toast.makeText(context, "inputComplete: " + verifyCodeView.getEditContent(), Toast.LENGTH_SHORT).show();
                    if (onFinishListener != null) {
                        onFinishListener.onNext(ebeiVerifyCodeView.getEditContent());
                    }
                }

                @Override
                public void invalidContent() {

                }
            });
            return dialog;
        }


        //        private int countTime = 59;
//        private Disposable editTextSub;
//        private Disposable countDownTime;
//
//
//        private void countDown(final Button gainCode) {
//            autoCountDown(gainCode, countTime);
//            countDownTime = RxView.clicks(gainCode)
//                    //防止重复点击
//                    .throttleFirst(2, TimeUnit.SECONDS)
//                    //将点击事件转换成倒计时事件
//                    .flatMap(o -> {
//                        //更新发送按钮的状态并初始化显现倒计时文字
//                        RxView.enabled(gainCode).accept(false);
//                        RxTextView.text(gainCode).accept(countTime + "秒后重发");
//                        //发送获取网络的请求
//                        getMsg();
//                        //返回 N 秒内的倒计时观察者对象。
//                        return Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).take(countTime);
//                    })
//                    //将递增数字替换成递减的倒计时数字
//                    .map(aLong -> countTime - (aLong + 1))
//                    //订阅点击事件
//                    .subscribe(aLong -> {
//                        //显示剩余时长。当倒计时为 0 时，还原 btn 按钮.
//                        if (aLong == 0) {
//                            RxView.enabled(gainCode).accept(true);
//                            RxTextView.text(gainCode).accept("重新发送");
//                        } else {
//                            RxTextView.text(gainCode).accept(aLong + "秒后重发");
//                        }
//                    });
//        }
//
//
//        //获取验证码
//        private void getMsg() {
//            RDClient.getService(ConfirmOrderApi.class)
//                    .quickPaymentResendCode(jsonObject)
//                    .enqueue(new EbeiRequestCallBack<EbeiApiResponse>() {
//                        @Override
//                        public void onSuccess(Call<EbeiApiResponse> call, Response<EbeiApiResponse> response) {
//                            EbeiUIUtils.showToast(R.string.bank_card_add_captcha_success_toast);
//                        }
//                    });
//        }
//
//        //自动倒计时
//        private void autoCountDown(final Button gainCode, final int countTime) {
//            Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
//                    .take(countTime)
//                    .map(aLong -> countTime - (aLong + 1))
//                    .doOnNext(aLong -> RxTextView.text(gainCode).accept(aLong + "秒后重发"))
//                    .doOnComplete(() -> {
//                        RxTextView.text(gainCode).accept("重新发送");
//                        RxView.enabled(gainCode).accept(true);
//                    })
//                    .subscribe();
//        }
//
//        //输入框监听
//        private void editTextListener(EditText validateCode, final Button confirm) {
//            editTextSub = RxTextView
//                    .textChanges(validateCode)
//                    .subscribe(charSequence -> {
//                        String code = charSequence.toString();
//                        if (code.length() > 3) {
//                            confirm.setEnabled(true);
//                        } else {
//                            confirm.setEnabled(false);
//                        }
//                    });
//        }
//
//        //确认按钮监听
//        private void confirmListener(final Button confirm) {
//            RxView.clicks(confirm)
//                    .throttleFirst(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
//                    .subscribe(o -> {
//                        RxView.enabled(confirm).accept(false);
//                        toCheckCode();
//                    });
//        }
//
//        //验证验证码
//        private void toCheckCode() {
//            jsonObject.put("smsCode", validateCode.getText().toString());
//            Call<WxOrAlaPayModel> call = RDClient.getService(ConfirmOrderApi.class).confirmPayment(jsonObject);
//            EbeiNetworkUtil.showCutscenes(context, call);
//            call.enqueue(new EbeiRequestCallBack<WxOrAlaPayModel>() {
//                @Override
//                public void onSuccess(Call<WxOrAlaPayModel> call, Response<WxOrAlaPayModel> response) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//                    if (onFinishListener != null) {
//                        onFinishListener.onNext(response.body());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<WxOrAlaPayModel> call, Throwable t) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//                    super.onFailure(call, t);
//                }
//            });
//        }
//
//
//        private void unsubscribe() {
//            if (countDownTime != null) {
//                countDownTime.dispose();
//            }
//            if (editTextSub != null) {
//                editTextSub.dispose();
//            }
//        }
//    }
//
        public interface OnFinishListener {
            void onNext(String apiResponse);
        }


    }
}
