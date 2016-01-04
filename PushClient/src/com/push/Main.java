package com.push;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.push.m.push.PushClient;
import com.push.m.push.NativeHttpClient.HttpClientCallback;
import com.push.m.push.model.Message;

public class Main {
	public static void main(String args[]) {
		//初始化
		PushClient client = new PushClient("1", 3000);
		//查询所有已注册的标签
		client.findAllTags(new HttpClientCallback() {

			@Override
			public void complete(int code, String json) {
				if (code == 200) {
					Gson gson = new Gson();
					List<Map> map = gson.fromJson(json, List.class);
					System.out.println("--------------------------------------");
					System.out.println("Response content: " + json);
					System.out.println("--------------------------------------");
				}
			}
			
		});
		
		//推送消息
		Map extras = new HashMap();
		extras.put("id", "1");
		
		Message msg = new Message();
		msg.setTitle("通知");
		msg.setMsgContent("测试 ");
		msg.setTag("teacher");
		msg.setExtras(extras);
		//msg.setExtras(extras);
		client.sendPush(msg, new HttpClientCallback() {

			@Override
			public void complete(int code, String json) {
				if (code == 200) {
					System.out.println(json);
				}
			}
			
		});
	}
}
