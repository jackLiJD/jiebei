package com.ald.ebei.dushi.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.auth.activity.EbeiIdAuthActivity;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.config.EbeiInitListener;
import com.ald.ebei.dushi.activity.EbeiLoanDetailSubmitActivity;
import com.ald.ebei.dushi.activity.EbeiRepaymentActivity;
import com.ald.ebei.dushi.activity.EbeiRepaymentAdvanceActivity;
import com.ald.ebei.dushi.model.EbeiAnalyzePeriodsModelData;
import com.ald.ebei.dushi.model.EbeiHomeModel;
import com.ald.ebei.dushi.model.EbeiHomeTermModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.model.EbeiAvailableCreditModel;
import com.ald.ebei.model.EbeiLandCheckModel;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiMarqueeTextView;
import com.ald.ebei.ui.EbeiMonthlySupplyDialog;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.ui.dialog.EbeiCustomDialog;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiModelEnum;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.dushi.model.EbeiAnalyzePeriodsModel;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EbeiMainFragment extends Fragment implements View.OnClickListener, EbeiInitListener {
    //    ImageView ivDefaultBg;
    TextView tvBrandTitle;
    TextView tvBrandDescription;
    TextView tvLoanTerm;
    TextView tvLoanPriceMonth;
    //    TextView tvTitleExamine;
    TextView tvSubmitSuccess;
    TextView tvSubmitSuccessDescription;
    View line1;
    View line2;
    TextView tvTransferMoney;
    TextView tvTransferMoneyDescription;
    View line3;
    View line4;
    TextView tvLoanSuccess;
    TextView tvLoanSuccessDescription;
    RelativeLayout rlStep3;
    TextView tvPriceDefault;
    TextView tvTermDefault;
    EbeiNoDoubleClickButton btnApplyDefault;
    TextView tvTipOneDefault;
    TextView tvTipTwoDefault;
    LinearLayout rlHomeDefault;
    TextView tvPriceUncertified;
    SeekBar seekCashLoanMoneyUncertified;
    TextView tvCashLoanMinAmountUncertified;
    TextView tvCashLoanMaxAmountUncertified;
    RecyclerView rvTermUncertified;
    TextView tvTipPeriodUncertified;
    TextView tvTipMonthlyUncertified;
    TextView tvTipTwoUncertified;
    TextView tvTipInterestUncertified;
    LinearLayout rlHomeUncertified;
    EbeiNoDoubleClickButton btnApplyUncertified;
    TextView tvPriceCertified;
    SeekBar seekCashLoanMoneyCertified;
    TextView tvCashLoanMinAmountCertified;
    TextView tvCashLoanMaxAmountCertified;
    RecyclerView rvTermCertified;
    TextView tvTipOneCertified;
    TextView tvTipTwoCertified;
    TextView tvTipInterestCertified;
    LinearLayout rlHomeCertified;
    EbeiNoDoubleClickButton btnDefaultApplyCertified;
    TextView tvRepaymentTitle;
    TextView tvRepaymentPrice;
    TextView tvRepaymentTimeTitle;
    TextView tvRepaymentDate;
    TextView tvTermRepayment;
    EbeiNoDoubleClickButton btnRepayment;
    RelativeLayout rlMarquee;
    EbeiMarqueeTextView marqueeView;
    ImageView ivMarqueeClose;
    TextView tvTipBottomRepayment;
    LinearLayout rlRepaymentInfo;
    TextView tvRepaymentPricePeriodTitle;
    TextView tvRepaymentPriceThisPeriod;
    TextView tvRepaymentDateLast;
    LinearLayout llHomeLoan;
    //    TextView tvTipStudent;
//    TextView tvBrandTitleProgress;
//    TextView tvTipPriceProgress;
    TextView tvLoanPriceProgress;
    TextView tvTipCertified;
    CardView cvOvertimeInfo;
    TextView tvRepaymentSurplus;
    EbeiNoDoubleClickButton btnRepaymentAdvance;
    RelativeLayout rlCurrMarquee;
    EbeiMarqueeTextView currMarqueeView;
    ImageView ivCurrMarqueeClose;

    //    private View includeBrandInfo;
    private View includeHomeDefault;
    private View includeUnCertified;
    private View includeCertified;
    private View includeProgress;
    private View includeHomeRepayment;
    private View includeCurrFinish;


    private List<EbeiHomeTermModel> ebeiHomeTermModels = new ArrayList();
    private EbeiSingleChoiceTermAdapter adapter;

    private EbeiHomeModel model;//首页实体类
    private EbeiAnalyzePeriodsModel periodsModel;//首页实体类
    private List<String> stringList;
    private boolean isCertified = false;
    public final static int REQ_REFRESH_STATUS = 23;

    EbeiSingleChoiceTermAdapter.OnItemClickListener onItemClickListener = new EbeiSingleChoiceTermAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(String itemData, int position) {
            for (int i = 0; i < ebeiHomeTermModels.size(); i++) {
                if (i == position) {
                    ebeiHomeTermModels.get(i).setChecked(true);
                } else {
                    ebeiHomeTermModels.get(i).setChecked(false);
                }
            }
            if (isCertified) {
                float progressPercent = (float)
                        seekCashLoanMoneyCertified.getProgress() / (float) seekCashLoanMoneyCertified.getMax();
                refreshAnalyzePeriods(getCashAmount(progressPercent), position);
            } else {
                float progressPercent = (float)
                        seekCashLoanMoneyUncertified.getProgress() / (float) seekCashLoanMoneyUncertified.getMax();
                refreshAnalyzePeriods(getCashAmount(progressPercent), position);
            }
            adapter.notifyDataSetChanged();
        }
    };

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float progressPercent = (float) seekBar.getProgress() / (float) seekBar.getMax();
            refreshAmountView(progressPercent);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //onstop去拿钱和分期数
            float progressPercent = (float) seekBar.getProgress() / (float) seekBar.getMax();
            refreshAmountView(progressPercent);
            refreshAnalyzePeriods(getCashAmount(progressPercent), adapter.getCurrentCheckPosition());
        }
    };

    public EbeiMainFragment() {
    }

    public static EbeiMainFragment newInstance() {
        EbeiMainFragment fragment = new EbeiMainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_ds_main, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        EbeiConfig.setCurrentActivity(getActivity());
        EbeiConfig.setEbeiInitListener(this);
        loadData(EbeiConfig.isLand());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void loadData(boolean isLogin) {
        if (isLogin) {
            loadSignInData();
        } else {
            loadCommonData();
        }
    }

    /**
     * 加载未登录数据
     */
    private void loadCommonData() {
        EbeiClient.getService(EbeiApi.class)
                .unSignInhomepage()
                .compose(EbeiRxUtils.<EbeiHomeModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiHomeModel>() {
                    @Override
                    public void onNext(EbeiHomeModel ebeiHomeModel) {
                        super.onNext(ebeiHomeModel);
                        model = ebeiHomeModel;
                        setDefault(model);
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * 加载登录数据
     */
    private void loadSignInData() {
        EbeiClient.getService(EbeiApi.class)
                .signInhomepage()
                .compose(EbeiRxUtils.<EbeiHomeModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiHomeModel>(getActivity()) {
                    @Override
                    public void onNext(EbeiHomeModel ebeiHomeModel) {
                        super.onNext(ebeiHomeModel);
                        model = ebeiHomeModel;
                        //先判断是否已经认证
                        if (EbeiModelEnum.Y.getModel().equals(model.getRiskStatus())) {
                            isCertified = true;
                            //已认证 下一步判断是否有借款
                            String loanReviewStatus = model.getLoanReviewStatus();
                            if (EbeiModelEnum.NO_LOAN.getModel().equals(loanReviewStatus)) {
                                //未产生借款
                                if (ebeiHomeTermModels.size() > 4) {
                                    tvTipCertified.setVisibility(View.VISIBLE);
                                } else {
                                    tvTipCertified.setVisibility(View.GONE);
                                }
                                setCertified(ebeiHomeModel);
                            } else if (EbeiModelEnum.REVIEW.getModel().equals(loanReviewStatus)) {
                                //借款审核中
                                setLoanInfo(ebeiHomeModel);
                            } else if (EbeiModelEnum.WAIT_LOAN.getModel().equals(loanReviewStatus)) {
                                //放款中
                                setLoanInfo(ebeiHomeModel);
                            } else if (EbeiModelEnum.AGREE.getModel().equals(loanReviewStatus)) {
                                //借款审核成功
                                String loanStatus = model.getLoanStatus();
                                if (EbeiModelEnum.WAIT_REPAY.getModel().equals(loanStatus)) {
                                    //待还款
                                    setRepaymentInfo(ebeiHomeModel);
                                } else if (EbeiModelEnum.WAIT_PROCE.getModel().equals(loanStatus)) {
                                    //还款处理中
                                    setRepaymentInfoIng(ebeiHomeModel);
                                } else if (EbeiModelEnum.OVERDUE.getModel().equals(loanStatus)) {
                                    //已逾期
                                    setRepaymentOverTimeInfo(ebeiHomeModel);
                                } else if (EbeiModelEnum.CURR_FINISH.getModel().equals(loanStatus)) {
                                    //本期已还清
                                    setCurrFinishInfo(ebeiHomeModel);
                                }
                            }
                        } else {
                            //展示未认证页面
                            isCertified = false;
                            if (ebeiHomeTermModels.size() > 4) {
                                tvTipPeriodUncertified.setVisibility(View.VISIBLE);
                            } else {
                                tvTipPeriodUncertified.setVisibility(View.GONE);
                            }
                            setUnCertified(ebeiHomeModel);
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }


    private void initView(View inflate) {
//        ivDefaultBg = inflate.findViewById(R.id.iv_default_bg);
//        includeBrandInfo = inflate.findViewById(R.id.include_brand_info);

        includeHomeDefault = inflate.findViewById(R.id.include_home_default);
        includeUnCertified = inflate.findViewById(R.id.include_home_signin_uncertified);
        includeCertified = inflate.findViewById(R.id.include_home_signin_certified);
        includeProgress = inflate.findViewById(R.id.include_progress);
        includeHomeRepayment = inflate.findViewById(R.id.include_home_repayment);
        includeCurrFinish = inflate.findViewById(R.id.include_home_curr_finish);
//        ivDefaultBg = inflate.findViewById(R.id.iv_default_bg);
        tvBrandTitle = inflate.findViewById(R.id.tv_brand_title);
        tvBrandDescription = inflate.findViewById(R.id.tv_brand_description);
        tvLoanTerm = inflate.findViewById(R.id.tv_loan_term);
        tvLoanPriceMonth = inflate.findViewById(R.id.tv_loan_price_month);
//        tvTitleExamine = inflate.findViewById(R.id.tv_title_examine);
        tvSubmitSuccess = inflate.findViewById(R.id.tv_submit_success);
        tvSubmitSuccessDescription = inflate.findViewById(R.id.tv_submit_success_description);
        line1 = inflate.findViewById(R.id.line_1);
        line2 = inflate.findViewById(R.id.line_2);
        tvTransferMoney = inflate.findViewById(R.id.tv_transfer_money);
        tvTransferMoneyDescription = inflate.findViewById(R.id.tv_transfer_money_description);
        line3 = inflate.findViewById(R.id.line_3);
        line4 = inflate.findViewById(R.id.line_4);
        tvLoanSuccess = inflate.findViewById(R.id.tv_loan_success);
        tvLoanSuccessDescription = inflate.findViewById(R.id.tv_loan_success_description);
        rlStep3 = inflate.findViewById(R.id.rl_step_3);
        tvPriceDefault = inflate.findViewById(R.id.tv_price_default);
        tvTermDefault = inflate.findViewById(R.id.tv_term_default);
        btnApplyDefault = inflate.findViewById(R.id.btn_apply_default);
        btnApplyDefault.setOnClickListener(this);
        tvTipOneDefault = inflate.findViewById(R.id.tv_tip_one_default);
        tvTipTwoDefault = inflate.findViewById(R.id.tv_tip_two_default);
        rlHomeDefault = inflate.findViewById(R.id.rl_home_default);
        tvPriceUncertified = inflate.findViewById(R.id.tv_price_uncertified);
        seekCashLoanMoneyUncertified = inflate.findViewById(R.id.seek_cash_loan_money_uncertified);
        tvCashLoanMinAmountUncertified = inflate.findViewById(R.id.tv_cash_loan_min_amount_uncertified);
        tvCashLoanMaxAmountUncertified = inflate.findViewById(R.id.tv_cash_loan_max_amount_uncertified);
        rvTermUncertified = inflate.findViewById(R.id.rv_term_uncertified);
        tvTipPeriodUncertified = inflate.findViewById(R.id.tv_tip_period_uncertified);
        tvTipMonthlyUncertified = inflate.findViewById(R.id.tv_tip_monthly_uncertified);
        tvTipMonthlyUncertified.setOnClickListener(this);
        tvTipTwoUncertified = inflate.findViewById(R.id.tv_tip_two_uncertified);
        tvTipInterestUncertified = inflate.findViewById(R.id.tv_tip_interest_uncertified);
        rlHomeUncertified = inflate.findViewById(R.id.rl_home_uncertified);
        btnApplyUncertified = inflate.findViewById(R.id.btn_apply_uncertified);
        btnApplyUncertified.setOnClickListener(this);
        tvPriceCertified = inflate.findViewById(R.id.tv_price_certified);
        seekCashLoanMoneyCertified = inflate.findViewById(R.id.seek_cash_loan_money_certified);
        tvCashLoanMinAmountCertified = inflate.findViewById(R.id.tv_cash_loan_min_amount_certified);
        tvCashLoanMaxAmountCertified = inflate.findViewById(R.id.tv_cash_loan_max_amount_certified);
        rvTermCertified = inflate.findViewById(R.id.rv_term_certified);
        tvTipOneCertified = inflate.findViewById(R.id.tv_tip_one_certified);
        tvTipOneCertified.setOnClickListener(this);
        tvTipTwoCertified = inflate.findViewById(R.id.tv_tip_two_certified);
        tvTipInterestCertified = inflate.findViewById(R.id.tv_tip_interest_certified);
        rlHomeCertified = inflate.findViewById(R.id.rl_home_certified);
        btnDefaultApplyCertified = inflate.findViewById(R.id.btn_default_apply_certified);
        btnDefaultApplyCertified.setOnClickListener(this);
        tvRepaymentTitle = inflate.findViewById(R.id.tv_repayment_title);
        tvRepaymentPrice = inflate.findViewById(R.id.tv_repayment_price);
        tvRepaymentTimeTitle = inflate.findViewById(R.id.tv_repayment_time_title);
        tvRepaymentDate = inflate.findViewById(R.id.tv_repayment_date);
        tvTermRepayment = inflate.findViewById(R.id.tv_term_repayment);
        btnRepayment = inflate.findViewById(R.id.btn_repayment);
        btnRepayment.setOnClickListener(this);
        rlMarquee = inflate.findViewById(R.id.rl_marquee);
        marqueeView = inflate.findViewById(R.id.mv_scroll);
        ivMarqueeClose = inflate.findViewById(R.id.iv_marquee_close);
        ivMarqueeClose.setOnClickListener(this);
        tvTipBottomRepayment = inflate.findViewById(R.id.tv_tip_bottom_repayment);
        rlRepaymentInfo = inflate.findViewById(R.id.rl_repayment_info);
        tvRepaymentPricePeriodTitle = inflate.findViewById(R.id.tv_repayment_price_period_title);
        tvRepaymentPriceThisPeriod = inflate.findViewById(R.id.tv_repayment_price_this_period);
        tvRepaymentDateLast = inflate.findViewById(R.id.tv_repayment_date_last);
        llHomeLoan = inflate.findViewById(R.id.ll_home_loan);
//        tvTipStudent = inflate.findViewById(R.id.tv_tip_student);
//        tvBrandTitleProgress = inflate.findViewById(R.id.tv_brand_title_progress);
//        tvTipPriceProgress = inflate.findViewById(R.id.tv_tip_price_progress);
        tvLoanPriceProgress = inflate.findViewById(R.id.tv_loan_price_progress);
        tvTipCertified = inflate.findViewById(R.id.tv_tip_certified);
        cvOvertimeInfo = inflate.findViewById(R.id.cv_overtime_info);
        tvRepaymentSurplus = inflate.findViewById(R.id.tv_repayment_price_surplus);
        btnRepaymentAdvance = inflate.findViewById(R.id.btn_repayment_advance);
        btnRepaymentAdvance.setOnClickListener(this);
        rlCurrMarquee = inflate.findViewById(R.id.rl_marquee_curr);
        currMarqueeView = inflate.findViewById(R.id.mv_scroll_curr);
        ivCurrMarqueeClose = inflate.findViewById(R.id.iv_marquee_close_curr);

        stringList = Arrays.asList(getActivity().getResources().getStringArray(R.array.array_term_test));
        for (int i = 0; i < stringList.size(); i++) {
            EbeiHomeTermModel ebeiHomeTermModel = new EbeiHomeTermModel();
            ebeiHomeTermModel.setTerm(stringList.get(i));
            if (i == 0) {
                ebeiHomeTermModel.setChecked(true);
            } else {
                ebeiHomeTermModel.setChecked(false);
            }
            ebeiHomeTermModels.add(i, ebeiHomeTermModel);
        }
        adapter = new EbeiSingleChoiceTermAdapter(getActivity(), ebeiHomeTermModels, onItemClickListener);
    }

    /**
     * 设置默认视图
     *
     * @param ebeiHomeModel
     */
    private void setDefault(EbeiHomeModel ebeiHomeModel) {
//        ivDefaultBg.setVisibility(View.VISIBLE);
//        includeBrandInfo.setVisibility(View.VISIBLE);
        includeHomeDefault.setVisibility(View.VISIBLE);
        includeProgress.setVisibility(View.GONE);
        includeUnCertified.setVisibility(View.GONE);
        includeCertified.setVisibility(View.GONE);
        includeHomeRepayment.setVisibility(View.GONE);
        includeCurrFinish.setVisibility(View.GONE);
        //设置最高上限
        tvPriceDefault.setText(String.valueOf(ebeiHomeModel.getUpAmount()));
    }

    /**
     * 设置未认证视图
     *
     * @param ebeiHomeModel
     */
    private void setUnCertified(EbeiHomeModel ebeiHomeModel) {
//        ivDefaultBg.setVisibility(View.VISIBLE);
//        includeBrandInfo.setVisibility(View.VISIBLE);
        includeHomeDefault.setVisibility(View.GONE);
        includeProgress.setVisibility(View.GONE);
        includeUnCertified.setVisibility(View.VISIBLE);
        includeCertified.setVisibility(View.GONE);
        includeHomeRepayment.setVisibility(View.GONE);
        includeCurrFinish.setVisibility(View.GONE);
        rvTermUncertified.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        rvTermUncertified.setAdapter(adapter);
        seekCashLoanMoneyUncertified.setOnSeekBarChangeListener(onSeekBarChangeListener);
        //设置借款的金额
        tvPriceUncertified.setText(String.valueOf(ebeiHomeModel.getDownAmount()));
        //设置上下限制
        seekCashLoanMoneyCertified.setLeft(ebeiHomeModel.getDownAmount().intValue());
        seekCashLoanMoneyCertified.setRight(ebeiHomeModel.getUpAmount().intValue());
        tvCashLoanMaxAmountUncertified.setText(ebeiHomeModel.getUpAmount().toString());
        tvCashLoanMinAmountUncertified.setText(ebeiHomeModel.getDownAmount().toString());
        //设置期数
        ebeiHomeTermModels.clear();
        for (int i = 0; i < model.getPeriods(); i++) {
            EbeiHomeTermModel e = new EbeiHomeTermModel();
            e.setTerm(stringList.get(i));
            if (i == 0) {
                e.setChecked(true);
            } else {
                e.setChecked(false);
            }
            ebeiHomeTermModels.add(i, e);
        }
        adapter.setDataList(ebeiHomeTermModels);
        //请求到数据计算一次
        float progressPercent = (float)
                seekCashLoanMoneyUncertified.getProgress() / (float) seekCashLoanMoneyUncertified.getMax();
        refreshAnalyzePeriods(getCashAmount(progressPercent),
                adapter.getCurrentCheckPosition());
    }

    /**
     * 设置已认证视图
     *
     * @param ebeiHomeModel
     */
    private void setCertified(EbeiHomeModel ebeiHomeModel) {
//        ivDefaultBg.setVisibility(View.VISIBLE);
//        includeBrandInfo.setVisibility(View.VISIBLE);
        includeHomeDefault.setVisibility(View.GONE);
        includeProgress.setVisibility(View.GONE);
        includeUnCertified.setVisibility(View.GONE);
        includeCertified.setVisibility(View.VISIBLE);
        includeHomeRepayment.setVisibility(View.GONE);
        includeCurrFinish.setVisibility(View.GONE);
        rvTermCertified.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        rvTermCertified.setAdapter(adapter);
        seekCashLoanMoneyCertified.setOnSeekBarChangeListener(onSeekBarChangeListener);
        //设置借款的金额
        tvPriceCertified.setText(String.valueOf(ebeiHomeModel.getDownAmount()));
        //设置上下限制
        seekCashLoanMoneyCertified.setLeft(ebeiHomeModel.getDownAmount().intValue());
        seekCashLoanMoneyCertified.setRight(ebeiHomeModel.getUpAmount().intValue());
        tvCashLoanMaxAmountCertified.setText(ebeiHomeModel.getUpAmount().toString());
        tvCashLoanMinAmountCertified.setText(ebeiHomeModel.getDownAmount().toString());
        //设置期数
        ebeiHomeTermModels.clear();
        for (int i = 0; i < model.getPeriods(); i++) {
            EbeiHomeTermModel e = new EbeiHomeTermModel();
            e.setTerm(stringList.get(i));
            if (i == 0) {
                e.setChecked(true);
            } else {
                e.setChecked(false);
            }
            ebeiHomeTermModels.add(i, e);
        }
        adapter.setDataList(ebeiHomeTermModels);
        //请求到数据计算一次
        float progressPercent = (float)
                seekCashLoanMoneyCertified.getProgress() / (float) seekCashLoanMoneyCertified.getMax();
        refreshAnalyzePeriods(getCashAmount(progressPercent),
                adapter.getCurrentCheckPosition());
    }

    /**
     * 设置借款状态视图
     *
     * @param ebeiHomeModel
     */
    private void setLoanInfo(EbeiHomeModel ebeiHomeModel) {
//        ivDefaultBg.setVisibility(View.GONE);
//        includeBrandInfo.setVisibility(View.GONE);
        includeHomeDefault.setVisibility(View.GONE);
        includeProgress.setVisibility(View.VISIBLE);
        includeUnCertified.setVisibility(View.GONE);
        includeCertified.setVisibility(View.GONE);
        includeHomeRepayment.setVisibility(View.GONE);
        includeCurrFinish.setVisibility(View.GONE);

        tvLoanPriceProgress.setText(String.valueOf(ebeiHomeModel.getLoanAmount()));
        tvLoanTerm.setText(ebeiHomeModel.getAllPeriods() + "个月");
        tvLoanPriceMonth.setText(EbeiAppUtils.formatAmount(ebeiHomeModel.getMonthAmount()));
        if ("WAIT_LOAN".equals(ebeiHomeModel.getLoanReviewStatus())) {
            line2.setBackground(getResources().getDrawable(R.drawable.rotate_line_dotted_progress_blue));
            line3.setBackground(getResources().getDrawable(R.drawable.rotate_line_dotted_progress_blue));
            tvTransferMoney.setTextColor(getResources().getColor(R.color.color_colorPrimary));
            tvTransferMoney.setCompoundDrawablesWithIntrinsicBounds
                    (R.mipmap.icon_homepage_reviewprogress_select_sucess, 0, 0, 0);
        }
        tvLoanSuccessDescription.setText(ebeiHomeModel.getBankName() + "(" +
                ebeiHomeModel.getCardNo().substring(ebeiHomeModel.getCardNo().length() - 4) + ")");
    }

    /**
     * 设置还款未逾期状态视图
     *
     * @param ebeiHomeModel
     */
    private void setRepaymentInfo(EbeiHomeModel ebeiHomeModel) {
        if (EbeiModelEnum.WAIT_REPAY.getModel().equals(ebeiHomeModel.getLoanStatus())) {
            btnRepayment.setText("立即还款");
            btnRepayment.setEnabled(true);
        }
//        ivDefaultBg.setVisibility(View.VISIBLE);
//        includeBrandInfo.setVisibility(View.VISIBLE);
        includeHomeDefault.setVisibility(View.GONE);
        includeProgress.setVisibility(View.GONE);
        includeUnCertified.setVisibility(View.GONE);
        includeCertified.setVisibility(View.GONE);
        includeHomeRepayment.setVisibility(View.VISIBLE);
        includeCurrFinish.setVisibility(View.GONE);

        tvRepaymentPrice.setText(EbeiAppUtils.formatAmount(ebeiHomeModel.getCurrWaitRepayAmount()));
        tvRepaymentTimeTitle.setText("还款倒计时" + ebeiHomeModel.getCountdownDay() + "天");
        if (EbeiMiscUtils.isNotEmpty(ebeiHomeModel.getRepayDay())) {
            tvRepaymentDate.setText(ebeiHomeModel.getRepayDay().split(" ")[0].substring(5));
        }
        if (ebeiHomeModel.getRepayingAmount() != null && 0 < ebeiHomeModel.getRepayingAmount().doubleValue()) {
            tvTermRepayment.setVisibility(View.VISIBLE);
            tvTermRepayment.setText(String.format(getResources().getString(R.string.formatter_home_repaying_amount), EbeiAppUtils.formatAmount(ebeiHomeModel.getRepayingAmount())));
        } else {
            tvTermRepayment.setVisibility(View.INVISIBLE);
        }
        cvOvertimeInfo.setVisibility(View.INVISIBLE);
        if (EbeiMiscUtils.isNotEmpty(ebeiHomeModel.getRepayFailMsg())) {
            rlMarquee.setVisibility(View.VISIBLE);
            marqueeView.setText(ebeiHomeModel.getRepayFailMsg());
        } else {
            rlMarquee.setVisibility(View.GONE);
        }
    }

    /**
     * 还款逾期
     *
     * @param ebeiHomeModel
     */
    private void setRepaymentOverTimeInfo(EbeiHomeModel ebeiHomeModel) {
//        ivDefaultBg.setVisibility(View.VISIBLE);
//        includeBrandInfo.setVisibility(View.VISIBLE);
        includeHomeDefault.setVisibility(View.GONE);
        includeProgress.setVisibility(View.GONE);
        includeUnCertified.setVisibility(View.GONE);
        includeCertified.setVisibility(View.GONE);
        includeHomeRepayment.setVisibility(View.VISIBLE);
        includeCurrFinish.setVisibility(View.GONE);

        tvRepaymentPrice.setText(EbeiAppUtils.formatAmount(ebeiHomeModel.getOverdueWaitAmount()));
        tvRepaymentTimeTitle.setTextColor(getResources().getColor(R.color.red));
        tvRepaymentTimeTitle.setText("已逾期" + ebeiHomeModel.getOverDueCountdownDay() + "天");
        tvRepaymentDate.setText(ebeiHomeModel.getOverDueRepayDay().split(" ")[0].substring(5));
        tvTermRepayment.setVisibility(View.VISIBLE);
//        tvTermRepayment.setText("含还款中金额" + EbeiAppUtils.formatAmount(ebeiHomeModel.getCurrWaitRepayAmount()));
        tvTermRepayment.setVisibility(View.GONE);

        if (model.getCurrWaitRepayAmount().doubleValue() > 0) {
            cvOvertimeInfo.setVisibility(View.VISIBLE);
            tvRepaymentPriceThisPeriod.setText(EbeiAppUtils.formatAmount(ebeiHomeModel.getCurrWaitRepayAmount()));
            tvRepaymentDateLast.setText("最后还款日" + ebeiHomeModel.getRepayDay().split(" ")[0]);
        } else {
            cvOvertimeInfo.setVisibility(View.GONE);
        }
        if (EbeiMiscUtils.isNotEmpty(ebeiHomeModel.getRepayFailMsg())) {
            rlMarquee.setVisibility(View.VISIBLE);
            marqueeView.setText(ebeiHomeModel.getRepayFailMsg());
        } else {
            rlMarquee.setVisibility(View.GONE);
        }
    }

    /**
     * 本期已还清
     *
     * @param ebeiHomeModel
     */
    private void setCurrFinishInfo(EbeiHomeModel ebeiHomeModel) {
//        ivDefaultBg.setVisibility(View.VISIBLE);
//        includeBrandInfo.setVisibility(View.VISIBLE);
        includeHomeDefault.setVisibility(View.GONE);
        includeProgress.setVisibility(View.GONE);
        includeUnCertified.setVisibility(View.GONE);
        includeCertified.setVisibility(View.GONE);
        includeHomeRepayment.setVisibility(View.GONE);
        includeCurrFinish.setVisibility(View.VISIBLE);
        tvRepaymentSurplus.setText(EbeiAppUtils.formatAmount(ebeiHomeModel.getAllWaitRepayAmount()));
        if (EbeiModelEnum.Y.getModel().equals(ebeiHomeModel.getAllRepaySwitch())) {
            btnRepaymentAdvance.setEnabled(true);
        } else {
            btnRepaymentAdvance.setEnabled(false);
        }
        if (EbeiMiscUtils.isNotEmpty(ebeiHomeModel.getRepayFailMsg())) {
            rlCurrMarquee.setVisibility(View.VISIBLE);
            currMarqueeView.setText(ebeiHomeModel.getRepayFailMsg());
        } else {
            rlCurrMarquee.setVisibility(View.GONE);
        }
    }

    /**
     * 还款中页面
     *
     * @param ebeiHomeModel
     */
    private void setRepaymentInfoIng(EbeiHomeModel ebeiHomeModel) {
        btnRepayment.setText("还款中");
        btnRepayment.setEnabled(false);
        if ("OVERDUE".equals(ebeiHomeModel.getLoanStatus())) {
            setRepaymentOverTimeInfo(ebeiHomeModel);
        } else {
            setRepaymentInfo(ebeiHomeModel);
        }
    }

    /**
     * 刷新月供的金额和利息
     *
     * @param amout
     * @param position
     */
    private void refreshAnalyzePeriods(final double amout, final int position) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amout);
        jsonObject.put("periods", position + 1);
        EbeiClient.getService(EbeiApi.class)
                .getAnalyzePeriods(jsonObject)
                .compose(EbeiRxUtils.<EbeiAnalyzePeriodsModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiAnalyzePeriodsModel>() {
                    @Override
                    public void onNext(EbeiAnalyzePeriodsModel ebeiAnalyzePeriodsModels) {
                        periodsModel = ebeiAnalyzePeriodsModels;
                        super.onNext(ebeiAnalyzePeriodsModels);
                        List<EbeiAnalyzePeriodsModelData> periods = ebeiAnalyzePeriodsModels.getPeriods();
                        if (isCertified) {
                            tvTipTwoCertified.setText(EbeiAppUtils.
                                    formatAmount(periods.get(0).getAmount()));
                            tvTipInterestCertified.setText("(含利息、服务费：" +
                                    String.valueOf(periodsModel.getTotalProfit().doubleValue()) + ")");
                        } else {
                            tvTipTwoUncertified.setText(EbeiAppUtils.
                                    formatAmount(periods.get(0).getAmount()));
                            tvTipInterestUncertified.setText("(含利息、服务费：" +
                                    String.valueOf(periodsModel.getTotalProfit().doubleValue()) + ")");
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
     * 刷新资金相关界面
     */
    private void refreshAmountView(double progressPercent) {
        double cashAmount;
        cashAmount = getCashAmount(progressPercent);
        if (isCertified) {
            seekCashLoanMoneyCertified.setProgress((int) (progressPercent * (double) 100));
            tvPriceCertified.setText(EbeiAppUtils.deleteFormatAmount(cashAmount + ""));
        } else {
            seekCashLoanMoneyUncertified.setProgress((int) (progressPercent * (double) 100));
            tvPriceUncertified.setText(EbeiAppUtils.deleteFormatAmount(cashAmount + ""));
        }
    }

    /**
     * 计算对应百分比的金额
     *
     * @param progressPercent
     * @return
     */
    private double getCashAmount(double progressPercent) {
        double cashAmount;
        if (model == null) {
            cashAmount = CalAmountProgress(new BigDecimal("0"), new BigDecimal("0"), progressPercent);
        } else {
            cashAmount = CalAmountProgress(model.getDownAmount(), model.getUpAmount(), progressPercent);
        }
        if (cashAmount % 100 != 0 || cashAmount % 100 < 50) {
            cashAmount = cashAmount - cashAmount % 100;
        } else if (cashAmount % 100 >= 50) {
            cashAmount = cashAmount + 100;
        }
        return cashAmount;
    }

    /**
     * 金额进度计算
     *
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param percent   百分比
     */
    private double CalAmountProgress(BigDecimal minAmount, BigDecimal maxAmount, double percent) {
        BigDecimal amountLimit = maxAmount.subtract(minAmount);
        return amountLimit.multiply(new BigDecimal(percent)).add(minAmount).doubleValue();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_REFRESH_STATUS && resultCode == Activity.RESULT_OK) {
            loadData(EbeiConfig.isLand());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void userLandCheck() {
        EbeiClient.getService(EbeiApi.class)
                .userLandCheck()
                .compose(EbeiRxUtils.<EbeiLandCheckModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiLandCheckModel>() {
                    @Override
                    public void onNext(final EbeiLandCheckModel ebeiLandCheckModel) {
                        super.onNext(ebeiLandCheckModel);
                        if (ebeiLandCheckModel == null) {
                            EbeiUIUtils.showToast("获取信息失败，请重试");
                            return;
                        }
                        if (!ebeiLandCheckModel.isOkLoan()) {
                            if (!EbeiMiscUtils.isEmpty(ebeiLandCheckModel.getMsg())) {
                                new EbeiCustomDialog.Builder(getActivity())
                                        .setCancelable(false)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setMessage(ebeiLandCheckModel.getMsg())
                                        .create()
                                        .show();
                                return;
                            }
                            if ("creditAuth".equals(ebeiLandCheckModel.getRedirectType())) {
                                Intent intent = new Intent();
                                intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, EbeiConstant.URL_AUTH);
                                EbeiActivityUtils.push(EbeiHtml5WebView.class, intent, EbeiBundleKeys.REQUEST_CODE_AUTH);
                                return;
                            }
                            if ("identityAuth".equals(ebeiLandCheckModel.getRedirectType())) {
                                EbeiActivityUtils.push(EbeiIdAuthActivity.class, EbeiBundleKeys.REQUEST_CODE_AUTH);
                            }
                        } else {
                            //借款
                            Intent loanIntent = new Intent();
                            float progress = (float) seekCashLoanMoneyCertified.getProgress() /
                                    (float) seekCashLoanMoneyCertified.getMax();
                            loanIntent.putExtra(EbeiConstant.APP_LOAN_AMOUNT, getCashAmount(progress));
                            loanIntent.putExtra(EbeiConstant.APP_LOAN_PERIOD,
                                    adapter.getCurrentCheckPosition() + 1);
                            loanIntent.putExtra(EbeiConstant.APP_LOAN_MONTH_AMOUNT,
                                    JSONObject.toJSONString(periodsModel.getPeriods()));
                            if (!EbeiMiscUtils.isEmpty(model.getCardNo())) {
                                loanIntent.putExtra(EbeiConstant.APP_LOAN_HAD_BANK_CARD, true);
                                loanIntent.putExtra(EbeiConstant.APP_LOAN_BANK_NAME, model.getBankName());
                                loanIntent.putExtra(EbeiConstant.APP_LOAN_BANK_CARD_NO, model.getCardNo());
                            }
                            EbeiActivityUtils.push(EbeiLoanDetailSubmitActivity.class, loanIntent, EbeiBundleKeys.REQUEST_CODE_LOAN);
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        return;
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_apply_default) {
            EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
        } else if (id == R.id.btn_apply_uncertified) {
            userLandCheck();
        } else if (id == R.id.btn_default_apply_certified) {
            userLandCheck();
        } else if (id == R.id.btn_repayment) {
            Intent intent = new Intent();
            intent.putExtra("loanId", model.getLoanId());
            intent.putExtra("repayAmount", model.getCurrWaitRepayAmount().doubleValue());
            if (EbeiModelEnum.OVERDUE.getModel().equals(model.getLoanStatus())) {
                BigDecimal all = model.getCurrWaitRepayAmount().add(model.getOverdueWaitAmount());
                intent.putExtra("overdueWaitAmount", model.getOverdueWaitAmount().doubleValue());
            }
            intent.putExtra("allRepaymentAmount", model.getAllRepayAmount().doubleValue());
            intent.putExtra("loanStatus", model.getLoanStatus());
            intent.putExtra("allRepaySwitch", model.getAllRepaySwitch());
            intent.putExtra("partRepaySwitch", model.getPartRepaySwitch());
            intent.putExtra("remitList", (Serializable) model.getRemitList());
            intent.putExtra("allWaitRepayAmount", model.getAllWaitRepayAmount().doubleValue());
            EbeiActivityUtils.push(EbeiRepaymentActivity.class, intent, EbeiBundleKeys.REQUEST_CODE_REPAYMENT);
        } else if (id == R.id.tv_tip_monthly_uncertified) {
            new EbeiMonthlySupplyDialog.Builder(getActivity())
                    .setData(periodsModel.getPeriods())
                    .create()
                    .show();
        } else if (id == R.id.tv_tip_one_certified) {
            new EbeiMonthlySupplyDialog.Builder(getActivity())
                    .setData(periodsModel.getPeriods())
                    .create()
                    .show();
        } else if (id == R.id.btn_repayment_advance) {
            Intent intentAdvance = new Intent();
            intentAdvance.putExtra("loanId", model.getLoanId());
            intentAdvance.putExtra("repayAmount", model.getAllRepayAmount().doubleValue());
            intentAdvance.putExtra("allWaitRepayAmount", model.getAllWaitRepayAmount().doubleValue());
            intentAdvance.putExtra("remitList", (Serializable) model.getRemitList());
            EbeiActivityUtils.push(EbeiRepaymentAdvanceActivity.class, intentAdvance, EbeiBundleKeys.REQUEST_CODE_REPAYMENT);
        } else if (id == R.id.iv_marquee_close) {
            rlMarquee.setVisibility(View.GONE);
        }
    }

    @Override
    public void success() {
        loadData(EbeiConfig.isLand());
    }
}
