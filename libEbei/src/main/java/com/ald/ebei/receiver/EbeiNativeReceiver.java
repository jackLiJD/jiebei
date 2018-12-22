package com.ald.ebei.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.dushi.activity.EbeiDsMainActivity;
import com.ald.ebei.protocol.EbeiCreProtocol;
import com.ald.ebei.protocol.EbeiProtocolUtils;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.alibaba.fastjson.JSONObject;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.log.EbeiLogger;

/**
 * 自定义接收器
 */
public class EbeiNativeReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER_OPEN = new IntentFilter(EbeiProtocolUtils.NATIVE_ACTION);
    private static final String TAG = EbeiNativeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction == null || !intentAction.equals(EbeiProtocolUtils.NATIVE_ACTION)) {
            EbeiLogger.d(TAG, "收到action不一致的广播");
            return;
        }
        String name = intent.getStringExtra(EbeiProtocolUtils.NATIVE_NAME);
        final String param = intent.getStringExtra(EbeiProtocolUtils.NATIVE_PARAM);
        final String config = intent.getStringExtra(EbeiProtocolUtils.NATIVE_CONFIG);
        JSONObject parseObject = JSONObject.parseObject(param);
        String scene = "";
        if (parseObject != null) {
            scene = parseObject.getString("scene");
        }
        if (!EbeiConfig.isLand() && EbeiMiscUtils.isNotEmpty(config)) {
            JSONObject configJsonObject = JSONObject.parseObject(config);
            String configType = configJsonObject.getString("configType");
            if ("login".equals(configType)) {
//                EbeiActivityUtils.push(GrayCodeLoginActivity.class);
                return;
            }
        }
        //登录
        if (EbeiCreProtocol.NATIVE_LOGIN_FOR_WEB.equals(name)) {
            EbeiConfig.setLand(false);
//            EbeiActivityUtils.push(GrayCodeLoginActivity.class);
        } else if ("APP_HOME".equals(name)) {
            //首页
            Intent i = new Intent();
            i.putExtra(EbeiBundleKeys.MAIN_DATA_TAB, 0);
            EbeiActivityUtils.push(EbeiDsMainActivity.class, i);
            EbeiActivityUtils.pop();
        } else if ("OPEN_NOTIFICATION".equals(name)) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            if (!manager.areNotificationsEnabled()) {
                Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.setData(Uri.fromParts("package", context.getPackageName(), null));
                Activity activity = EbeiActivityUtils.peek();
                activity.startActivity(i);
            }
        }
    }
}
