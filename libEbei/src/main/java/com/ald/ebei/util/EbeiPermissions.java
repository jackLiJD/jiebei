package com.ald.ebei.util;

import android.Manifest;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/3/30 10:19
 * 描述：
 * 修订历史：
 */
public class EbeiPermissions {
    /**
     * 需要进行检测的定位权限数组
     */
    public static String[] locationPermissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE};


    /**
     * 需要进行检测的依图权限数组
     */
    public static String[] yituPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

    /**
     * 需要进行检测的faceID权限数组
     */
    public static String[] facePermissions = {android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
}
