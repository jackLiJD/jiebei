package com.ald.ebei.auth.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ald.ebei.R;
import com.ald.ebei.EbeiTopBarActivity;
import com.ald.ebei.auth.liveness.EbeiLivenessFactory;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;
import com.ald.ebei.ui.EbeiNoDoubleClickButton;
import com.ald.ebei.util.EbeiAppUtils;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiConstant;

import static com.ald.ebei.auth.liveness.impl.EbeiFaceIDILiveness.FACE_ID_RESULT;
import static com.ald.ebei.auth.liveness.impl.EbeiFaceIDILiveness.REQUEST_CODE_ERR;


public class EbeiStartFaceActivity extends EbeiTopBarActivity implements View.OnClickListener {

    EbeiNoDoubleClickButton btnStart;
    EbeiLivenessFactory factory;
    EbeiUploadCardResultModel mYiTuResultModel;

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_start_face;
    }

    @Override
    protected void setViewModel() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACE_ID_RESULT) {
            //from liveness
            factory.handleLivenessResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_CODE_ERR) {
            factory.startLiveness(mYiTuResultModel, getIntent().getStringExtra(EbeiBundleKeys.INTENT_KEY_CREDIT_PROMOTE_SCENE));
        }

    }

    @Override
    public String getStatName() {
        return "人脸识别";
    }

    @Override
    protected void afterOnCreate() {
        super.afterOnCreate();
        init();
        setListener();
    }

    private void init() {
        setTitle("人脸识别");
        setTitleColor(ContextCompat.getColor(this, R.color.text_important_color));
        setLeftImage(R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                onBackPressed();
            }
        });

        btnStart = findViewById(R.id.btn_start);
        factory = new EbeiLivenessFactory(this);
        this.mYiTuResultModel = (EbeiUploadCardResultModel) getIntent().getSerializableExtra(EbeiBundleKeys.RR_IDF_MODEL);
        EbeiAppUtils.burialPoint(EbeiConstant.APP_PAGE_FACE_LIVENESS, "face_liveness_open");
    }

    private void setListener() {
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_start) {
            factory.startLiveness(mYiTuResultModel, getIntent().getStringExtra(EbeiBundleKeys.INTENT_KEY_CREDIT_PROMOTE_SCENE));
        }
    }
}
