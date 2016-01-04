package com.push.m.model;

public class AliasDevice implements java.io.Serializable {
	/**long serialVersionUID: TODO属性声明*/
	private static final long serialVersionUID = 6819688662171203746L;
	private String deviceId;
	private String aliasName;
	private String userName;
	private String online;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	
}
