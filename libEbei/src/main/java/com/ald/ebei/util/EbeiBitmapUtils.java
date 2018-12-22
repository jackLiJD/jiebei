package com.ald.ebei.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;


import com.ald.ebei.util.log.EbeiLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图像工具类，用于裁剪、放大缩小、质量调整等...
 * Created by Jacky on 2015/12/10.
 */

public class EbeiBitmapUtils {

    public static final int UNCONSTRAINED = -1;
    public static final String TAG = EbeiBitmapUtils.class.getSimpleName();
    public static final int DEFAULT_FLOAT = 20 * 1024;

    private static int mFloatBytes = DEFAULT_FLOAT;

    public static void setCompressFloatBytes(int floatBytes) {
        mFloatBytes = Math.max(floatBytes, DEFAULT_FLOAT);
    }

    /**
     * 压缩图片到指定大小<br/>
     * <b>注意：如果待压缩图片已经比设置的maxBytes小，是不会进行压缩的。</b>
     *
     * @param bitmap 待压缩图片
     * @param maxBytes 图片最大限制
     */
    public static byte[] compress(Bitmap bitmap, int maxBytes) {
        //先来一发，看看本身是不是已经很小了，很小了就不要搞那么多毛线了，否则越压越模糊，就算是100
        ByteArrayOutputStream bout = null;
        byte[] ret;
        try {
            bout = new ByteArrayOutputStream(maxBytes);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bout);
            ret = bout.toByteArray();
            EbeiLogger.e(TAG, "originSize:" + ret.length + ",max:" + maxBytes + ",float:" + mFloatBytes);
            if (ret.length < maxBytes) {
                EbeiLogger.e(TAG, "small enough ,return origin.");
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            EbeiDataUtils.close(bout);
        }
        int st = 10;
        int ed = 100;
        while (true) {
            try {
                int ct = (int) (((ed * 1.0f - st) / 2) + 0.5f) + st;
                bout = new ByteArrayOutputStream(maxBytes);
                bitmap.compress(Bitmap.CompressFormat.JPEG, ct, bout);
                ret = bout.toByteArray();
                int gap = ret.length - maxBytes;
                EbeiLogger.e(TAG, "compressed size:" + ret.length + ",center:" + ct);
                if (Math.abs(gap) <= mFloatBytes) {
                    EbeiLogger.e(TAG, "compressed OK:" + ret.length + ",center:" + ct + ",gap:" + gap);
                    return ret;
                } else {
                    if (gap > 0) {
                        ed = ct;
                        EbeiLogger.e(TAG, "too big,gap:" + gap + ",float " + mFloatBytes + ",try " + st + "-" + ed);
                    } else {
                        st = ct;
                        EbeiLogger.e(TAG, "too small,gap:" + gap + ",float " + mFloatBytes + ",try " + st + "-" + ed);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                EbeiDataUtils.close(bout);
            }
        }

    }

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 图片等比例压缩
     * @param filePath
     * @param reqWidth 期望的宽
     * @param reqHeight 期望的高
     * @return
     */
    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth,
                                             int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算InSampleSize
     * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
     * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            int ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
            // inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
            if (ratio < 3)
                inSampleSize =  ratio;
            else if (ratio < 6.5)
                inSampleSize = 4;
            else if (ratio < 8)
                inSampleSize = 8;
            else
                inSampleSize =  ratio;
        }

        return inSampleSize;
    }

    /**
     * 图片缩放到指定宽高
     *
     * 非等比例压缩，图片会被拉伸
     *
     * @param bitmap 源位图对象
     * @param w 要缩放的宽度
     * @param h 要缩放的高度
     * @return 新Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBmp;
    }



    /**
     * 读取某个路径的精确的图片，图片大小一定不会超过指定的宽度和高度，并且如果高的话，一定会精确地缩放到相应的高度或
     * 宽度，以大的那条边为准。
     *
     * @param path 路径
     * @param maxWidth 最大的宽度
     * @param maxHeight 最大的高度
     */
    public static Bitmap getBitmapExactly(String path, int maxWidth, int maxHeight) {
        System.gc();
        System.gc();
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int degree = EbeiBitmapUtils.getBitmapDegree(path);
        if (degree == 90 || degree == 270) {
            int temp = outWidth;
            outWidth = outHeight;
            outHeight = temp;
        }
        Bitmap bitmap = null;
        try {
            options.inJustDecodeBounds = false;
            double scale = 1.0;
            if (outWidth > maxWidth && outHeight > maxHeight) {
                //如果长和宽都大于指定的话，则取最大的开始缩
                scale = Math.max(outWidth * 1.0 / maxWidth, outHeight * 1.0 / maxHeight);
            } else if (outWidth > maxWidth) {
                scale = outWidth * 1.0 / maxWidth;
            } else if (outHeight > maxHeight) {
                scale = outHeight * 1.0 / maxHeight;
            }
            if (scale > 1.0) {
                int sample = 2;
                while (true) {
                    if (Math.abs(sample - scale) < 0.1 || sample > scale) {
                        break;
                    }
                    sample <<= 1;
                }
                //这里把缩放的大小放大一倍，好再来缩小
                options.inSampleSize = sample >> 1;
            }
            bitmap = BitmapFactory.decodeFile(path, options);
            if (degree != 0) {
                bitmap = rotateBitmapByDegree(bitmap, degree);
            }
            int rawWidth = bitmap.getWidth();
            int rawHeight = bitmap.getHeight();
            Matrix matrix = null;
            if (rawWidth > maxWidth && rawHeight > maxHeight) {
                matrix = new Matrix();
                float theScale = Math.max(rawWidth * 1.0f / maxWidth, rawHeight * 1.0f / maxHeight);
                theScale = 1f / theScale;
                matrix.postScale(theScale, theScale);
            } else if (rawWidth > maxWidth) {
                matrix = new Matrix();
                float theScale = rawWidth * 1.0f / maxWidth;
                theScale = 1f / theScale;
                matrix.postScale(theScale, theScale);
            } else if (rawHeight > maxHeight) {
                matrix = new Matrix();
                float theScale = rawHeight * 1.0f / maxHeight;
                theScale = 1f / theScale;
                matrix.postScale(theScale, theScale);
            }
            if (matrix != null) {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeight, matrix, true);
            }
        } catch (Throwable th) {
            th.printStackTrace();
            System.gc();
            System.gc();
            System.gc();
        }
        return bitmap;
    }

    /**
     * 返回某个路径用于显示缩略图的图片，并不要求太精确，并且如果超过最大边的时候，可能会多缩小一些，以省内存
     * @param path 路径
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     */
    public static Bitmap getBitmapThumbnail(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int degree = EbeiBitmapUtils.getBitmapDegree(path);
        if (degree == 90 || degree == 270) {
            int temp = outWidth;
            outWidth = outHeight;
            outHeight = temp;
        }
        Bitmap bitmap = null;
        try {
            options.inJustDecodeBounds = false;
            double scale = 1.0;
            if (outWidth > maxWidth && outHeight > maxHeight) {
                //如果长和宽都大于指定的话，则取最大的开始缩
                scale = Math.max(outWidth * 1.0 / maxWidth, outHeight * 1.0 / maxHeight);
            } else if (outWidth > maxWidth) {
                scale = outWidth * 1.0 / maxWidth;
            } else if (outHeight > maxHeight) {
                scale = outHeight * 1.0 / maxHeight;
            }
            if (scale > 1.0) {
                int sample = 2;
                while (true) {
                    if (Math.abs(sample - scale) < 0.1 || sample > scale) {
                        break;
                    }
                    sample <<= 1;
                }
                options.inSampleSize = sample;
            }
            bitmap = BitmapFactory.decodeFile(path, options);
            if (degree != 0) {
                bitmap = rotateBitmapByDegree(bitmap, degree);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 验证图像不要内存溢出，在操作前先缩小下
     *
     * @param path 文件路径
     * @param maxSize 一条边的最大长度
     */
    private static Bitmap getTailorBitmap(String path, int maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int size = outWidth >= outHeight ? outWidth : outHeight;
        int sampleSize = (int) Math.rint((float) size / maxSize);
        options.inSampleSize = sampleSize > 0 ? sampleSize : 1;
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError error) {
            EbeiLogger.e("ESA", "getTailorBitmap decodeFile OutOfMemoryError." + error.getMessage());
            return null;
        }
        return bitmap;
    }


    /**
     * 读取图片的旋转的角度
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
                default:
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     * @param bm 需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap bitmap = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError error) {
            EbeiLogger.e("ESA", "rotateBitmapByDegree decodeFile OutOfMemoryError." + error.getMessage());
        }
        if (bitmap == null) {
            bitmap = bm;
        }
        if (bm != bitmap) {
            bm.recycle();
        }
        return bitmap;
    }

    public static Bitmap getThumbnailBitmap(String path, int squareSize) {
        Bitmap bitmap = getTailorBitmap(path, squareSize);
        if (bitmap == null) {
            return null;
        }
        int degree = getBitmapDegree(path);
        if (degree != 0) {
            bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        return bitmap;
    }


    public static Bitmap getBitmap(String path, int maxSize) {
        Bitmap bitmap = getTailorBitmap(path, maxSize);
        if (bitmap == null) {
            return null;
        }
        int degree = getBitmapDegree(path);
        if (degree != 0) {
            bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        return bitmap;
    }

}

