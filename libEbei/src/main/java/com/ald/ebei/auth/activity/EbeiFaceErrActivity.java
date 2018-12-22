package com.ald.ebei.auth.activity;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;

/**
 * 人脸识别失败页面
 * Created by ywd on 2018/11/17.
 */

public class EbeiFaceErrActivity extends EbeiTopBarActivity implements View.OnClickListener {
    private TextView tvRestart;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_restart) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public String getStatName() {
        return "人脸识别失败";
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_face_err;
    }

    @Override
    protected void setViewModel() {

    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        init();
        setListener();
    }

    private void init() {
        setTitle("身份认证");
        setTitleColor(ContextCompat.getColor(this, R.color.text_important_color));
        setLeftImage(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                onBackPressed();
            }
        });
        tvRestart = findViewById(R.id.tv_restart);
    }

    private void setListener() {
        tvRestart.setOnClickListener(this);
    }
}
