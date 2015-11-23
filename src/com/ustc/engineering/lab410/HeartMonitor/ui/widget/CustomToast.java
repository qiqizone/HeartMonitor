package com.ustc.engineering.lab410.HeartMonitor.ui.widget;



import com.ustc.engineering.lab410.HeartMonitor.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 控制Toast显示 新的Toast到来时，取消前一个显示
 * 
 * @author gjyuan
 * 
 */
public class CustomToast {

	public static final int LENGTH_SHORT = 1000;
	public static final int LENGTH_LONG = 3000;

	private static Toast mViewToast;

	/**
	 * 
	 * @param mContext
	 * @param text
	 * @param duration
	 *            CustomToast LENGTH_SHORT 1秒<br/>
	 *            CustomToast LENGTH_LONG 3秒
	 */
	public static void showToast(Context mContext, String text, int duration) {
		if (mContext == null)
			return;
		if (duration <= 1) {
			duration = LENGTH_SHORT;
		}
		showViewToast(mContext,text,duration);

	}

	public static void showToast(Context mContext, int resId, int duration) {
		showToast(mContext, mContext.getResources().getString(resId), duration);
	}

	public static void showViewToast(Context mContext, String text,
			int duration) {
		if (mViewToast == null) {
			mViewToast = new Toast(mContext);
		}
		View view = View.inflate(mContext, R.layout.toast_layout, null);
		TextView toastText = (TextView) view.findViewById(R.id.toast_text);
		toastText.setText(text);
		mViewToast.setView(view);
		mViewToast.setDuration(duration);
		mViewToast.setGravity(Gravity.CENTER, 0, 0);
		mViewToast.show();
	}
}
