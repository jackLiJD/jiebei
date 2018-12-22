package com.ald.ebei.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.ald.ebei.config.EbeiConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jacky Yu on 2015/11/17.
 */
public class EbeiFileUtils {
    private static final long K = 1 << 10;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;
    private static final long P = T * K;

    private EbeiFileUtils() {}

    /**
     * Return the size of a directory in bytes
     */
    public static long dirSize(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return 0;
        }

        long result = 0;
        File[] fileList = dir.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            // Recursive call if it's a directory
            if (fileList[i].isDirectory()) {
                result += dirSize(fileList[i]);
            } else {
                result += fileList[i].length();
            }
        }
        return result;
    }

    public static boolean deleteDirRecursively(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }

        for (File child : dir.listFiles()) {
            if (child.isFile()) {
                child.delete();
            } else if (child.getName() != "." && child.getName() != "..") {
                deleteDirRecursively(child);
            }
        }
        return dir.delete();
    }


    public static String getFormatedFileSize(long size, int decimalCount) {
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative value");
        } else if (size < K) {
            return String.format("%dB", size);
        } else if (size < M) {
            return String.format("%." + decimalCount + "fK", size * 1.0f / K);
        } else if (size < G) {
            return String.format("%." + decimalCount + "fM", size * 1.0f / M);
        } else if (size < T) {
            return String.format("%." + decimalCount + "fG", size * 1.0f / G);
        } else if (size < P) {
            return String.format("%." + decimalCount + "fT", size * 1.0f / T);
        } else {
            return String.format("%." + decimalCount + "fP", size * 1.0f / P);
        }
    }


    /**
     * Try to return the absolute file path from the given Uri  兼容了file:///开头的 和 content://开头的情况
     *
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    //图片上传时的临时存储文件
    public static File getImageCacheFile() {
        String name = System.currentTimeMillis() + ".jpg";
        File dir = EbeiDataUtils.getCacheDirectory(EbeiConfig.getContext());
        dir.mkdirs();
        return new File(dir, name);
    }

    //从资源文件中获取json
    public static String getAssetsData(Context context, String path) {
        String result = "";
        try {
            //获取输入流
            InputStream mAssets = context.getAssets().open(path);
            //获取文件的字节数
            int lenght = mAssets.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据写入到字节数组中
            mAssets.read(buffer);
            mAssets.close();
            result = new String(buffer);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

}
