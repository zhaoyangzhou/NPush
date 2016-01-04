package com.push.m.push.model;

import java.util.Map;

public class Message {
	/**String title: 通知标题*/
	private String title;
	/**String msgContent: 通知内容*/
	private String msgContent;
	/**Map extras: 附加字段*/
	private Map extras;
	/**String tag: 标签 */
	private String tag;
	/**String aliasName: 别名*/
	private String aliasName;
	
	/**int timeToLive: 离线消息保存时间*/
	private long timeToLive = 0;
	/**String method: 调用的服务端方法名称*/
	private String method;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public Map getExtras() {
		return extras;
	}
	public void setExtras(Map extras) {
		this.extras = extras;
	}
	public long getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
}