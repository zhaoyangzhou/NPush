package com.push.m;

import java.util.Map;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.push.m.activity.ChatActivity;
import com.push.m.R;

public class NotificationBuilder {
	private static NotificationManager manager;
	private String textMsg;
	private static int messageNum = 0;
	private static Context context;

	public NotificationBuilder(Context context) {
		this.context = context;
		manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	/**
	 * <p>Method ：alert
	 * <p>Description : 通知栏消息
	 *
	 * @param title 标题
	 * @param content 内容
	 * @param extras 附加字段
	 * @param cls 跳转到的Activity
	 */
	public static void alert(String title, String content, Map extras, Class<?> cls) {
		if(cls == null) return;
        //构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
        Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
    	Intent gotoIntent = new Intent(context, cls);
        if(extras != null) {
        	Set keySet = extras.keySet();
        	for(Object obj : keySet) {
        		String key = (String) obj;
        		gotoIntent.putExtra(key, (String)extras.get(key));
        	}
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);                                                                          
        notification.setLatestEventInfo(context, title, content, pendingIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
        notification.defaults = Notification.DEFAULT_ALL;//声音默认
        manager.notify(messageNum++, notification);//发动通知,id由自己指定，每一个Notification对应的唯一标志
	}

	public String getTextMsg() {
		return textMsg;
	}

	public void setTextMsg(String textMsg) {
		this.textMsg = textMsg;
	}

}