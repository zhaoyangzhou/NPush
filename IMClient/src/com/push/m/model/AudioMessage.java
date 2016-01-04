package com.push.m.model;


public class AudioMessage implements java.io.Serializable {
	/**long serialVersionUID: TODO属性声明*/
	private static final long serialVersionUID = -7053827058330577148L;
	/**String msgId: 消息ID*/
	private String msgId;
	/**String senderName: 发送者姓名*/
	private String senderName;
	/**String sendDeviceId: 发送设备ID*/
	private String sendDeviceId;
	/**String recvDeviceId: 接收设备ID*/
	private String recvDeviceId;
	/**String sendAliasName: 发送者别名，唯一*/
	private String sendAliasName;
	/**String recvAliasName: 接收者别名，唯一*/
	private String recvAliasName;
	/**String fileUrl: 文件服务端下载地址*/
	private String fileUrl;
	/**float duration: 音频时长*/
	private float duration;
	/**long time: 发送时间*/
	private long time;
	/**String msgId: 消息状态 0未发送 1已发送*/
	private String msgStatus;
	/**MessageType direct: send发送 recv接收*/
	private MessageType direct;
	
	
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getSendDeviceId() {
		return sendDeviceId;
	}
	public void setSendDeviceId(String sendDeviceId) {
		this.sendDeviceId = sendDeviceId;
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
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	public String getMsgStatus() {
		return msgStatus;
	}
	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}
	public String getSendAliasName() {
		return sendAliasName;
	}
	public void setSendAliasName(String sendAliasName) {
		this.sendAliasName = sendAliasName;
	}
	public String getRecvAliasName() {
		return recvAliasName;
	}
	public void setRecvAliasName(String recvAliasName) {
		this.recvAliasName = recvAliasName;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public MessageType getDirect() {
		return direct;
	}
	public void setDirect(MessageType direct) {
		this.direct = direct;
	}
	
}
