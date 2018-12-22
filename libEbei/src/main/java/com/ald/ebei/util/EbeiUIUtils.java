package com.ald.ebei.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;


import com.ald.ebei.config.EbeiConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/4
 * 描述：UI相关的工具类
 * 修订历史：
 */
public class EbeiUIUtils {

    private static final String IO_EXCEPTION = "java.io.IOException:";
    /**
     * toast 过滤 3s内禁止重复出现
     */
    private static final long Interval = 3 * 1000;
    private static final EbeiSoftMap<String, Long> map = new EbeiSoftMap<>();
    private static Toast mToast;
    private static Toast CURR_TOAST;

    public static synchronized void sendBroadcast(String action) {
        sendBroadcast(new Intent(action));
    }

    public static synchronized void sendBroadcast(Intent intent) {
        EbeiConfig.getContext().sendBroadcast(intent);
    }

    /**
     * 通过此方法得到的ProgressDialog需要在activity中显示的dismiss掉，比方说在onStop里面。
     */
    public static ProgressDialog showWaiting(final Activity activity, final String message) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }

        if (onUiThread()) {
            return createWaitingAndShow(activity, message);
        } else {
            return (ProgressDialog) showDialogFromBackgroundThread(activity, new Callable<Dialog>() {
                @Override
                public Dialog call() throws Exception {
                    return createWaitingAndShow(activity, message);
                }
            });
        }
    }

    private static ProgressDialog createWaitingAndShow(Activity activity, String message) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setCancelable(false);
        pd.setMessage(message);
        pd.show();
        return pd;
    }

    /**
     * 通过此方法得到的AlertDialog需要在activity中显示的dismiss掉，比方说在onStop里面。
     */
    public static AlertDialog showDialog(final Activity activity, final String title, final String message) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }

        if (onUiThread()) {
            return createDialogAndShow(activity, title, message);
        } else {
            return (AlertDialog) showDialogFromBackgroundThread(activity, new Callable<Dialog>() {
                @Override
                public Dialog call() throws Exception {
                    return createDialogAndShow(activity, title, message);
                }
            });
        }
    }

    private static AlertDialog createDialogAndShow(Activity activity, String title, String message) {
        Builder builder = new Builder(activity);
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton("确定", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * 通过此方法得到的AlertDialog需要在activity中显示的dismiss掉，比方说在onStop里面。
     */
    public static AlertDialog showConfirm(final Activity activity, final String message,
                                          final OnClickListener okListener, final OnClickListener cancelListener) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }

        if (onUiThread()) {
            return createConfirmAndShow(activity, message, okListener, cancelListener);
        } else {
            return (AlertDialog) showDialogFromBackgroundThread(activity, new Callable<Dialog>() {
                @Override
                public Dialog call() throws Exception {
                    return createConfirmAndShow(activity, message, okListener, cancelListener);
                }
            });
        }
    }

    private static AlertDialog createConfirmAndShow(Activity activity, String message, OnClickListener okListener,
                                                    OnClickListener cancelListener) {
        Builder builder = new Builder(activity);
        builder.setTitle("确认").setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", okListener);
        builder.setNegativeButton("取消", cancelListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * 通过异步转同步的方法显示对话框，并且返回对话框的引用，以方便关闭。
     */
    private static Dialog showDialogFromBackgroundThread(final Activity activity, final Callable<Dialog> callable) {
        final Dialog[] dialog = new Dialog[1];
        final CountDownLatch latch = new CountDownLatch(1);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog[0] = callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dialog[0];
    }

    public static boolean onUiThread() {
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }

    /**
     * 显示toast
     *
     * @param id
     */
    public static void showToast(int id) {
        Context context = EbeiConfig.getContext();
        toast(context, context.getString(id));
    }

    /**
     * 显示toast
     *
     */
    public static void showToast(String msg) {
        toast(EbeiConfig.getContext(), msg);
    }

    /**
     * 显示toast
     *
     */
    public static void showToastWithoutTime(String msg) {
        Toast.makeText(EbeiConfig.getContext(),msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(final Context context, final String msg) {
        long preTime = 0;
        if (map.containsKey(msg)) {
            preTime = map.get(msg);
        }
        final long now = System.currentTimeMillis();
        if (now >= preTime + Interval) {
            if (CURR_TOAST != null) {
                CURR_TOAST.cancel();
            }
            if (context != null) {
                EbeiConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (CURR_TOAST == null) {
                            Toast toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
                            toast.show();
                            CURR_TOAST = toast;
                        } else {
                            CURR_TOAST.setText(msg);
                            CURR_TOAST.show();
                        }
                        map.put(msg, now);
                    }
                });

            }
        }
    }

    // 设置recyclerview的回弹阻尼效果
//    public static void reboundDamping(RecyclerView recyclerView) {
//        new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView),
//                1f, // VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD --> Default is 3
//                VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
//                VerticalOverScrollBounceEffectDecorator.DEFAULT_DECELERATE_FACTOR);
//    }

}
