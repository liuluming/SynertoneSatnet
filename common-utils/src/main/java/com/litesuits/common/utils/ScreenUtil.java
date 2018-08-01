package com.litesuits.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import java.lang.reflect.Field;

public class ScreenUtil {
	public static float getWidth(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Activity activity = (Activity) context;
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	public static float getHight(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Activity activity = (Activity) context;
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public static float getRealHight(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Activity activity = (Activity) context;
		if (Build.VERSION.SDK_INT >= 17) {
			activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
		} else {
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		}
		return dm.heightPixels;
	}

	/**
	 * 得到状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
	// 获取指定Activity的截屏
	public  static Bitmap takeScreenShot(Activity activity){
		//View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		if (view == null) {
			return null;
		}
		Bitmap screenshot;
		screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
		Canvas c = new Canvas(screenshot);
		c.translate(-view.getScrollX(), -view.getScrollY());
		view.draw(c);

		return screenshot;
	}
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
