package com.ald.ebei.auth.liveness;

import android.app.Activity;
import android.content.Intent;
import com.ald.ebei.auth.liveness.impl.EbeiFaceIDILiveness;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;

/**
 * 人脸识别工
 * Created by sean yu on 2017/7/23.
 */

public class EbeiLivenessFactory {

    private EbeiILiveness liveness;

    public EbeiLivenessFactory(Activity context) {
        liveness = new EbeiFaceIDILiveness(context);
    }
    /**
     * 启动活体认证
     */
    public void startLiveness(EbeiUploadCardResultModel model, String scene) {
        liveness.startLiveness(model, scene);
    }

    /**
     * 处理人脸识别结果
     */
    public void handleLivenessResult(int requestCode, int resultCode, Intent data) {
        liveness.handleLivenessResult(requestCode, resultCode, data);

    }
}
