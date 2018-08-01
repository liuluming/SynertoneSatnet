package synertone.com.satnet.view;


import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import synertone.com.satnet.R;

public  class ToastUtil {

	private static Toast toast = null;
	private static TextView view;

	/**
	 * 请求提示 toast
	 * @param msg
	 */
	public static void doToast(String msg) {
		try {
			if (toast == null || view == null) {
				toast = new Toast(x.app());
				view = new TextView(x.app());
				int left = DensityUtil.dip2px(13);
				int top = DensityUtil.dip2px(8);
				int right = left;
				int bottom = top;
				view.setPadding(left, top, right, bottom);
				view.setBackgroundResource(R.drawable.shape_corner_toast_black_background);
				view.setTextColor(Color.parseColor("#ffffff"));
				view.setTextSize(16);
			}
			view.setText(msg);
			int yOffset = DensityUtil.dip2px(75);
			toast.setGravity(Gravity.BOTTOM, 0, yOffset);
			toast.setView(view);
			toast.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
