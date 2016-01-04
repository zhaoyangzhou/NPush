package com.push.m.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.push.m.chat.IMUtil;
import com.push.m.service.PushService;

/**
 * <p>Class       : com.push.m.receiver.SystemReceiver
 * <p>Description: 接收系统广播，实现唤醒功能
 *
 */
public class SystemReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		boolean enablePush = IMUtil.getBooleanSharedPreferences(context, "IM", "enablePush", false);
		if(enablePush) {
			if (action.equals("android.intent.action.BOOT_COMPLETED") || action.equals("android.intent.action.USER_PRESENT")) {//手机启动 唤醒屏幕
				context.startService(new Intent(
						context.getApplicationContext(), PushService.class));
			}
		}
	}
}
