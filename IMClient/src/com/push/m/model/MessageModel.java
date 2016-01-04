package com.push.m.model;

import java.io.Serializable;

public class MessageModel<T> implements Serializable {
	
	/**long serialVersionUID: TODO属性声明*/
	private static final long serialVersionUID = 3488193011088722396L;
	/**MessageType msgType: 消息类型-text(文本类型即时消息)/audio(音频类型即时消息)/pushText(文本类型推送消息)*/
	private MessageType msgType;
	/**T data: 消息内容*/
	private T data;

	public MessageType getMsgType() {
		return msgType;
	}

	public void setMsgType(MessageType msgType) {
		this.msgType = msgType;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
