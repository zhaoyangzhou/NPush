package com.push.m.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

// 建议加上注解， 混淆后表名不受影响
@Table(name = "chat_history")
public class ChatHistory extends EntityBase {
	
	/**long time: 接收时间和发送时间，毫秒*/
	@Column(column = "time")
	private long time;
	/**String userAliasName: 当前操作人别名,用于查询自己的聊天记录时使用*/
	@Column(column = "userAliasName")
	private String userAliasName;
	/**String recvDeviceId: 接收设备ID*/
	@Column(column = "recvDeviceId")
	private String recvDeviceId;
	/**String sendDeviceId: 发送设备ID*/
	@Column(column = "sendDeviceId")
	private String sendDeviceId;
	/**String recvAliasName: 接收设备别名*/
	@Column(column = "recvAliasName")
	private String recvAliasName;
	/**String sendAliasName: 发送设备别名*/
	@Column(column = "sendAliasName")
	private String sendAliasName;
	/**String msgType: 消息类型 text audio*/
	@Column(column = "msgType")
	private String msgType;
	/**String textMsg: 文本消息内容*/
	@Column(column = "textMsg")
	private String textMsg;
	/**String fileUrl: 语音消息文件存储路径*/
	@Column(column = "fileUrl")
	private String fileUrl;
	/**String duration: 音频文件时长*/
	@Column(column = "duration")
	private String duration;
	/**boolean msgStatus: 消息状态 -1失败 0发送中 1发送成功 2未读 3已读*/
	@Column(column = "msgStatus")
	private int msgStatus; 
	/**String direct: send发送 recv接收*/
	@Column(column = "direct")
	private String direct;

    public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getRecvDeviceId() {
		return recvDeviceId;
	}

	public void setRecvDeviceId(String recvDeviceId) {
		this.recvDeviceId = recvDeviceId;
	}

	public String getSendDeviceId() {
		return sendDeviceId;
	}

	public void setSendDeviceId(String sendDeviceId) {
		this.sendDeviceId = sendDeviceId;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getTextMsg() {
		return textMsg;
	}

	public void setTextMsg(String textMsg) {
		this.textMsg = textMsg;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getRecvAliasName() {
		return recvAliasName;
	}

	public void setRecvAliasName(String recvAliasName) {
		this.recvAliasName = recvAliasName;
	}

	public String getSendAliasName() {
		return sendAliasName;
	}

	public void setSendAliasName(String sendAliasName) {
		this.sendAliasName = sendAliasName;
	}

	public String getDirect() {
		return direct;
	}

	public void setDirect(String direct) {
		this.direct = direct;
	}

	public String getUserAliasName() {
		return userAliasName;
	}

	public void setUserAliasName(String userAliasName) {
		this.userAliasName = userAliasName;
	}

	@Override
    public String toString() {
        return "ChatHistory{" +
                "id=" + getId() +
                ", time=" + time +
                ", userAliasName=" + userAliasName +
                ", recvAliasName='" + recvAliasName + '\'' +
                ", sendAliasName='" + sendAliasName + '\'' +
                ", recvDeviceId='" + recvDeviceId + '\'' +
                ", sendDeviceId='" + sendDeviceId + '\'' +
                ", msgType='" + msgType + '\'' +
                ", textMsg='" + textMsg + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", duration='" + duration + '\'' +
                ", msgStatus=" + msgStatus +
                ", direct=" + direct +
                '}';
    }
}
