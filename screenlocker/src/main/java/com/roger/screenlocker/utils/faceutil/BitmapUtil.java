package com.roger.screenlocker.utils.faceutil;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    private static Bitmap bp;


    /**
     * base64转为bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public static String bitmaptoString(Bitmap bitmap) {

        // 将Bitmap转换成字符串

        String string = null;

        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        bitmap.compress(CompressFormat.JPEG, 40, bStream);

        byte[] bytes = bStream.toByteArray();

        string = Base64.encodeToString(bytes, Base64.DEFAULT);

        return string;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round(
                    (float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 360, 600);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


    public static Bitmap saveBitmap(String imgpath, File photoFile) {
        Bitmap bitmap = getSmallBitmap(imgpath);
        Bitmap rotateBitMap = rotateBitMap(bitmap, imgpath);
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            rotateBitMap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rotateBitMap;
    }


    /** 保存方法 */
    public static void onlySaveBitmap(String path, Bitmap bm) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static Bitmap rotateBitMap(Bitmap bp, String imgpath) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信�?
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            bp = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight(), m,
                    true);
        }
        return bp;
    }


    private Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 >
                1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50,
                    baos);// 这里压缩50%，把压缩后的数据存放到baos�?
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(
                baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // �?��读入图片，此时把options.inJustDecodeBounds 设回true�?
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，�?��高和宽我们设置为
        float hh = 800f;// 这里设置高度�?00f
        float ww = 480f;// 这里设置宽度�?80f
        // 缩放比�?由于是固定比例缩放，只用高或者宽其中�?��数据进行计算即可
        int be = 1;// be=1表示不缩�?
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩�?
            be = (int) (newOpts.outWidth / ww);
        }
        else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩�?
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false�?
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压�?
    }


    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100,
                baos);// 质量压缩方法，这�?00表示不压缩，把压缩后的数据存放到baos�?
        int options = 100;
        while (baos.toByteArray().length / 1024 >
                100) { // 循环判断如果压缩后图片是否大�?00kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options,
                    baos);// 这里压缩options%，把压缩后的数据存放到baos�?
            options -= 10;// 每次都减�?0
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(
                baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream�?
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null,
                null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
