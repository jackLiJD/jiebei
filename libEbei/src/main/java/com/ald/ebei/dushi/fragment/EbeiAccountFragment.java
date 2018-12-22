package com.ald.ebei.dushi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.activity.SettingActivityEbei;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.dushi.activity.EbeiBankCardManagerActivity;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiSPUtil;
import com.ald.ebei.util.EbeiUIUtils;


public class EbeiAccountFragment extends Fragment implements View.OnClickListener {
    private ImageView mImgUserHeadPortrait;
    private EbeiNoDoubleClickButton mBtnLogin;
    private TextView mTxtUserPhone;
    private LinearLayout mLLLoanRecord;
    private LinearLayout mLLPayMenthHistory;
    private LinearLayout mLLBankCard;
    private LinearLayout mLLHelpCenter;
    private ImageView mImgSetting;
    private ImageView mImgBack;
    private TextView mTxtTitle;

    private static final int REQ_LOGOUT = 11;
    public static final int REQ_LOGIN = 12;

    public EbeiAccountFragment() {
    }

    public static EbeiAccountFragment newInstance() {
        EbeiAccountFragment fragment = new EbeiAccountFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_account, container, false);
        initView(inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataAndListener();
    }

    private void initDataAndListener() {
        mTxtTitle.setText("我的");
        mImgBack.setVisibility(View.GONE);

        mLLLoanRecord.setOnClickListener(this);
        mLLPayMenthHistory.setOnClickListener(this);
        mLLBankCard.setOnClickListener(this);
        mLLHelpCenter.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mImgSetting.setOnClickListener(this);

        changeUi(EbeiConfig.isLand());
    }

    private void initView(View inflate) {
        mImgUserHeadPortrait = inflate.findViewById(R.id.img_user_head_portrait);
        mBtnLogin = inflate.findViewById(R.id.btn_login);
        mTxtUserPhone = inflate.findViewById(R.id.txt_user_phone);
        mLLLoanRecord = inflate.findViewById(R.id.ll_loan_record);
        mLLPayMenthHistory = inflate.findViewById(R.id.ll_pay_menth_history);
        mLLBankCard = inflate.findViewById(R.id.ll_bank_card);
        mLLHelpCenter = inflate.findViewById(R.id.ll_help_center);
        mImgBack = inflate.findViewById(R.id.layout_head_icon);
        mTxtTitle = inflate.findViewById(R.id.layout_head_title);
        mImgSetting = inflate.findViewById(R.id.img_right);
    }

    private void changeUi(boolean islogin) {
        if (islogin) {
            //todo 加载头像
            mTxtUserPhone.setVisibility(View.VISIBLE);
            StringBuffer userPhone = new StringBuffer();
            String phone = EbeiSPUtil.getString(EbeiConstant.APP_PHONE, "");
            userPhone.append(phone.substring(0, 3))
                    .append("****")
                    .append(phone.substring(7));
            mTxtUserPhone.setText(userPhone.toString());
            mBtnLogin.setVisibility(View.GONE);
        } else {
            mTxtUserPhone.setVisibility(View.GONE);
            mBtnLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
        } else if (id == R.id.img_right) {
            if (!TextUtils.isEmpty(EbeiSPUtil.getString(EbeiConstant.APP_TOKEN, null))) {
                EbeiActivityUtils.push(SettingActivityEbei.class, REQ_LOGOUT);
            } else {
//                    EbeiActivityUtils.push(LoginActivity.class, REQ_LOGIN);
                EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
            }
        } else if (id == R.id.ll_loan_record) {
            if (EbeiConfig.isLand()) {
                Intent loanRecordIntent = new Intent();
                loanRecordIntent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, EbeiConstant.S_MENTH_HISTORY);
                EbeiActivityUtils.push(EbeiHtml5WebView.class, loanRecordIntent);
            } else {
                EbeiUIUtils.showToast("请登录后点击查看！");
//                    EbeiActivityUtils.push(LoginActivity.class, REQ_LOGIN);
                EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
            }
        } else if (id == R.id.ll_pay_menth_history) {
            if (EbeiConfig.isLand()) {
                Intent loanRecordIntent = new Intent();
                loanRecordIntent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, EbeiConstant.REPAY_HISTORY);
                EbeiActivityUtils.push(EbeiHtml5WebView.class, loanRecordIntent);
            } else {
                EbeiUIUtils.showToast("请登录后点击查看！");
//                    EbeiActivityUtils.push(LoginActivity.class, REQ_LOGIN);
                EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
            }
        } else if (id == R.id.ll_bank_card) {
            if (EbeiConfig.isLand()) {
                Intent intent = new Intent();
                intent.putExtra(EbeiConstant.APP_KEY_BANK_CARD_MANAGER_SELECT_TYPE, "normal");
                EbeiActivityUtils.push(EbeiBankCardManagerActivity.class, intent);
            } else {
//                    EbeiActivityUtils.push(LoginActivity.class, REQ_LOGIN);
                EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
            }
        } else if (id == R.id.ll_help_center) {
            Intent intent = new Intent();
            intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, EbeiConstant.HELP_CENTER);
            EbeiActivityUtils.push(EbeiHtml5WebView.class, intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUi(EbeiConfig.isLand());
    }
}
