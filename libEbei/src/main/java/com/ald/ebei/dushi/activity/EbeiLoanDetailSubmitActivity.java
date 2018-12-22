package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ald.ebei.EbeiApplication;
import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.dushi.model.EbeiAnalyzePeriodsModelData;
import com.ald.ebei.dushi.model.EbeiBankCardListModel;
import com.ald.ebei.dushi.model.EbeiBankCardModel;
import com.ald.ebei.dushi.model.EbeiProtocolListModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiMonthlySupplyDialog;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.ui.dialog.BottomDialogLikeIOS;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiModelEnum;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiSPUtil;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.baiduloc.EbeiLocationService;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bqs.risk.df.android.BqsDF;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.tongdun.android.shell.FMAgent;

import static com.ald.ebei.util.EbeiBundleKeys.REQUEST_CODE_SEND_SMS;

/**
 * 借款确认页面
 * Created by ywd on 2018/11/19.
 */

public class EbeiLoanDetailSubmitActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private Context context;

    private TextView tvTipPrice;
    private TextView tvLoanPrice;
    private TextView tvMonth;
    private TextView tvLoanType;
    private TextView tvLoanTerm;
    private TextView tvAddCardTip;
    private LinearLayout llCardInfo;
    private CheckBox cbAgreement;
    private EbeiNoDoubleClickButton btnApply;
    private ScrollView svMain;
    private TextView txtLoanPeriod;
    private CardView cdBankInfo;
    private ImageView image;
    private TextView tvName;
    private TextView tvCardNum;
    private TextView txtMonthAmount;
    private TextView tvAgreement;

    private boolean agreementChecked = false;
    private List<EbeiAnalyzePeriodsModelData> monthPeriods;
    private boolean hadCard = true;
    public static final int REQ_BANK_INFO = 98;
    private String bankCardNo;
    private String bankName;
    private int period;
    private String phone;
    private EbeiBankCardModel ebeiBankCardModel;
    private EbeiLocationService ebeiLocationService;
    private BDLocation bdLlocation;

    @Override
    public String getStatName() {
        return "借款详情_确认";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_loan_detail_submit;
    }

    @Override
    protected void setViewModel() {
    }

    @Override
    protected void afterOnCreate() {
        context = this;
        super.afterOnCreate();
        setTitle("借款详情");
        setTitleColor(ContextCompat.getColor(this, R.color.text_important_color));
        setLeftImage(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                onBackPressed();
            }
        });
        initView();
        initData();
        setListener();
        getBindCardList();
    }

    private void initView() {
        tvTipPrice = findViewById(R.id.tv_tip_price);
        tvLoanPrice = findViewById(R.id.tv_loan_price);
        tvMonth = findViewById(R.id.tv_month);
        tvLoanType = findViewById(R.id.tv_loan_type);
        tvLoanTerm = findViewById(R.id.tv_loan_term);
        tvAddCardTip = findViewById(R.id.tv_add_card_tip);
        llCardInfo = findViewById(R.id.ll_card_info);
        cbAgreement = findViewById(R.id.cb_agreement);
        btnApply = findViewById(R.id.btn_apply);
        svMain = findViewById(R.id.sv_main);
        txtLoanPeriod = findViewById(R.id.txt_loan_period);
        cdBankInfo = findViewById(R.id.cd_bankInfo);
        image = findViewById(R.id.image);
        tvName = findViewById(R.id.tv_name);
        tvCardNum = findViewById(R.id.tv_card_num);
        txtMonthAmount = findViewById(R.id.txt_month_amount);
        tvAgreement = findViewById(R.id.tv_agreement_loan);
    }

    private void initData() {
        Intent intent = getIntent();
        double amount = intent.getDoubleExtra(EbeiConstant.APP_LOAN_AMOUNT, 0d);
        tvLoanPrice.setText(EbeiAppUtils.deleteFormatAmount(String.valueOf(amount)));

        period = intent.getIntExtra(EbeiConstant.APP_LOAN_PERIOD, 0);
        txtLoanPeriod.setText(period + "个月");

        String stringExtra = intent.getStringExtra(EbeiConstant.APP_LOAN_MONTH_AMOUNT);
        monthPeriods = JSONObject.parseArray(stringExtra,
                EbeiAnalyzePeriodsModelData.class);
        txtMonthAmount.setText(EbeiAppUtils.formatAmount(monthPeriods.get(0).getAmount()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        ebeiLocationService = ((EbeiApplication) getApplication()).ebeiLocationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        ebeiLocationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            ebeiLocationService.setLocationOption(ebeiLocationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            ebeiLocationService.setLocationOption(ebeiLocationService.getOption());
        }
    }

    @Override
    protected void onStop() {
        stopLoc();
        super.onStop();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                bdLlocation = location;
                stopLoc();
                sendMsg();
            } else {
                EbeiUIUtils.showToast("未获取到定位，请开启权限后重试");
            }
        }

    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_apply) {
            if (!agreementChecked) {
                EbeiUIUtils.showToastWithoutTime("请先同意借款相关协议");
                return;
            }
            if (!hadCard) {
                EbeiUIUtils.showToastWithoutTime("请选择银行卡");
                return;
            }
            if ("请选择".equals(tvLoanType.getText().toString())) {
                EbeiUIUtils.showToastWithoutTime("请选择借款用途");
                return;
            }
            if ("请选择".equals(tvLoanTerm.getText().toString())) {
                EbeiUIUtils.showToastWithoutTime("请选择还款来源");
                return;
            }
            ebeiLocationService.registerListener(mListener);
            ebeiLocationService.start();// 定位SDK
        } else if (id == R.id.tv_month) {
            new EbeiMonthlySupplyDialog.Builder(this)
                    .setData(monthPeriods)
                    .create()
                    .show();
        } else if (id == R.id.tv_loan_type) {
            //借款用途
            //条件选择器
            final List<String> optionsTypeItems = new ArrayList<>();
            optionsTypeItems.add("个人经营");
            optionsTypeItems.add("个人消费");
            optionsTypeItems.add("个人学习");
            optionsTypeItems.add("其他资金周转");
            showOptions(optionsTypeItems);
        } else if (id == R.id.tv_loan_term) {
            //条件选择器
            final List<String> optionsTermItems = new ArrayList<>();
            optionsTermItems.add("个人经营收入");
            optionsTermItems.add("工资收入");
            optionsTermItems.add("其他非工资性收入");
            showOptions(optionsTermItems);
        } else if (id == R.id.ll_card_info) {
            if (hadCard) {
                EbeiActivityUtils.push(EbeiBankCardManagerActivity.class, REQ_BANK_INFO);
            } else {
                EbeiActivityUtils.push(EbeiBankCardAddActivity.class, REQ_BANK_INFO);
            }
        } else if (id == R.id.tv_agreement_loan) {
            if ("请选择".equals(tvLoanType.getText().toString())) {
                EbeiUIUtils.showToastWithoutTime("请选择借款用途");
                return;
            }
            if ("请选择".equals(tvLoanTerm.getText().toString())) {
                EbeiUIUtils.showToastWithoutTime("请选择还款来源");
                return;
            }
            getProtocolList();
        }
    }

    private void showOptions(List<String> list) {
        OptionsPickerView pvTypeOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = list.get(options1);
                tvLoanType.setText(tx);
            }
        }).build();
        pvTypeOptions.setPicker(list);
        pvTypeOptions.show();
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
                                    hadCard = true;
                                    String bankName = ebeiBankCardModel.getBankName();
                                    String bankCardNo = ebeiBankCardModel.getCardNumber();
                                    tvAddCardTip.setVisibility(View.GONE);
                                    cdBankInfo.setVisibility(View.VISIBLE);
                                    Glide.with(context).load(ebeiBankCardModel.getBankIcon()).into(image);
                                    tvName.setText(bankName);
                                    tvCardNum.setText(getString(R.string.bank_card_no, bankCardNo.substring(bankCardNo.length() - 4)));
                                    return;
                                } else {
                                    hadCard = false;
                                    tvAddCardTip.setVisibility(View.VISIBLE);
                                    cdBankInfo.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            hadCard = false;
                            tvAddCardTip.setVisibility(View.VISIBLE);
                            cdBankInfo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                    }
                });
    }

    private void getProtocolList() {
        JSONObject reqObj = new JSONObject();
        reqObj.put("amount", tvLoanPrice.getText().toString());
        reqObj.put("loanRemark", tvLoanType.getText().toString());
        reqObj.put("nper", period);
        reqObj.put("repayRemark", tvLoanTerm.getText().toString());
        EbeiClient.getService(EbeiApi.class)
                .getProtocolList(reqObj)
                .compose(EbeiRxUtils.<EbeiProtocolListModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiProtocolListModel>() {
                    @Override
                    public void onNext(EbeiProtocolListModel ebeiProtocolListModel) {
                        super.onNext(ebeiProtocolListModel);
                        if (ebeiProtocolListModel != null) {
                            new BottomDialogLikeIOS.Builder(context)
                                    .setList(ebeiProtocolListModel.getLoanInfos())
                                    .create()
                                    .show();
                        } else {
                            EbeiUIUtils.showToast("获取协议失败，请重试");
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_BANK_INFO) {
                ebeiBankCardModel = (EbeiBankCardModel) data.getSerializableExtra("bank_model");
                bankCardNo = ebeiBankCardModel.getCardNumber();
                bankName = ebeiBankCardModel.getBankName();
                tvAddCardTip.setVisibility(View.GONE);
                cdBankInfo.setVisibility(View.VISIBLE);
                Glide.with(context).load(ebeiBankCardModel.getBankIcon()).into(image);
                tvName.setText(bankName);
                tvCardNum.setText(getString(R.string.bank_card_no, bankCardNo.substring(bankCardNo.length() - 4)));
            } else if (requestCode == REQUEST_CODE_SEND_SMS) {
                String msgCode = data.getStringExtra("smsCode");
                if (EbeiMiscUtils.isEmpty(msgCode)) {
                    EbeiUIUtils.showToast("获取验证码失败，请重试");
                    return;
                }
                submit(msgCode);
            }
        }
    }


    private void setListener() {
        btnApply.setOnClickListener(this);
        tvMonth.setOnClickListener(this);
        tvLoanType.setOnClickListener(this);
        tvLoanTerm.setOnClickListener(this);
        llCardInfo.setOnClickListener(this);
        tvAgreement.setOnClickListener(this);
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                agreementChecked = b;
            }
        });
    }

    private void sendMsg() {
        //发送短信
        JSONObject jsonObject = new JSONObject();
        phone = EbeiSPUtil.getString(EbeiConstant.APP_PHONE, "");
        jsonObject.put("mobile", phone);
        jsonObject.put("smsType", "LOAN_CONFIRM");

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
        Intent intent = new Intent();
        intent.putExtra("mobile", EbeiConfig.getEbeiAccountProvider().getUserName());
        intent.putExtra("smsType", EbeiModelEnum.LOAN_CONFIRM.getModel());
        intent.putExtra("verify_title", "确认借款");
        EbeiActivityUtils.push(EbeiVerifyCodeActivity.class, intent, REQUEST_CODE_SEND_SMS);
    }

    private void submit(String msgCode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", tvLoanPrice.getText().toString());
        jsonObject.put("bankCardNum", ebeiBankCardModel.getCardNumber());
        //同盾风控
        String blackBox = FMAgent.onEvent(context);
        jsonObject.put("blackBox", blackBox);
        //白骑士设备指纹
        String bqsBlackBox = BqsDF.getTokenKey();
        jsonObject.put("bqsBlackBox", bqsBlackBox);

        jsonObject.put("address", bdLlocation.getAddress().address);
        jsonObject.put("city", bdLlocation.getCity());
        jsonObject.put("county", bdLlocation.getDistrict());
        jsonObject.put("province", bdLlocation.getProvince());

        jsonObject.put("latitude", bdLlocation.getLatitude());
        jsonObject.put("longitude", bdLlocation.getLongitude());

        jsonObject.put("loanRemark", tvLoanType.getText().toString());
        jsonObject.put("repayRemark", tvLoanTerm.getText().toString());
        jsonObject.put("periods", period);
        jsonObject.put("smsCode", msgCode);
        jsonObject.put("remark", "");

        EbeiClient.getService(EbeiApi.class)
                .applyLoan(jsonObject)
                .compose(EbeiRxUtils.<JSONObject>io_main())
                .subscribe(new EbeiBaseObserver<JSONObject>(this) {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        super.onNext(jsonObject);
                        EbeiUIUtils.showToastWithoutTime("借款成功");
                        finish();
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToastWithoutTime(ebeiApiException.getMsg());
                    }
                });
    }

    private void stopLoc() {
        ebeiLocationService.unregisterListener(mListener); //注销掉监听
        ebeiLocationService.stop(); //停止定位服务
    }
}
