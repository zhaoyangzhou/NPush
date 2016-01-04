package com.push.m.receiver;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.push.m.Constants;
import com.push.m.NotificationBuilder;
import com.push.m.activity.ChatActivity;
import com.push.m.activity.PushMessageDetailActivity;
import com.push.m.chat.IMUtil;
import com.push.m.chat.IMUtil.IMDownloadCallback;
import com.push.m.model.AliasDevice;
import com.push.m.model.AudioMessage;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.model.PushMessage;
import com.push.m.model.TextMessage;

/**
 * <p>Class       : com.push.m.receiver.IMReceiver
 * <p>Description: 接收推送消息
 *
 */
public class IMReceiver extends BroadcastReceiver {
	private final String TAG = "IMMessage-IMReceiver";
	/**boolean IS_BACKGROUND: ChatActivity是否切换到后台运行，如果是则在通知栏显示提醒，并在IMReceiver中记录消息*/
	public static boolean IS_BACKGROUND = true;
	/**boolean CHAT_VIEW_VISIBLE: ChatActivity是否可见*/
	public static boolean CHAT_VIEW_VISIBLE = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Constants.MESSAGE_RECEIVE_ACTION) && IS_BACKGROUND) {
	        MessageModel model = (MessageModel)intent.getSerializableExtra("model");
	        
	        if(model.getMsgType().equals(MessageType.TEXT)) {//文本类型即时消息
	        	TextMessage message = (TextMessage)model.getData();
				message.setDirect(MessageType.RECV);
	        	String textMsg = message.getTextMsg();
	        	Map extras = new HashMap();
	        	extras.put("friendName", message.getSenderName());
	        	extras.put("friendDeviceId", message.getSendDeviceId());
		        NotificationBuilder.alert("通知", textMsg, extras, ChatActivity.class);
		        if(!CHAT_VIEW_VISIBLE) {
		        	IMUtil.saveChat(message, Constants.MSG_STATUS_UNREAD);
		        }
	        } else if(model.getMsgType().equals(MessageType.AUDIO)) {//音频类型即时消息
	        	final AudioMessage message = (AudioMessage)model.getData();
				message.setDirect(MessageType.RECV);
	        	IMUtil.download(message.getFileUrl(), new IMDownloadCallback() {
					
					@Override
					public void cbSuccess(String filePath) {
						Map extras = new HashMap();
			        	extras.put("friendName", message.getSenderName());
			        	extras.put("friendDeviceId", message.getSendDeviceId());
				        NotificationBuilder.alert("通知", "您有一条语音消息", extras, ChatActivity.class);
				        message.setFileUrl(filePath);
				        if(!CHAT_VIEW_VISIBLE) {
				        	IMUtil.saveChat(message, Constants.MSG_STATUS_UNREAD);
				        }
					}
				});
	        } else if(model.getMsgType().equals(MessageType.PUSHTEXT)) {//文本类型推送消息
	        	final PushMessage message = (PushMessage)model.getData();
	        	String title = message.getTitle();
	        	String msgContent = message.getMsgContent();
	        	String extras = message.getExtras();
	        	Map extrasMap = IMUtil.jsonToMap(extras);
		        NotificationBuilder.alert(title, msgContent, extrasMap, PushMessageDetailActivity.class);
	        }
		}
	}
}
