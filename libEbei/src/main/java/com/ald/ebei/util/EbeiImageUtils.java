package com.ald.ebei.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.ald.ebei.R;
import com.bumptech.glide.Glide;
import com.ald.ebei.ui.EbeiGlideRoundTransform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/3/15 23:44
 * 描述：
 * 修订历史：
 */
public class EbeiImageUtils {

    /**
     * 通过传入图片的文件路径,获取压缩文件(用于本地图片上传前压缩时使用,可直接调用)
     */
    public static File getCompressFileFromResourc(String imgFilePath) {
        File compressFile = EbeiFileUtils.getImageCacheFile();
        Bitmap bitmap = rotateAndCompressBitmapFromResource(imgFilePath, 480, 800);
        if (bitmap != null) {
            compressBitmapToFile(bitmap, compressFile, 200);
        }
        return compressFile;
    }

    /**
     * 对本地拍摄照片90度翻转纠正,对Bitmap形式的图片进行压缩
     */
    public static Bitmap rotateAndCompressBitmapFromResource(String file, int reqWidth, int reqHeight) {
        Bitmap bitmap;
        try {
            int rotation = EbeiBitmapUtils.getBitmapDegree(file);
            System.gc();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(file, options);
            options.inJustDecodeBounds = false;
            int width = options.outWidth;
            int height = options.outHeight;
            int be = 1;
            if (width > height && width > reqWidth) {
                be = (int) (width / reqWidth);
            } else if (width < height && height > reqHeight) {
                be = (int) (height / reqHeight);
            }
            if (be <= 0) {
                be = 1;
            } else {
                be = be >= 6 ? 6 : be;
            }
            //  LogUtils.i("==>EbeiImageUtils", "inSampleSize" + be);
            options.inSampleSize = be;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeFile(file, options);
            if (rotation != 0) {
                bitmap = EbeiBitmapUtils.rotateBitmapByDegree(bitmap, rotation);
            }
            //   LogUtils.i("==>EbeiImageUtils", "bitmap" + getBitmapSize(bitmap));
        } catch (Exception exception) {
            bitmap = null;
            System.gc();
        }
        return bitmap;
    }

    /**
     * 对本地拍摄照片90度翻转纠正,对Bitmap形式的图片进行压缩(用于动态发布页小图展示)
     */
    public static Bitmap getSmallCompressBitmap(String file, int reqWidth, int reqHeight) {
        Bitmap bitmap;
        try {
            int rotation = EbeiBitmapUtils.getBitmapDegree(file);
            System.gc();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(file, options);
            options.inJustDecodeBounds = false;
            int width = options.outWidth;
            int height = options.outHeight;
            int be = 1;
            if (width > height && width > reqWidth) {
                be = (int) (width / reqWidth);
            } else if (width < height && height > reqHeight) {
                be = (int) (height / reqHeight);
            }
            if (be <= 0) {
                be = 1;
            }
//            LogUtils.i("==>EbeiImageUtils", "inSampleSize" + be);
            options.inSampleSize = be;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeFile(file, options);
            if (rotation != 0) {
                bitmap = EbeiBitmapUtils.rotateBitmapByDegree(bitmap, rotation);
            }
//            LogUtils.i("==>EbeiImageUtils", "bitmap" + getBitmapSize(bitmap));
        } catch (Exception exception) {
            bitmap = null;
            System.gc();
        }
        return bitmap;
    }

    /*
     * 质量压缩:方便上传到服务器.
     */
    public static void compressBitmapToFile(Bitmap bmp, File file, int maxSize) {

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            //  LogUtils.i("==>EbeiImageUtils", "压缩前：" + baos.toByteArray().length / 1024 + "KB");
            while (baos.toByteArray().length / 1024 > maxSize) {
                baos.reset();
                options -= 5;
                if (options < 0) {
                    options = 0;
                }
                bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
                if (options == 0) {
                    break;
                }
            }
            // LogUtils.i("==>EbeiImageUtils", "压缩后：" + baos.toByteArray().length / 1024 + "KB" + "options" + options);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
                System.gc();
            }
        }
    }

    /*
     * 该方法是对内存中的Bitmap进行质量上的压缩
     */
    private Bitmap compressBmpFromBmp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            if (options < 0) {
                options = 0;
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            if (options == 0) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    // 获取bitmap大小(供测试使用)
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {  //API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();  //earlier version
    }

    /**
     * 为ImageView设置圆角图片
     * @param imageView ImageView
     * @param roundImageUrl 图片地址
     * @param defaultImage 默认图片
     * @param roundImageRadius 角度
     */
    public static void setRoundImage(final ImageView imageView, String roundImageUrl, Drawable defaultImage, int roundImageRadius) {
        if (null == defaultImage) {
            defaultImage = ContextCompat.getDrawable(imageView.getContext(), R.drawable.fw__default_picture);
        }

        EbeiGlideRoundTransform transform = new EbeiGlideRoundTransform(imageView.getContext(), roundImageRadius);
        Glide.with(imageView.getContext()).load(roundImageUrl)
                //.transform(new CenterCrop(imageView.getContext()), transform)
                .transform(transform)
                .crossFade()
                .placeholder(defaultImage)
                .error(defaultImage)
                .into(imageView);
    }
}
