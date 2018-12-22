package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.dushi.model.EbeiBankCardModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.entity.EbeiApiResponse;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.log.EbeiLogger;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

/**
 * 银行卡信息
 * Created by ywd on 2018/11/19.
 */

public class EbeiBankCardInfoActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private static final String TAG = "EbeiBankCardInfoActivity";
    private EbeiBankCardModel ebeiBankCardModel;
    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvIsMain;
    private TextView tvCardNum;
    private EbeiNoDoubleClickButton btnSetMain;
    private EbeiNoDoubleClickButton btnDelete;
    private Context context;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_set_main) {
            if (EbeiMiscUtils.isEmpty(getRid())) {
                EbeiUIUtils.showToast("银行卡信息获取失败，请重试");
                return;
            }
            setMain(ebeiBankCardModel.getRid());
        } else if (id == R.id.btn_delete) {
            if (EbeiMiscUtils.isEmpty(getRid())) {
                EbeiUIUtils.showToast("银行卡信息获取失败，请重试");
                return;
            }
            delete(ebeiBankCardModel.getRid());
        }
    }

    @Override
    public String getStatName() {
        return "银行卡详情";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_bank_card_info;
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
        init();
        setListener();
    }

    private void init() {
        ivIcon = findViewById(R.id.image);
        tvName = findViewById(R.id.tv_name);
        tvIsMain = findViewById(R.id.tv_is_main);
        tvCardNum = findViewById(R.id.tv_card_num);
        btnSetMain = findViewById(R.id.btn_set_main);
        btnDelete = findViewById(R.id.btn_delete);
        Intent intent = getIntent();
        ebeiBankCardModel = (EbeiBankCardModel) intent.getSerializableExtra("bank_model");
        if (ebeiBankCardModel != null) {
            String url = ebeiBankCardModel.getBankIcon();
            String bankName = ebeiBankCardModel.getBankName();
            String isMain = ebeiBankCardModel.getIsMain();
            String bankCardNum = ebeiBankCardModel.getCardNumber();

            if (EbeiMiscUtils.isNotEmpty(url)) {
                Glide.with(context).load(url).into(ivIcon);
            }
            if (EbeiMiscUtils.isNotEmpty(bankName)) {
                tvName.setText(bankName);
            }
            if ("Y".equals(isMain)) {
                tvIsMain.setVisibility(View.VISIBLE);
                btnSetMain.setVisibility(View.GONE);
            } else {
                tvIsMain.setVisibility(View.GONE);
                btnSetMain.setVisibility(View.VISIBLE);
            }
            if (EbeiMiscUtils.isNotEmpty(bankCardNum)) {
                tvCardNum.setText(getString(R.string.bank_card_no_list, bankCardNum.substring(bankCardNum.length() - 4)));
            }
        }
    }

    private void setListener() {
        btnSetMain.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    /**
     * 设置主卡
     *
     * @param rid 卡id
     */
    private void setMain(String rid) {
        JSONObject dataObj = new JSONObject();
        dataObj.put("rid", rid);
        EbeiClient.getService(EbeiApi.class)
                .setMain(dataObj)
                .compose(EbeiRxUtils.<EbeiApiResponse>io_main())
                .subscribe(new EbeiBaseObserver<EbeiApiResponse>() {
                    @Override
                    public void onNext(EbeiApiResponse versionInfo) {
                        super.onNext(versionInfo);
                        EbeiLogger.d(TAG, "设置成功");
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
     * 删除银行卡
     *
     * @param rid
     */
    private void delete(String rid) {
        JSONObject dataObj = new JSONObject();
        dataObj.put("rid", rid);
        dataObj.put("clientType", "02");
        EbeiClient.getService(EbeiApi.class)
                .delete(dataObj)
                .compose(EbeiRxUtils.<EbeiApiResponse>io_main())
                .subscribe(new EbeiBaseObserver<EbeiApiResponse>() {
                    @Override
                    public void onNext(EbeiApiResponse versionInfo) {
                        super.onNext(versionInfo);
                        EbeiUIUtils.showToast("删除成功");
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
     * 获取银行卡id
     *
     * @return
     */
    private String getRid() {
        return ebeiBankCardModel == null || ebeiBankCardModel.getRid() == null ? null : ebeiBankCardModel.getRid();
    }
}
