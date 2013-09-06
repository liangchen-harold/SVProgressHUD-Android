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
				show(context, static_waitLoadingView);
				Log.i("SVProgressHUD", "show2");
			}
			catch (Exception e1)
			{
				Log.i("SVProgressHUD", "show failed");
			}
		}
	}
	public static void dismiss(Context context)
	{
		try
		{
			hide(context, static_waitLoadingView);
			Log.i("SVProgressHUD", "hide2");
		}
		catch (Exception e1)
		{
			Log.i("SVProgressHUD", "hide failed");
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

	private static void show(Context context, View v)
	{
		WindowManager.LayoutParams params;
		{
			params = new WindowManager.LayoutParams();
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			params.format = PixelFormat.TRANSLUCENT;
			params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
			params.setTitle("Toast");
		}
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (v.getParent() != null) {
			wm.removeView(v);
		}
		wm.addView(v, params);
	}
	private static void hide(Context context, View v)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (v.getParent() != null) {
			wm.removeView(v);
		}
	}
}
