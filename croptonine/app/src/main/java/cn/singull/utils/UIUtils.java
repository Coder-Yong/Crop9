package cn.singull.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

/**
 * Created by xinou03 on 2016/1/6 0006.
 */
public class UIUtils {
    /**
     * toast
     *
     * @param context 上下文
     * @param str     内容
     */
    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 复制内容到剪贴板
     *
     * @param str     复制内容
     * @param context
     */
    public static void copyString(String str, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        cmb.setText(str);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取通知栏高度
     *
     * @param act Activity
     * @return
     */
    public static int getStatusBarHeight(Activity act) {
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 获取虚拟按键高度
     *
     * @param act Activity
     * @return
     */
    public static int getStatusBtnHeight(Activity act) {
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.bottom;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文
     * @param type    传“width”或“height”
     * @return
     */
    public static int getScreenData(Context context, String type) {
        DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
        if (type.equals("height")) {
            return dm2.heightPixels;
        } else if (type.equals("width")) {
            return dm2.widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 修改图片方向,压缩
     */
    public static Bitmap setDigree(String path, int x, int y, Bitmap.Config config) {
        // TODO Auto-generated method stub
        Bitmap bitmap = bitmapSample(path, x, y ,config);
        ExifInterface exif = null;// 获取照片信息
        int digree = 0;// 相机角度
        try {
            exif = new ExifInterface(path);
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)) {
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
                    break;
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            bitmap.recycle();
            return bitmap2;
        } else {
            return bitmap;
        }
    }

    /**
     * 图片压缩
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap bitmapSample(String path, int width, int height, Bitmap.Config config) {
        int count = width > height ? height : width;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Bitmap bitmap = null;
        if (options.outWidth > count) {
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;
            options.inSampleSize = options.outWidth / count;
            Log.i("setDigree:inSample", options.inSampleSize + "");
            bitmap = BitmapFactory.decodeFile(path, options);
        } else {
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;
            bitmap = BitmapFactory.decodeFile(path, options);
        }
        return bitmap;
    }
}
