package com.push.m;

import android.os.Environment;

public class Constants {
	// 接收数据超时时间302秒
	public static final int READ_IDLE_TIME = 302;
	//发送数据超时时间302秒
	public static final int WRITE_IDLE_TIME = 302;
	// 总超时时间300秒
	public static final int ALL_IDLE_TIME = 300;
	// 隔30秒后重连
	public static final int RE_CONN_WAIT_SECONDS = 30;
	//PushService Action
	public static final String PUSH_SERVICE_ACTION = "com.push.android.intent.PushService";
	//IMMessage Receiver Action
	public static final String MESSAGE_RECEIVE_ACTION = "com.push.android.intent.MESSAGE_RECEIVED";
	//Online Status Action
	public static final String ONLINE_RECEIVE_ACTION = "com.push.android.intent.ONLINE_RECEIVED";
	
	public static final String AUDIO_DIR = Environment.getExternalStorageDirectory()
			.getPath() + "/TestRecord/";
	
	public static final String IM_IP = "192.168.0.186";
	
	public static final int IM_PORT = 1666;
	
	public static final String SERVER = "http://192.168.0.186:8088/";

	/**int MSG_STATUS_SENDING: 发送中*/
	public static final int MSG_STATUS_SENDING = 0;
	/**int MSG_STATUS_SUCCESS: 发送成功*/
	public static final int MSG_STATUS_SUCCESS = 1;
	/**int MSG_STATUS_FAIL: 发送失败*/
	public static final int MSG_STATUS_FAIL = -1;
	/**int MSG_STATUS_UNREAD: 未读*/
	public static final int MSG_STATUS_UNREAD = 2;
	/**int MSG_STATUS_READ: 已读*/
	public static final int MSG_STATUS_READ = 3;
	
}
