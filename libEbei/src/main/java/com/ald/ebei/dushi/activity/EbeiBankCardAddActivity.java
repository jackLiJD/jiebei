package com.ald.ebei.dushi.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.dushi.model.EbeiSupportBankModel;
import com.ald.ebei.dushi.model.EbeiUserInfoModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.entity.EbeiApiResponse;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiEditTextWithDelNew;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiTimerCountDownUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.log.EbeiLogger;
import com.alibaba.fastjson.JSONObject;

/**
 * 添加银行卡页面
 * Created by ywd on 2018/11/19.
 */

public class EbeiBankCardAddActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private static final String TAG = "EbeiBankCardAddActivity";
    private EbeiEditTextWithDelNew etCardNum;//银行卡号
    private EbeiEditTextWithDelNew etPhone;//手机号
    private EbeiEditTextWithDelNew etCode;//验证码
    private TextView tvSupportCard;//支持的银行卡
    private TextView tvGetCode;//获取验证码
    private EbeiNoDoubleClickButton btnApply;
    private EbeiSupportBankModel ebeiSupportBankModel;
    private TextView tvRealName;
    private TextView tvBankName;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_support_card) {
            EbeiActivityUtils.push(EbeiSupportBankActivity.class, new Intent(), EbeiBundleKeys.REQUEST_CODE_BANK_SUPPORT_BANK);
        } else if (id == R.id.tv_bank_name) {
            EbeiActivityUtils.push(EbeiSupportBankActivity.class, new Intent(), EbeiBundleKeys.REQUEST_CODE_BANK_SUPPORT_BANK);
        } else if (id == R.id.tv_get_code) {
            String card = etCardNum.getText().toString();
            String mobile = etPhone.getText().toString();
            if (EbeiMiscUtils.isEmpty(card)) {
                EbeiUIUtils.showToast("银行卡号不能为空");
                return;
            }
            if (ebeiSupportBankModel == null || EbeiMiscUtils.isEmpty(ebeiSupportBankModel.getBankCode())) {
                EbeiUIUtils.showToast("请先选择所属银行");
                return;
            }
            if (EbeiMiscUtils.isEmpty(mobile)) {
                EbeiUIUtils.showToast("手机号不能为空");
                return;
            }
            getCode(ebeiSupportBankModel.getBankCode(), card, mobile);
        } else if (id == R.id.btn_apply) {
            String cardNum = etCardNum.getText().toString();
            String code = etCode.getText().toString();
            if (EbeiMiscUtils.isEmpty(cardNum)) {
                EbeiUIUtils.showToast("银行卡号不能为空");
                return;
            }
            if (EbeiMiscUtils.isEmpty(code)) {
                EbeiUIUtils.showToast("验证码不能为空");
                return;
            }
            addBankCard(cardNum, code);
        }
    }

    @Override
    public String getStatName() {
        return null;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_bank_card_add;
    }

    @Override
    protected void setViewModel() {

    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        setTitle("添加银行卡");
        setTitleColor(ContextCompat.getColor(this, R.color.text_important_color));
        setLeftImage(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                onBackPressed();
            }
        });
        init();
        setListener();
        getUserInfo();
    }

    private void init() {
        tvRealName = findViewById(R.id.tv_real_name);
        tvBankName = findViewById(R.id.tv_bank_name);
        etCardNum = findViewById(R.id.et_card_num);
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        tvSupportCard = findViewById(R.id.tv_support_card);
        tvGetCode = findViewById(R.id.tv_get_code);
        btnApply = findViewById(R.id.btn_apply);
    }

    private void setListener() {
        tvSupportCard.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        tvBankName.setOnClickListener(this);
        btnApply.setOnClickListener(this);
    }

    private void getUserInfo() {
        EbeiClient.getService(EbeiApi.class)
                .getUserInfo()
                .compose(EbeiRxUtils.<EbeiUserInfoModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiUserInfoModel>() {
                    @Override
                    public void onNext(EbeiUserInfoModel ebeiUserInfoModel) {
                        super.onNext(ebeiUserInfoModel);
                        if (ebeiUserInfoModel != null) {
                            String realName = ebeiUserInfoModel.getRealName();
                            String idCardNo = ebeiUserInfoModel.getIdNumber();
                            tvRealName.setText(realName);
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * 绑卡
     *
     * @param cardNum
     * @param code
     */
    private void addBankCard(String cardNum, String code) {
        JSONObject dataObj = new JSONObject();
        dataObj.put("cardNumber", cardNum);
        dataObj.put("clientType", "02");
        dataObj.put("verifyCode", code);
        EbeiClient.getService(EbeiApi.class)
                .bankCardAdd(dataObj)
                .compose(EbeiRxUtils.<EbeiApiResponse>io_main())
                .subscribe(new EbeiBaseObserver<EbeiApiResponse>() {
                    @Override
                    public void onNext(EbeiApiResponse versionInfo) {
                        super.onNext(versionInfo);
                        EbeiLogger.d(TAG, "添加成功");
                        EbeiUIUtils.showToast("添加成功");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiLogger.d(TAG, ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * 获取验证码
     */
    private void getCode(String bankCode, String cardNumber, String mobile) {
        JSONObject dataObj = new JSONObject();
        dataObj.put("bankCode", bankCode);
        dataObj.put("cardNumber", cardNumber);
        dataObj.put("clientType", "02");
        dataObj.put("mobile", mobile);
        EbeiClient.getService(EbeiApi.class)
                .authSign(dataObj)
                .compose(EbeiRxUtils.<EbeiApiResponse>io_main())
                .subscribe(new EbeiBaseObserver<EbeiApiResponse>() {
                    @Override
                    public void onNext(EbeiApiResponse versionInfo) {
                        super.onNext(versionInfo);
                        EbeiUIUtils.showToast("验证码已发送");
                        timerDown(60000, 1000);
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast("验证码发送失败,请重试");
                    }
                });
    }

    /**
     * 倒计时
     *
     * @param millisInFuture    总时间
     * @param countDownInterval 时间间隔
     */
    private void timerDown(long millisInFuture, long countDownInterval) {
        new EbeiTimerCountDownUtils(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGetCode.setText(toClock(millisUntilFinished));
                tvGetCode.setClickable(false);
            }

            @Override
            public String toClock(long millisUntilFinished) {
                return super.toClock(millisUntilFinished, "秒后重新发送");
            }

            @Override
            public void onFinish() {
                tvGetCode.setClickable(true);
                tvGetCode.setText("获取验证码");
                super.onFinish();
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EbeiBundleKeys.REQUEST_CODE_BANK_SUPPORT_BANK) {
                if (data != null) {
                    ebeiSupportBankModel = (EbeiSupportBankModel) data.getSerializableExtra("supportBank");
                    if (ebeiSupportBankModel != null) {
                        tvBankName.setText(ebeiSupportBankModel.getBankName());
                        tvBankName.setTextColor(getResources().getColor(R.color.color_txt_33));
                    }
                }
            }
        }
    }
}
