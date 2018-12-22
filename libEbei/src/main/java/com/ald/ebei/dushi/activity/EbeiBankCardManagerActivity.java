package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.dushi.model.EbeiBankCardListModel;
import com.ald.ebei.dushi.model.EbeiBankCardModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiRxUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ald.ebei.util.EbeiConstant.APP_KEY_BANK_CARD_MANAGER_SELECT_TYPE;

/**
 * 银行卡管理
 * Created by ywd on 2018/11/19.
 */

public class EbeiBankCardManagerActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private List<EbeiBankCardModel> ebeiBankCardModels;
    private RecyclerView rvBankCardList;
    private LinearLayout llEmpty;
    private EbeiNoDoubleClickButton btnAdd;
    private EbeiBankCardListAdapter adapter;
    private String selectType = "normal";
    private Context context;

    @Override
    public String getStatName() {
        return "银行卡管理";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_bank_card_manager;
    }

    @Override
    protected void setViewModel() {

    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        context = this;
        setTitle("银行卡管理");
        setTitleColor(ContextCompat.getColor(this, R.color.text_important_color));
        setLeftImage(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                onBackPressed();
            }
        });
        setRightText("添加", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                EbeiActivityUtils.push(EbeiBankCardAddActivity.class, intent, EbeiBundleKeys.REQUEST_CODE_BANK_CARD_ADD);
            }
        }, getResources().getColor(R.color.color_colorPrimary));
        Intent intent = getIntent();
        selectType = intent.getStringExtra(APP_KEY_BANK_CARD_MANAGER_SELECT_TYPE);
        initView();
        initData();
    }

    private void initView() {
        ebeiBankCardModels = new ArrayList<>();
        rvBankCardList = findViewById(R.id.rv_bank_card);
        llEmpty = findViewById(R.id.ll_empty);
        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
    }

    private void initData() {
        EbeiClient.getService(EbeiApi.class)
                .getBindBankList()
                .compose(EbeiRxUtils.<EbeiBankCardListModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiBankCardListModel>() {
                    @Override
                    public void onNext(EbeiBankCardListModel ebeiBankCardListModel) {
                        super.onNext(ebeiBankCardListModel);
                        ebeiBankCardModels = ebeiBankCardListModel.getBankInfos();
                        if (ebeiBankCardModels.isEmpty()) {
                            llEmpty.setVisibility(View.VISIBLE);
                            rvBankCardList.setVisibility(View.GONE);
                        } else {
                            llEmpty.setVisibility(View.GONE);
                            rvBankCardList.setVisibility(View.VISIBLE);
                            adapter = new EbeiBankCardListAdapter(context, ebeiBankCardModels, onItemClickListener);
                            rvBankCardList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            rvBankCardList.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                    }
                });
    }

    private EbeiBankCardListAdapter.OnItemClickListener onItemClickListener = new EbeiBankCardListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(EbeiBankCardModel itemData, int position) {
            if ("normal".equals(selectType)) {
                Intent intent = new Intent();
                intent.putExtra("bank_model", itemData);
                EbeiActivityUtils.push(EbeiBankCardInfoActivity.class, intent, EbeiBundleKeys.REQUEST_CODE_BANK_CARD_EDIT);
            } else {
                Intent intent = new Intent();
                intent.putExtra("bank_model", itemData);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EbeiBundleKeys.REQUEST_CODE_BANK_CARD_ADD) {
                initData();
            } else if (requestCode == EbeiBundleKeys.REQUEST_CODE_BANK_CARD_EDIT) {
                initData();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_add) {
            Intent intent = new Intent();
            EbeiActivityUtils.push(EbeiBankCardAddActivity.class, intent, EbeiBundleKeys.REQUEST_CODE_BANK_CARD_ADD);
        }
    }
}
