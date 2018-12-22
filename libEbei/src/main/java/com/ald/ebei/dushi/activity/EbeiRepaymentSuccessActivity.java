package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.dushi.model.EbeiRepaymentSuccessModel;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;

/**
 * Created by ywd on 2018/11/30.
 */

public class EbeiRepaymentSuccessActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private Context context;
    private EbeiRepaymentSuccessModel ebeiRepaymentSuccessModel;

    private TextView tvAmount;
    private TextView tvBankName;
    private EbeiNoDoubleClickButton btnFinish;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_confirm) {
            finish();
        }
    }

    @Override
    public String getStatName() {
        return "还款提交成功页面";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_repayment_finish;
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
        initView();
        setListener();
        initData();
    }

    private void initView() {
        tvAmount = findViewById(R.id.tv_repayment_price);
        tvBankName = findViewById(R.id.tv_bank_name);
        btnFinish = findViewById(R.id.btn_confirm);
    }

    private void setListener() {
        btnFinish.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        ebeiRepaymentSuccessModel = (EbeiRepaymentSuccessModel) intent.getSerializableExtra("repaymentSubmitSuccessModel");
        if (ebeiRepaymentSuccessModel != null) {
            tvAmount.setText(ebeiRepaymentSuccessModel.getRepayAmount());
            tvBankName.setText(ebeiRepaymentSuccessModel.getBankName() + "(" + ebeiRepaymentSuccessModel.getCardNum().substring(ebeiRepaymentSuccessModel.getCardNum().length() - 4) + ")");
        }
    }
}
