package com.push.m;

import android.app.Application;
import android.util.Log;

import com.push.m.chat.IMUtil;

public class AppApplication extends Application {
	private final String TAG = AppApplication.class.getName();

	@Override
	public void onCreate() {
		Log.d(TAG, " Application onCreate invoked...");
		IMUtil.init(this);
		initNotification();
	}

	/**
	 * 设置通知效果样式
	 */
	private void initNotification() {
		new NotificationBuilder(this);
	}
}
