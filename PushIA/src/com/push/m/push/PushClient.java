package com.push.m.push;

import java.util.HashMap;
import java.util.Map;

import com.push.m.push.NativeHttpClient.HttpClientCallback;
import com.push.m.push.model.Message;

public class PushClient {
	private String appKey = null;
	private int timeToLive = 0;
	private NativeHttpClient httpClient = null;
	
	public PushClient(String appKey, int timeToLive) {
		this.appKey = appKey;
		this.timeToLive = timeToLive;
		httpClient = new NativeHttpClient();
	}
	
	/**
	 * <p>Method ：sendPush
	 * <p>Description : 推送消息
	 *
	 * @param <T>
	 * @param msg 
	 */
	public <T> void sendPush(Message msg, HttpClientCallback callback) {
		msg.setTimeToLive(timeToLive);
		msg.setMethod("send");
		httpClient.doPost(appKey, msg, callback);
	}
	
	public <T> void cancelMessage(String cancelTag, String startTime, String endTime, HttpClientCallback callback) {
		Map map = new HashMap();
		map.put("timeToLive", timeToLive);
		map.put("cancelTag", cancelTag);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("method", "cancel");
		httpClient.doPost(appKey, map, callback);
	}
	
	public <T> void findAllTags(HttpClientCallback callback) {
		if(callback == null) {
			return;
		}
		Message msg = new Message();
		msg.setMethod("findAllTags");
		httpClient.doPost(appKey, msg, callback);
	}
	
	public <T> void findAllDevice(HttpClientCallback callback) {
		Message msg = new Message();
		msg.setMethod("findAllDevice");
		httpClient.doPost(appKey, msg, callback);
	}
}
