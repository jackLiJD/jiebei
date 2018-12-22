package com.ald.ebei.auth.liveness;

import android.content.Intent;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;

/*
 * Created by sean yu on 2017/7/23.
 */

public interface EbeiILiveness {

    /**
     * 启动人脸识别
     */
    void startLiveness(EbeiUploadCardResultModel model, String scene);

    /**
     * 活体识别返回值处理
     *
     */
    void handleLivenessResult(int requestCode, int resultCode, Intent data);
}
