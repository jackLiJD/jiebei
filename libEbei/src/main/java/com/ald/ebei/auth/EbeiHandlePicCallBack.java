package com.ald.ebei.auth;

/**
 * 图片处理回调
 * Created by sean yu on 2017/7/21.
 */

public interface EbeiHandlePicCallBack {

    /**
     * 图片处理开始
     */
    void onStart();

    /**
     * 图片处理中
     */
    void onHandle();

    /**
     * 图片处理成功
     *
     * @param picPath 图片路径
     */
    void onSuccess(String[] picPath);

    /**
     * 图片处理取消
     */
    void onCancel();

    /**
     * 读取身份证正面信息完成
     */
    void onIDInfoRecognized(EbeiIDCardInfoModel.CardInfo cardInfo);
}
