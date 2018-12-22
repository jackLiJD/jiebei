package com.ald.ebei.auth.idcard;

import android.content.Intent;
import com.ald.ebei.auth.EbeiHandlePicCallBack;

/**
 * 身份认证基类接口
 * Created by sean yu on 2017/7/21.
 */

public interface EbeiIDCardScan {

    /**
     * 认证身份证正面
     */
    void scanFront();

    /**
     * 认证身份证反面
     */
    void scanBack();

    /**
     * 图片处理回调
     *
     * @param callBack 处理返回监听
     */
    EbeiIDCardScan setCallBack(EbeiHandlePicCallBack callBack);

    /**
     * 在onActivity中处理返回图片
     */
    void handleScanResult(int requestCode, int resultCode, Intent data);


    /**
     * 提交图片
     */
    void submitIDCard(String[] pictureArray);

    /**
     * 提交身份证正面，用于绑定银行卡
     */
    void submitIDCard(String picture);
}
