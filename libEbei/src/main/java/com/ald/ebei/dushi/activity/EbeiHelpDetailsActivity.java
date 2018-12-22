package com.ald.ebei.dushi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.activity.EbeiBaseActivity;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.model.EbeiHelpDetaildsModel;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.util.EbeiConstant;
import com.alibaba.fastjson.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EbeiHelpDetailsActivity extends EbeiBaseActivity {

    private ImageView backIv;
    private TextView title, secondTitle, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_details);
        initView();
        initData();
    }

    private void initData() {
        JSONObject dataObj = new JSONObject();
        dataObj.put("id", EbeiConstant.QUESTION_ID);

        JSONObject reqObj = new JSONObject();
        reqObj.put(EbeiConstant.APP_KEY_PARAM, dataObj);
        EbeiClient
                .getService(EbeiApi.class)
                .helpCenterDetails(reqObj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new EbeiBaseObserver<EbeiHelpDetaildsModel>(EbeiHelpDetailsActivity.this) {
                    @Override
                    public void onNext(EbeiHelpDetaildsModel jsonObject) {
                        super.onNext(jsonObject);
//                        title.setText(jsonObject.getTitle());
                        secondTitle.setText(jsonObject.getTitle());
                        content.setText(jsonObject.getContent());
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);

                    }
                });

    }

    private void initView() {
        backIv = findViewById(R.id.back);
        title = findViewById(R.id.title);
        secondTitle = findViewById(R.id.second_title);
        content = findViewById(R.id.content);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public String getStatName() {
        return "EbeiHelpDetailsActivity";
    }
}
