package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.dushi.model.EbeiSupportBankListModel;
import com.ald.ebei.dushi.model.EbeiSupportBankModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiRxUtils;

/**
 * 支持的银行卡列表
 * Created by ywd on 2018/11/25.
 */

public class EbeiSupportBankActivity extends EbeiTopBarActivity {
    private Context context;
    private RecyclerView rvBank;

    @Override
    public String getStatName() {
        return "支持的银行卡列表";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_support_bank;
    }

    @Override
    protected void setViewModel() {

    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        context = this;
        setTitle("支持的银行卡");
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
    }

    private void initView() {
        rvBank = findViewById(R.id.rv_bank);
    }

    private void initData() {
        EbeiClient.getService(EbeiApi.class)
                .listBank()
                .compose(EbeiRxUtils.<EbeiSupportBankListModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiSupportBankListModel>() {
                    @Override
                    public void onNext(EbeiSupportBankListModel supportBankModels) {
                        super.onNext(supportBankModels);
                        if (supportBankModels != null) {
                            rvBank.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            rvBank.setAdapter(new EbeiSupportBankListAdapter(context, supportBankModels.getListBank(), onItemClickListener));
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                    }
                });
    }

    private EbeiSupportBankListAdapter.OnItemClickListener onItemClickListener = new EbeiSupportBankListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(EbeiSupportBankModel itemData, int position) {
            if (itemData != null && !EbeiMiscUtils.isEmpty(itemData.getBankCode())) {
                Intent intent = new Intent();
                intent.putExtra("supportBank", itemData);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    };
}
