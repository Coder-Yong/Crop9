package cn.singull.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
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
    private static int getStatusBarHeight(Activity act){
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
    /**
     * 获取虚拟按键高度
     * @param act Activity
     * @return
     */
    private static int getStatusBtnHeight(Activity act){
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.bottom;
    }
}
