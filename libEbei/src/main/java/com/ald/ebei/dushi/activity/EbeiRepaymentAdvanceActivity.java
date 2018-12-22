package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.dushi.model.EbeiBankCardListModel;
import com.ald.ebei.dushi.model.EbeiBankCardModel;
import com.ald.ebei.dushi.model.EbeiHomeModel;
import com.ald.ebei.dushi.model.EbeiRepaymentSuccessModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiEditTextWithDelNew;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.ui.dialog.EbeiRepaymentRemitDialog;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiModelEnum;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import static com.ald.ebei.util.EbeiBundleKeys.REQUEST_CODE_SEND_SMS;

/**
 * 还款页面(提前结清)
 * Created by ywd on 2018/11/20.
 */

public class EbeiRepaymentAdvanceActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private Context context;
    private TextView tvTip;
    private EbeiEditTextWithDelNew etRepaymentMoney;
    private CheckBox cbPeriodMoney;
    private TextView tvPeriodMoney;
    private RelativeLayout rlRepaymentCard;
    private ImageView ivBankLogo;
    private TextView tvBankName;
    private EbeiNoDoubleClickButton btnRepayment;
    private LinearLayout llOverdueThis;
    private TextView tvOtherRepayment;
    private boolean isChecked = false;
    private EbeiBankCardModel ebeiBankCardModel;
    private double maxReyapmentMoney;
    private double allWaitRepayAmount;
    private List<EbeiHomeModel.RemitListModel> remitList;//未出账分期减免手续费

    private String loanId;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_repayment_tip) {
            new EbeiRepaymentRemitDialog.Builder(context)
                    .setData(remitList)
                    .setSurplusAmount(String.valueOf(allWaitRepayAmount))
                    .setShouldRepayAmount(String.valueOf(maxReyapmentMoney))
                    .create()
                    .show();
        } else if (id == R.id.rl_repayment_card) {
            Intent intent = new Intent();
            EbeiActivityUtils.push(EbeiBankCardManagerActivity.class, intent, EbeiBundleKeys.REQUEST_CODE_BANK_SELECT);
        } else if (id == R.id.btn_repayment) {
            String repaymentAmount = etRepaymentMoney.getText().toString();
            if (EbeiMiscUtils.isEmpty(repaymentAmount)) {
                EbeiUIUtils.showToast("请输入还款金额");
                return;
            }
            if (Double.parseDouble(repaymentAmount) <= 0) {
                EbeiUIUtils.showToast("还款金额需要大于零");
                return;
            }
            if (ebeiBankCardModel == null) {
                EbeiUIUtils.showToast("请选择还款银行卡");
                return;
            }
            sendMsg();
        } else if (id == R.id.tv_repayment_other) {
            Intent intent1 = new Intent();
            intent1.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, EbeiConstant.REPAY_OTHER);
            EbeiActivityUtils.push(EbeiHtml5WebView.class, intent1);
        }
    }

    @Override
    public String getStatName() {
        return "提前结清";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_repayment_advance;
    }

    @Override
    protected void setViewModel() {

    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        context = this;
        setTitle("还款");
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
        Intent intent = getIntent();
        loanId = String.valueOf(intent.getLongExtra("loanId", 0));
        maxReyapmentMoney = intent.getDoubleExtra("repayAmount", 0);
        allWaitRepayAmount = intent.getDoubleExtra("allWaitRepayAmount", 0);
        remitList = (List<EbeiHomeModel.RemitListModel>) intent.getSerializableExtra("remitList");
        etRepaymentMoney.setText(String.valueOf(maxReyapmentMoney));
        etRepaymentMoney.setClearVisibility(false);
        getBindCardList();
    }

    private void init() {
        tvTip = findViewById(R.id.tv_repayment_tip);
        etRepaymentMoney = findViewById(R.id.et_repayment_money);
        etRepaymentMoney.setFocusable(false);
        etRepaymentMoney.setFocusableInTouchMode(false);
        cbPeriodMoney = findViewById(R.id.cb_period_money);
        tvPeriodMoney = findViewById(R.id.tv_period_money);
        llOverdueThis = findViewById(R.id.ll_overdue_this);
        rlRepaymentCard = findViewById(R.id.rl_repayment_card);
        ivBankLogo = findViewById(R.id.iv_bank_logo);
        tvBankName = findViewById(R.id.tv_bank_name);
        btnRepayment = findViewById(R.id.btn_repayment);
        tvOtherRepayment = findViewById(R.id.tv_repayment_other);
    }

    private void setListener() {
        tvTip.setOnClickListener(this);
        etRepaymentMoney.addTextChangedListener(textWatcher);
        etRepaymentMoney.setKeyListener(new DigitsKeyListener(false, true));
        cbPeriodMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
            }
        });
        rlRepaymentCard.setOnClickListener(this);
        btnRepayment.setOnClickListener(this);
        tvOtherRepayment.setOnClickListener(this);
    }

    private void getBindCardList() {
        EbeiClient.getService(EbeiApi.class)
                .getBindBankList()
                .compose(EbeiRxUtils.<EbeiBankCardListModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiBankCardListModel>() {
                    @Override
                    public void onNext(EbeiBankCardListModel ebeiBankCardListModel) {
                        super.onNext(ebeiBankCardListModel);
                        if (ebeiBankCardListModel != null) {
                            for (EbeiBankCardModel item : ebeiBankCardListModel.getBankInfos()) {
                                if (EbeiModelEnum.Y.getModel().equals(item.getIsMain())) {
                                    ebeiBankCardModel = item;
                                    String bankName = ebeiBankCardModel.getBankName();
                                    String bankCardNo = ebeiBankCardModel.getCardNumber();
                                    tvBankName.setText(bankName + bankCardNo.substring(bankCardNo.length() - 4));
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEND_SMS) {
            if (data != null) {
                String msgCode = data.getStringExtra("smsCode");
                if (EbeiMiscUtils.isEmpty(msgCode)) {
                    EbeiUIUtils.showToast("获取验证码失败，请重试");
                    return;
                }
                repayment(msgCode);
            }
        } else {
            if (data != null) {
                ebeiBankCardModel = (EbeiBankCardModel) data.getSerializableExtra("bank_model");
                String bankName = ebeiBankCardModel.getBankName();
                String bankCardNo = ebeiBankCardModel.getCardNumber();
                tvBankName.setText(bankName + bankCardNo.substring(bankCardNo.length() - 4));
            }
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (EbeiMiscUtils.isNotEmpty(editable.toString())) {
                double inputMoney = Double.parseDouble(String.valueOf(editable));
                etRepaymentMoney.removeTextChangedListener(this);
                if (inputMoney >= maxReyapmentMoney) {
                    etRepaymentMoney.setText(String.valueOf(maxReyapmentMoney));
                    cbPeriodMoney.setChecked(true);
                } else {
                    etRepaymentMoney.setText(editable);
                    cbPeriodMoney.setChecked(false);
                }
                etRepaymentMoney.addTextChangedListener(this);
                etRepaymentMoney.setSelection(editable.toString().length());
            }
        }
    };

    private void repayment(String smsCode) {
        JSONObject reqObj = new JSONObject();
        reqObj.put("bankCardNum", ebeiBankCardModel.getCardNumber());
        reqObj.put("isAllRepay", "Y");
        reqObj.put("loanId", loanId);
        reqObj.put("payType", ebeiBankCardModel.getPayType());
        reqObj.put("repayAmount", etRepaymentMoney.getText().toString().trim());
        reqObj.put("smsCode", smsCode);
        EbeiClient.getService(EbeiApi.class)
                .applyRepay(reqObj)
                .compose(EbeiRxUtils.<EbeiRepaymentSuccessModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiRepaymentSuccessModel>(this) {
                    @Override
                    public void onNext(EbeiRepaymentSuccessModel ebeiRepaymentSuccessModel) {
                        super.onNext(ebeiRepaymentSuccessModel);
                        if (ebeiRepaymentSuccessModel != null) {
                            Intent intent = new Intent();
                            intent.putExtra("repaymentSubmitSuccessModel", ebeiRepaymentSuccessModel);
                            EbeiActivityUtils.push(EbeiRepaymentSuccessActivity.class, intent, EbeiBundleKeys.REQUEST_CODE_REPAYMENT_SUCCESS);
                            EbeiActivityUtils.finish(EbeiRepaymentActivity.class);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                    }
                });
    }

    private void sendMsg() {
        //发送短信
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile", EbeiConfig.getEbeiAccountProvider().getUserName());
        jsonObject.put("smsType", EbeiModelEnum.REPAY_CONFIRM.getModel());

        EbeiClient.getService(EbeiApi.class)
                .requestSendMsgCode(jsonObject)
                .compose(EbeiRxUtils.<JSONObject>io_main())
                .subscribe(new EbeiBaseObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        super.onNext(jsonObject);
                        EbeiUIUtils.showToastWithoutTime("短信验证码已经发送");
                        showSubmitView();
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToastWithoutTime(ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * 展示输入验证码页面
     */
    private void showSubmitView() {
        Intent intentCode = new Intent();
        intentCode.putExtra("mobile", EbeiConfig.getEbeiAccountProvider().getUserName());
        intentCode.putExtra("smsType", EbeiModelEnum.REPAY_CONFIRM.getModel());
        intentCode.putExtra("verify_title", "确认还款");
        EbeiActivityUtils.push(EbeiVerifyCodeActivity.class, intentCode, REQUEST_CODE_SEND_SMS);
    }
}
