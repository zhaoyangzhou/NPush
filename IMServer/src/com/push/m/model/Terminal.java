package com.push.m.model;

import io.netty.channel.Channel;

public class Terminal implements java.io.Serializable{

	private static final long serialVersionUID = 479148324800517968L;

	/**String[] tags: 标签*/
	private String[] tags ;
	
	/**String deviceId: 设备唯一ID*/
	private String deviceId;
	
	/**String aliasName: 设备别名*/
	private String aliasName;
	
	/**boolean enablePush: 是否启用推送*/
	private boolean enablePush;
	
	/**String platform: 平台-android/ios*/
	private String platform;
	
	private Channel channel;

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	public boolean isEnablePush() {
		return enablePush;
	}

	public void setEnablePush(boolean enablePush) {
		this.enablePush = enablePush;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

}

