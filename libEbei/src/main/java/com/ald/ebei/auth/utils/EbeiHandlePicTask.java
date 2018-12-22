package com.ald.ebei.auth.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ald.ebei.auth.EbeiHandlePicCallBack;
import com.ald.ebei.util.EbeiBitmapUtil;

/**
 * 图片处理异步任务
 * 注：默认第一张为身份证正面
 * 第二张为身份证反面
 * Created by sean yu on 2017/7/21.
 */
public class EbeiHandlePicTask extends AsyncTask<byte[], Integer, String[]> {

    private EbeiHandlePicCallBack callBack;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callBack != null) {
            callBack.onStart();
        }
    }

    /**
     * 图片处理回调
     *
     * @param callBack 处理返回监听
     */
    public EbeiHandlePicTask setCallBack(EbeiHandlePicCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    protected String[] doInBackground(byte[]... params) {
        if (callBack != null) {
            callBack.onHandle();
        }
        String[] pictureArray = new String[params.length];
        int len = params.length;
        for (int i = 0; i < len; i++) {
            if (params[i] == null) {
                pictureArray[i] = "";
                continue;
            }
            String saveFrontPath = byte2Path(params[i]);
            String imageCompressPath = EbeiBitmapUtil.compressUploadFile(saveFrontPath, 800, 400);
            pictureArray[i] = imageCompressPath;
        }
        return pictureArray;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        if (callBack != null) {
            callBack.onSuccess(strings);
        }
    }

    /**
     * 储存文件流
     */
    private String byte2Path(byte[] fileByte) {
        Bitmap imageFront = EbeiBitmapUtil.getBitmap(fileByte);
        String saveFilePath = EbeiBitmapUtil.saveBitmap(imageFront);
        if (imageFront != null && !imageFront.isRecycled()) {
            imageFront.recycle();
        }
        return saveFilePath;
    }
}
