package com.ald.ebei.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//import cn.jpush.android.api.JPushInterface;

public class EbeiJpushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
////            Log.d(TAG, "JPush 用户注册成功");
//        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
////            EbeiLogger.d(TAG, "接受到推送下来的自定义消息");
//        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
////            EbeiLogger.d(TAG, "接受到推送下来的通知");
//        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
////            EbeiLogger.d(TAG, "用户点击打开了通知");
//            processCustomMessage(context, intent.getExtras());
//        } else {
//        }
    }

    private void processCustomMessage(Context context, Bundle bundle) {
//        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
//        JpushModel jpushModel = JSONObject.parseObject(extra, JpushModel.class);
//        String url = jpushModel.getUrl();
//        String type = jpushModel.getType();
//        //0是打开webview
//        if ("0".equals(type)) {
//            Intent intent = new Intent(context, EbeiHtml5WebView.class);
//            intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, url);
//            context.startActivity(intent);
//        }
    }
}
