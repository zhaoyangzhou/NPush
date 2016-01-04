package com.push.m.model;


public class PushMessage implements java.io.Serializable {
	/**long serialVersionUID: TODO属性声明*/
	private static final long serialVersionUID = 771119611405361170L;
	/**String title: 通知标题*/
	private String title;
	/**String msgContent: 通知内容*/
	private String msgContent;
	/**String extras: 附加字段*/
	private String extras;
	/**String recvDeviceId: 接收设备ID */
	private String recvDeviceId;
	/**long time: 消息发送时间*/
	private long time;
	/**String msgId: 消息状态 0未发送 1已发送*/
	private String msgStatus;
	
	/**int timeToLive: 离线消息保存时间*/
	private long timeToLive = 172800000;
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
	public String getExtras() {
		return extras;
	}
	public void setExtras(String extras) {
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
	public String getRecvDeviceId() {
		return recvDeviceId;
	}
	public void setRecvDeviceId(String recvDeviceId) {
		this.recvDeviceId = recvDeviceId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getMsgStatus() {
		return msgStatus;
	}
	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}
	
}