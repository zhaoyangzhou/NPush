package com.push.m.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

// 建议加上注解， 混淆后表名不受影响
@Table(name = "friends")
public class Friends extends EntityBase {
	
	/**String deviceId: 设备ID*/
	@Column(column = "deviceId")
	private String deviceId;
	/**String userName: 好友姓名*/
	@Column(column = "userName")
	private String userName;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
    public String toString() {
        return "Friends{" +
                "id=" + getId() +
                ", deviceId='" + deviceId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
