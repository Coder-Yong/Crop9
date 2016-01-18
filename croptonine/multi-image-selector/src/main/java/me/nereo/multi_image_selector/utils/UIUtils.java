package me.nereo.multi_image_selector.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * Created by xinou03 on 2016/1/6 0006.
 */
public class UIUtils {
    /**
     * toast
     * @param context 上下文
     * @param str 内容
     */
    public static void toast(Context context,String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
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
     * @param act Activity
     * @return
     */
    public static int getStatusBarHeight(Activity act){
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
    /**
     * 获取虚拟按键高度
     * @param act Activity
     * @return
     */
    public static int getStatusBtnHeight(Activity act){
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.bottom;
    }

    /**
     * 获取屏幕尺寸
     * @param context 上下文
     * @param type 传“width”或“height”
     * @return
     */
    public static int getScreenData( Context context,String type){
        DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
        if(type.equals("height")){
            return dm2.heightPixels;
        }else if (type.equals("width")){
            return dm2.widthPixels;
        }else{
            return 0;
        }
    }
}
