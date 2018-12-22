package com.ald.ebei.auth.idcard;

import android.app.Activity;
import android.content.Intent;

import com.ald.ebei.auth.EbeiHandlePicCallBack;
import com.ald.ebei.auth.idcard.impl.EbeiFaceIDCard;
import com.ald.ebei.auth.idcard.impl.EbeiFaceIDCardPhoto;
import com.ald.ebei.network.EbeiNetworkUtil;
import com.ald.ebei.util.EbeiMiscUtils;


/**
 * 身份证认证工厂方法
 * Created by sean yu on 2017/7/21.
 */

public class EbeiIDCardScanFactory {
    private static final String SCAN_ID_CARD_FACE = "FACE_PLUS";
    public static String mSwitchType = "N";
    private boolean isAuthPhoto = false;

    private EbeiIDCardScan ebeiIdCardScan;
    private EbeiHandlePicCallBack mCallBack;
    private Activity mContext;


    public EbeiIDCardScanFactory(Activity context) {
        this.mContext = context;
        mSwitchType = "Y";
        generateIDCardScan(SCAN_ID_CARD_FACE);
    }

    public EbeiIDCardScanFactory setAuthPhoto(boolean authPhoto) {
        isAuthPhoto = authPhoto;
        return this;
    }

    private void generateIDCardScan(String type) {
        if (EbeiMiscUtils.isEmpty(type)) {
            return;
        }

      if (type.equals(SCAN_ID_CARD_FACE)) {
            if (!isAuthPhoto) {
                ebeiIdCardScan = new EbeiFaceIDCard(mContext);
            } else {
                ebeiIdCardScan = new EbeiFaceIDCardPhoto(mContext);
            }
        }
    }

    public EbeiIDCardScanFactory setCallBack(EbeiHandlePicCallBack callBack) {
        this.mCallBack = callBack;
        if (ebeiIdCardScan != null && callBack != null)
            ebeiIdCardScan.setCallBack(mCallBack);
        return this;
    }

    public void scanFront() {
        if (ebeiIdCardScan != null) {
            EbeiNetworkUtil.showCutscenes(mContext, null);
            ebeiIdCardScan.scanFront();
        }
    }

    public void scanBack() {
        if (ebeiIdCardScan != null) {
            EbeiNetworkUtil.showCutscenes(mContext, null);
            ebeiIdCardScan.scanBack();
        }
    }

    /**
     * 提交身份证照片
     */
    public void submitIDCard(String[] picPath) {
        ebeiIdCardScan.submitIDCard(picPath);
    }

    /**
     * @param picPath 正面路径
     */
    public void submitIDCard(String picPath) {
        ebeiIdCardScan.submitIDCard(picPath);
    }

    /**
     */
    public void handleScanResult(int requestCode, int resultCode, Intent data) {
        if (ebeiIdCardScan != null) {
            ebeiIdCardScan.handleScanResult(requestCode, resultCode, data);
        }
    }
}
