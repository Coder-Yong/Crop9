package cn.singull.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

public class BlurUtils {

	public static Bitmap Blur(Context context ,Bitmap b ,float count) {
		if(b == null){
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(),
				Config.ARGB_8888);
		// 初始化Renderscript
		RenderScript rs = RenderScript.create(context);
		// 创建高斯模糊对象
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));
		// 创建Allocations，此类是将数据传递给RenderScript内核的主要方法，并制定一个后备类型存储给定类型
		Allocation allIn = Allocation.createFromBitmap(rs, b);
		Allocation allOut = Allocation.createFromBitmap(rs, bitmap);
		// 设定模糊度
		blurScript.setRadius(count);

		// Perform the Renderscript
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// Copy the final bitmap created by the out Allocation to the outBitmap
		allOut.copyTo(bitmap);

		// After finishing everything, we destroy the Renderscript.
		rs.destroy();
		return bitmap;
	}

}
