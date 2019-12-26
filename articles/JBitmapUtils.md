```
package com.example.ly.bitmapdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.socks.library.KLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片工具
 * 欢迎加入轮子学习(android)交流群 群名称：轮子学习(android) 群 号：539175620
 * Created by ly on 2016/12/14.
 */

public class JBitmapUtils {

    /*
    * 旋转图片
    * @param angle
    * @param bitmap
    * @return Bitmap
    */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    /**
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        Bitmap bitmap = null;

        KLog.d("options.outWidth:" + options.outWidth);
        KLog.d("options.outHeight:" + options.outHeight);

        int wRatio = (int) Math.ceil(options.outWidth / (float) 720);
        int hRatio = (int) Math.ceil(options.outHeight / (float) 1280);

        KLog.d("options.wRatio:" + wRatio + "，hRatio:" + hRatio);


        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                options.inSampleSize = wRatio;
            } else {
                options.inSampleSize = hRatio;
            }
        }

        in = new BufferedInputStream(new FileInputStream(new File(path)));
        options.inJustDecodeBounds = false;

        // 杯具的老戳手机-1G以下的内存的某些手机无法加载高清图片大于1M以上，只能加大压缩力度
        try {
            bitmap = BitmapFactory.decodeStream(in, null, options);

            KLog.d("options.outWidth:" + options.outWidth);
            KLog.d("options.outHeight:" + options.outHeight);

        } catch (Exception e) {
            e.printStackTrace();

            wRatio = (int) Math.ceil(options.outWidth / (float) 480);
            hRatio = (int) Math.ceil(options.outHeight / (float) 800);

            if (wRatio > 1 && hRatio > 1) {
                if (wRatio > hRatio) {
                    options.inSampleSize = wRatio;
                } else {
                    options.inSampleSize = hRatio;
                }
            }

            in = new BufferedInputStream(new FileInputStream(new File(path)));
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(in, null, options);


            KLog.d("options.outWidth:" + options.outWidth);
            KLog.d("options.outHeight:" + options.outHeight);


            // 获取图片的旋转角度
            int degree = readPictureDegree(path);
            if (degree <= 0) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                Bitmap rotaBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return rotaBitmap;
            }
        }

        return bitmap;
    }


    /**
     * 将bitmap保存到SD卡中，返回路径
     *
     * @param @param  bitmap 保存的图片
     * @param @param  folder 存入的文件夹名称，注意，要写 "/"
     * @param @param  picName 要存入的图片命名
     * @param @return
     * @return String 返回图片在sd卡中的路径
     * @throws @date [2015年10月14日 下午7:37:46]
     * @author 李吉
     * @Title: saveBitmapToSDCard
     */
    public static String saveBitmap2File(Bitmap bitmap, String filePath) {
        File targetFile = new File(filePath);
        try {
            if (targetFile.exists()) {
                targetFile.delete();
            } else {
                targetFile.mkdirs();
            }
            BufferedOutputStream bos;
            bos = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 100表示不进行压缩，70表示压缩率为30%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath;

    }


}

```
