package com.ald.ebei.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.ald.ebei.config.EbeiConfig;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/3
 * 描述：单位转换类
 * 修订历史：
 */
@SuppressWarnings("unused")
public class EbeiDensityUtils {
    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 把dip转换为px单位
     **/
    public static int getPxByDip(int dip) {
        DisplayMetrics dm = EbeiConfig.getContext().getResources().getDisplayMetrics();
        return (int) (dip * dm.density + 0.5f);
    }

    /**
     * 把dip转换为px单位
     */
    public static float getPxByDip(float dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return dp * dm.density;
    }

    /**
     * 把dip转换为px单位
     **/
    public static int getPxBySp(int sp) {
        DisplayMetrics dm = EbeiConfig.getContext().getResources().getDisplayMetrics();
        return (int) (sp * dm.scaledDensity);
    }

    /**
     * 获取屏幕宽度
     */
    public static float getWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属性
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 窗口的宽度
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static float getHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属性
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 窗口的高度
        return dm.heightPixels;
    }

    /**
     * 获取手机屏幕相关参数
     */
    public static Rect getAppRect(Activity activity) {
        Rect appRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(appRect); // 应用区域
        return appRect;
    }

    public static float getHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

}
