package com.ald.ebei.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.network.exception.EbeiApiExceptionEnum;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.log.EbeiLogger;


/**
 * 自定义接收器
 */
public class EbeiApiReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER_OPEN = new IntentFilter(EbeiConfig.ACTION_API_OPEN);
    private static final String TAG = EbeiApiReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(EbeiConfig.ACTION_API_OPEN)) {
            EbeiLogger.d(TAG, "收到action不一致的广播");
            return;
        }
        final int errCode = intent.getIntExtra(EbeiConfig.EXTRA_ERR_CODE, 0);
        if (errCode == EbeiApiExceptionEnum.TOKEN_INVALID.getErrorCode() || errCode == EbeiApiExceptionEnum.SIGN_EEOR.getErrorCode()) {
            EbeiLogger.w(TAG, "onReceive, errorCode == " + errCode);
//            EbeiActivityUtils.push(LoginActivity.class, REQ_REFRESH_STATUS);
            EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
        }
    }

}
