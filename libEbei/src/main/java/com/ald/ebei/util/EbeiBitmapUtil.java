package com.ald.ebei.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 常用方法工具类
 *
 * @author Zvezda
 */

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/5
 * 描述：Bitmap常用方法工具类
 * 修订历史：
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class EbeiBitmapUtil {
    /**
     * 工具类标签
     */
    public static final String TOOL_LOG = "TOOL_LOG";

    /**
     * 2个浮点的数据加法
     *
     * @param a
     * @param b
     * @return
     */
    public static float getAdd(float a, float b) {
        BigDecimal ab = new BigDecimal(Float.toString(a));
        BigDecimal bb = new BigDecimal(Float.toString(b));
        return ab.add(bb).floatValue();
    }

    /**
     * 2个浮点的数据减法
     *
     * @param a
     * @param b
     * @return
     */
    public static float getSub(float a, float b) {
        BigDecimal ab = new BigDecimal(Float.toString(a));
        BigDecimal bb = new BigDecimal(Float.toString(b));
        return ab.subtract(bb).floatValue();
    }

    /**
     * 2个浮点的数据加法
     *
     * @param a
     * @param b
     * @return
     */
    public static double getAdd(double a, double b) {
        BigDecimal ab = BigDecimal.valueOf(a);
        BigDecimal bb = BigDecimal.valueOf(b);
        return ab.add(bb).doubleValue();
    }

    /**
     * 2个浮点的数据减法
     *
     * @param a
     * @param b
     * @return
     */
    public static double getSub(double a, double b) {
        BigDecimal ab = BigDecimal.valueOf(a);
        BigDecimal bb = BigDecimal.valueOf(b);
        return ab.subtract(bb).doubleValue();
    }

    /**
     * 修剪浮点类型
     *
     * @param value 数值
     * @param rules 规则(如:0.00保留2位小数)
     * @return
     */
    public static String getTrim(double value, String rules) {
        DecimalFormat df = new DecimalFormat(rules);
        return df.format(value);
    }

    /**
     * 读取指定路径下的图片
     *
     * @param path
     *            图片路径
     * @return
     */
    /*
     * public static Bitmap getBitmap(String path) { return BitmapFactory.decodeFile(path); }
	 */

    /**
     * 将字节数组转化成图片
     *
     * @param dataArr
     * @return
     */
    public static Bitmap getBitmap(byte[] dataArr) {
        return BitmapFactory.decodeByteArray(dataArr, 0, dataArr.length);
    }

    /**
     * 图片转化成字节
     *
     * @param bitmap
     * @return
     */
    public static byte[] getByteArr(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获得Drawable对象
     *
     * @param bitmap
     * @return
     */
    public static Drawable getDrawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(bitmap);
    }

    /**
     * 按照比例缩放将图片的宽度缩放到新宽度
     *
     * @param bitmap
     * @param w
     * @return
     */
    public static Bitmap scaleBitmapW(Bitmap bitmap, float w) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        float scale = w / width;
        return scaleBitmap(bitmap, scale);
    }

    /**
     * 按照比例缩放将图片的高度缩放到新高度
     *
     * @param bitmap
     * @return
     */
    public static Bitmap scaleBitmapH(Bitmap bitmap, float h) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        float scale = h / height;
        return scaleBitmap(bitmap, scale);
    }

    /**
     * 按比例缩放图片
     *
     * @param bitmap
     * @param scale
     * @return
     */
    public synchronized static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    /**
     * 将图片缩放到指定大小
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public synchronized Bitmap scaleBitmap(Bitmap bitmap, float w, float h) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleW = w / width;
        float scaleH = h / height;
        matrix.postScale(scaleW, scaleH);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    /**
     * @param path
     * @param newpath
     * @return
     */
    public static String bitmap2File(String path, String newpath) {
        File file = null;
        Bitmap bitmap = getSimplifyBitmap(path);
        int degree = readPictureDegree(path);
        bitmap = rotaingImageView(degree, bitmap);
        if (null == bitmap) {
            return null;
        }
        try {
            file = new File(newpath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            int size = 100;
            if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
                size = 80;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, size, fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 旋转图片为正方向
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap getSimplifyBitmap(String path) {
        int side = 960;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int dWidth = options.outWidth / side;
        int dHeight = options.outHeight / side;
        if (dWidth < dHeight) {
            options.inSampleSize = dHeight;
        } else {
            options.inSampleSize = dWidth;
        }
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    /**
     * 压缩图片质量
     *
     * @return
     */
    public static Bitmap compressImage(String path) {
        Bitmap image = chageSizeImg(path);
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
        // return image ;
    }

    /**
     * 压缩图片质量
     *
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length > (1024 * 1024)) { //循环判断如果压缩后图片是否大于1024kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static String compressSaveImage(Bitmap image, String saveFilePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length > (800 * 1024)) { //循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        try {
            FileOutputStream fos = new FileOutputStream(saveFilePath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveFilePath;
    }

    /**
     * 改变图片大小 防止内存溢出
     *
     * @param path
     * @return
     */
    public static Bitmap chageSizeImg(String path) {
        if (path == null || path.length() < 1)
            return null;
        File file = new File(path);
        if (!file.exists())
            return null;
        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 数字越大读出的图片占用的heap越小 不然总是溢出
        if (file.length() < 20480) { // 0-20k
            opts.inSampleSize = 1;
        } else if (file.length() < 51200) { // 20-50k
            opts.inSampleSize = 2;
        } else if (file.length() < 307200) { // 50-300k
            opts.inSampleSize = 4;
        } else if (file.length() < 819200) { // 300-800k
            opts.inSampleSize = 6;
        } else if (file.length() < 1048576) { // 800-1024k
            opts.inSampleSize = 8;
        } else {
            opts.inSampleSize = 10;
        }
        System.out.println("----压缩" + opts.inSampleSize);
        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
        return resizeBmp;
    }

    /**
     * 检查是否存在SD卡
     *
     * @return
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 将raw文件下的数据库文件写到系统默认路径下
     *
     * @param context
     * @param dbID    dbID 数据库文件的ID
     * @param dbName  生成数据库文件的名字(此名字要和创建SqlHelper的名字一样)
     */
    public static void writeDB(Context context, int dbID, String dbName) {
        final String DATA_BASE_PATH = "/data/data/" + context.getPackageName() + "/databases";
        try {
            File dir = new File(DATA_BASE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String dbPath = DATA_BASE_PATH + "/" + dbName;
            if (!(new File(dbPath)).exists()) {
                InputStream is = context.getResources().openRawResource(dbID);
                FileOutputStream fos = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            Log.e(TOOL_LOG, "T Exception------------->" + e.toString());
        }
    }

    /**
     * @param bitmap 原图
     *               希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int width, int height) {
        if (null == bitmap || height <= 0 || width <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg >= width && heightOrg >= height) {
            // 压缩到一个最小长度是edgeLength的bitmap

			/*
             * int longerEdgeW = (int)(width * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg)); int longerEdgeH = (int)(height * Math.max(widthOrg, heightOrg) / Math.min(widthOrg,
			 * heightOrg)); int scaledWidth = widthOrg > heightOrg ? longerEdgeW : width; int scaledHeight = widthOrg > heightOrg ? height : longerEdgeH; Bitmap scaledBitmap;
			 * 
			 * try{ scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true); } catch(Exception e){ return null; }
			 */

            try {
                result = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                bitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }

    /**
     * 将图片放大缩小指定倍数
     *
     * @param bm
     * @param width  指定宽度
     * @param height 指定高度
     * @return
     */
    public static Bitmap scaleNovelImage(Bitmap bm, float width, float height) {
        if (bm == null)
            return bm;

        int bmpwidth = bm.getWidth();
        int bmpheight = bm.getHeight();
        if (bmpwidth <= 0 || bmpheight <= 0)
            return bm;
        float scaleW = width / bmpwidth;
        float scaleH = height / bmpheight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        try {
            Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bmpwidth, bmpheight, matrix, true);
            if (bm != null && bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 指定图片的切边，对图片进行圆角处理
     *
     * @param bitmap  需要被切圆角的图片
     * @param roundPx 要切的像素大小
     * @return
     */
    public static Bitmap fillLeftTop(Bitmap bitmap, int roundPx) {
        try {
            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
            // 然后在画板上画出一个想要的形状的区域。
            // 最后把源图片帖上。
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();

            Bitmap paintingBoard = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(paintingBoard);
            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            clipTop(canvas, paint, roundPx, width, height);
            // clipLeft(canvas,paint,roundPx,width,height);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            // 帖子图
            final Rect src = new Rect(0, 0, width, height);
            final Rect dst = src;
            canvas.drawBitmap(bitmap, src, dst, paint);
            return paintingBoard;
        } catch (Exception exp) {
            return bitmap;
        }
    }

    private static void clipLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(offset, 0, width, height);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(0, 0, offset * 2, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, 0, width - offset, height);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(width - offset * 2, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipTop(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, offset, width, height);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(0, 0, width, offset * 2);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipBottom(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, 0, width, height - offset);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(0, height - offset * 2, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipAll(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    /**
     * 高斯模糊仅支持api17以上
     *
     * @param bitmap
     * @param ctx
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap blurBitmap(Bitmap bitmap, Context ctx, int hei) {
        if (Build.VERSION.SDK_INT < 17) {
            int by = (bitmap.getHeight() - hei) / 4;
            return Bitmap.createBitmap(bitmap, 0, by, bitmap.getWidth(), hei, null, false);
            /*
             * Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), hei, EbeiConfig.ARGB_8888); return bitmap;
			 */
        }
        // Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), hei, Config.ARGB_8888);

        // Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(ctx);

        // Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        // Set the radius of the blur
        blurScript.setRadius(25.f);// 0<radius <= 25 ;

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        // recycle the original bitmap
        bitmap.recycle();

        // After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    public static Bitmap getHSMHRoundBitmap(Context ctx, Bitmap bm, int height, int roundPx) {
        Bitmap outBitmap = null;
        int bmW = bm.getWidth();
        int bmH = bm.getHeight();
        if (height > bmH)
            height = bmH;
        // 图片截取
        try {
            outBitmap = Bitmap.createBitmap(bmW, height, Config.ARGB_8888);
        } catch (Exception e) {
            return null;
        }
        // 高斯模糊
        if (Build.VERSION.SDK_INT > 11) {
            // Let's create an empty bitmap with the same size of the bitmap we want to blur
            // Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), EbeiConfig.ARGB_8888);

            // Instantiate a new Renderscript
            RenderScript rs = RenderScript.create(ctx);

            // Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            // Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bm);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

            // Set the radius of the blur
            blurScript.setRadius(25.f);

            // Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            // Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);

            // After finishing everything, we destroy the Renderscript.
            rs.destroy();
        }
        // 圆角
        try {
            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
            // 然后在画板上画出一个想要的形状的区域。

            Canvas canvas = new Canvas(outBitmap);
            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            clipTop(canvas, paint, roundPx, bmW, height);
            // clipLeft(canvas,paint,roundPx,width,height);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            // 帖子图
            final Rect src = new Rect(0, 0, bmW, height);
            final Rect dst = src;
            canvas.drawBitmap(bm, src, dst, paint);
            return outBitmap;
        } catch (Exception exp) {

        }
        bm.recycle();
        return outBitmap;

    }

    /**
     * 获取和保存当前屏幕的截图
     *
     * @param mActivity
     * @param path      图片保存路径
     * @param fileName  图片名称
     */
    public static void getAndSaveCurrentImage(Activity mActivity, String path, String fileName) {
        // 构建Bitmap
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        // 获取屏幕
        View decorview = mActivity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        bmp = decorview.getDrawingCache();
        // 保存Bitmap
        try {
            File savePath = new File(path);
            // 文件
            String filepath = path + fileName;
            File file = new File(filepath);
            if (!savePath.exists()) {
                savePath.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
            bmp.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
        return sdcardDir.toString();
    }

    /**
     * 将drawable转换为bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转化为drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * 放大缩小图片
     **/
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    /**
     * 图片按比例大小压缩方法（根据Bitmap图片压缩）：
     *
     * @param image
     * @return
     */
    public static Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得图片，返回bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapByPath(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 将图片压缩到可提交大小
     */
    public static String compressUploadFile(String filePath) {
        return compressUploadFile(filePath, 1080, 1920);
    }

    /**
     * 将图片压缩到可提交大小
     */
    public static String compressUploadFile(String filePath, float width, float height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = height;//这里设置高度为800f
        float ww = width;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);
        File f = new File(EbeiDataUtils.getSDCardAppPath(), System.currentTimeMillis() + ".jpeg");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
        }
        return compressSaveImage(bitmap, f.getPath());//压缩好比例大小后再进行质量压缩
    }


    /**
     * 保存方法
     */
    public static String saveBitmap(Bitmap bitmap) {
        File f = new File(EbeiDataUtils.getSDCardAppPath(), System.currentTimeMillis() + ".jpeg");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f.getPath();
    }

}
