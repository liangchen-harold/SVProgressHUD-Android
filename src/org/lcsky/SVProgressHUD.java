package org.lcsky;


import org.lcsky.svprogresshud.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.lang.reflect.Method;



public class SVProgressHUD {
	static View static_waitLoadingView = null;
	static Toast static_toast = null;
	static Handler static_handler = null;

	public static void showInViewWithoutIndicator(final Context context, String text, float delay)
	{
		if (static_handler != null)
		{
			dismiss(context);
		}
		static_handler = new Handler();
		static_handler.postDelayed(new Runnable() {
			@Override
			public void run()
			{
				dismiss(context);
			}
		}, (long)(delay*1000));

		showInView(context, text, false);
	}
	public static void showInView(Context context, String text, boolean networkIndicator)
	{
		if (static_waitLoadingView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			static_waitLoadingView = inflater.inflate(R.layout.svprogresshud, null);
		}
		TextView textView = (TextView) static_waitLoadingView.findViewById(R.id.waitloadingtextview);
		textView.setText(text);
		if (!networkIndicator)
		{
			ProgressBar progressBar1 = (ProgressBar) static_waitLoadingView.findViewById(R.id.progressBar1);
			progressBar1.setVisibility(View.INVISIBLE);
			LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) progressBar1.getLayoutParams();
			params.height = 0;
			progressBar1.setLayoutParams(params);
		}
		else
		{
			ProgressBar progressBar1 = (ProgressBar) static_waitLoadingView.findViewById(R.id.progressBar1);
			progressBar1.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) progressBar1.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			progressBar1.setLayoutParams(params);
		}

		//显示
		{
			try
			{
				showByHackToast(context, static_waitLoadingView);
				Log.i("SVProgressHUD", "show1");
			}
			catch (Exception e)
			{
				try
				{
					showByUsingNormalMethod(context, static_waitLoadingView);
					Log.i("SVProgressHUD", "show2");
				}
				catch (Exception e1)
				{
					Log.i("SVProgressHUD", "show failed");
				}
			}
		}
	}
	public static void dismiss(Context context)
	{
		try
		{
			hideByHackToast(context, static_waitLoadingView);
			Log.i("SVProgressHUD", "hide1");
		}
		catch (Exception e)
		{
			try
			{
				hideByUsingNormalMethod(context, static_waitLoadingView);
				Log.i("SVProgressHUD", "hide2");
			}
			catch (Exception e1)
			{
				Log.i("SVProgressHUD", "hide failed");
			}
		}
		//static变量要及时置null
		static_waitLoadingView = null;
		static_toast = null;
		if (static_handler != null)
		{
			static_handler.removeCallbacksAndMessages(null);
			static_handler = null;
		}
	}

	private static void showByHackToast(Context context, View v) throws Exception
	{
		if (static_toast == null) {
			static_toast = new Toast(context);
			static_toast.setView(v);
		}
		//  从Toast对象中获得mTN变量
		Field field = static_toast.getClass().getDeclaredField("mTN");
		field.setAccessible(true);
		Object obj = field.get(static_toast);
		try {
			Field mNextViewFor_4_1 =  obj.getClass().getDeclaredField("mNextView");
			if (mNextViewFor_4_1 != null) {
				mNextViewFor_4_1.setAccessible(true);
				mNextViewFor_4_1.set(obj, v);
			}
		} catch (Exception e) {
		}
		// TN对象中获得了show方法
		Method method =  obj.getClass().getDeclaredMethod("show", null);
		// 调用show方法来显示Toast信息提示框
		method.invoke(obj, null);
	}
	private static void hideByHackToast(Context context, View v) throws Exception
	{
		if (static_toast != null) {
			//  从Toast对象中获得mTN变量
			Field field = static_toast.getClass().getDeclaredField("mTN");
			field.setAccessible(true);
			Object obj = field.get(static_toast);
			// TN对象中获得了show方法
			Method method =  obj.getClass().getDeclaredMethod("hide", null);
			// 调用show方法来显示Toast信息提示框
			method.invoke(obj, null);
		}
	}
	private static void showByUsingNormalMethod(Context context, View v)
	{
		WindowManager.LayoutParams params;
		{
			params = new WindowManager.LayoutParams();
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			params.format = PixelFormat.TRANSLUCENT;
			//mParams.windowAnimations = android.R.style.Animation_Toast;
			params.type = WindowManager.LayoutParams.TYPE_TOAST;
			params.setTitle("Toast");
		}
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (v.getParent() != null) {
			wm.removeView(v);
		}
		wm.addView(v, params);
	}
	private static void hideByUsingNormalMethod(Context context, View v)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (v.getParent() != null) {
			wm.removeView(v);
		}
	}
}
