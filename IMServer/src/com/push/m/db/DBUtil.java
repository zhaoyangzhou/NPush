package com.push.m.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.push.m.chat.IMUtil;
import com.push.m.model.AliasDevice;
import com.push.m.model.AudioMessage;
import com.push.m.model.MessageType;
import com.push.m.model.PushMessage;
import com.push.m.model.Terminal;
import com.push.m.model.TextMessage;

public class DBUtil {
	
	/** 查找单个设备
	 * @param device
	 * @return
	 */
	public static AliasDevice findDevice(AliasDevice device) {
		Connection conn = null;
		try {
			String sql = "select device_id, alias_name, user_name, online from alias_device where alias_name=? ";
			Object[] params = new Object[]{device.getAliasName()};
			conn = DBHelper.getConnection();
			List<Map> list = DBHelper.query(conn, sql, params);
			
			for(Map map : list) {
				device.setDeviceId((String)map.get("device_id"));
				device.setAliasName((String)map.get("alias_name"));
				device.setUserName((String)map.get("user_name"));
				device.setOnline((String)map.get("online"));
	        }  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return device;
	}
	
	/** 查找所有设备
	 * @return
	 */
	public static List<Map> findAllDevice() {
		Connection conn = null;
		List<Map> list = null;
		try {
			String sql = "select * from alias_device where online='1' group by alias_name union select * from alias_device t1 where online='0' and not exists(select 1 from alias_device t2 where t1.alias_name=t2.alias_name and t2.online='1') group by alias_name ";
			conn = DBHelper.getConnection();
			list = DBHelper.query(conn, sql, null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static List<Map> findAllTags() {
		Connection conn = null;
		List<Map> list = null;
		try {
			String sql = "select distinct tag from tag_device ";
			conn = DBHelper.getConnection();
			list = DBHelper.query(conn, sql, null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/** 根据Tag查找设备
	 * @param tag
	 * @return
	 */
	public static List<Map> findDevicesByTag(String tag) {
		Connection conn = null;
		List<Map> list = null;
		try {
			String sql = "select tag, device_id, online, user_name, platform from tag_device t where tag=? ";
			conn = DBHelper.getConnection();
			list = DBHelper.query(conn, sql, new Object[]{tag});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * <p>Method ：findDevicesByAliasName
	 * <p>Description : 按别名查找设备
	 *
	 * @param aliasName
	 * @return 
	 */
	public static Map findDevicesByAliasName(String aliasName) {
		Connection conn = null;
		Map map = null;
		try {
			String sql = "select device_id, alias_name, user_name, online, platform from alias_device t where alias_name=? ";
			conn = DBHelper.getConnection();
			List<Map> list = DBHelper.query(conn, sql, new Object[]{aliasName});
			if(list.size() > 0) {
				map = list.get(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/** 保存或修改设备信息
	 * @param terminal
	 */
	public static void saveOrUpdateDevice(Terminal terminal) {
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			String[] tags = terminal.getTags();
			String deviceId = terminal.getDeviceId();
			String aliasName = terminal.getAliasName();
			boolean online = terminal.isEnablePush();
			String platform = terminal.getPlatform();
			
			String querySql1 = "select uuid, tag, device_id, online from tag_device where tag=? and device_id=? ";
			String querySql2 = "select device_id, alias_name, user_name, online from alias_device where alias_name=? and device_id=? ";
			String insertSql1 = "insert into tag_device(uuid, tag, device_id, user_name, online, platform) values(?, ?, ?, ?, '1', ?) ";
			String insertSql2 = "insert into alias_device(device_id, alias_name, user_name, online, platform) values(?, ?, ?, '1', ?) ";
			String updSql1 = "update tag_device set online=? where tag=? and device_id=? ";
			String updSql2 = "update alias_device set alias_name=?, user_name=?, online=? where alias_name=? and device_id=? ";
			Object[] params = null;
			//将设备信息持久化到tag_device表中
			for(int i = 0, len = tags.length; i < len; i++) {
				params = new Object[]{tags[i], deviceId};
				List<Map> list = DBHelper.query(conn, querySql1, params);
				if(list.size() > 0) {//如果存在该设备和标签的注册信息，则修改上线状态，否则插入
					params = new Object[]{online ? "1" : "0", tags[i], deviceId};
					DBHelper.saveOrUpdate(conn, updSql1, params);
				} else {
					params = new Object[]{getUUID(), tags[i], deviceId, aliasName, platform};
					DBHelper.saveOrUpdate(conn, insertSql1, params);
				}
			}
			//将设备信息持久化到alias_device表中
			List<Map> list = DBHelper.query(conn, querySql2, new Object[]{aliasName, deviceId});
			if(list.size() > 0) {//如果存在该设备的注册信息，则修改否则插入
				params = new Object[]{aliasName, aliasName, online ? "1" : "0", aliasName, deviceId};
				DBHelper.saveOrUpdate(conn, updSql2, params);
			} else {
				params = new Object[]{deviceId, aliasName, aliasName, platform};
				DBHelper.saveOrUpdate(conn, insertSql2, params);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>Method ：saveOrUpdateIMMessage
	 * <p>Description : 保存推送的消息记录
	 *
	 * @param <T>
	 * @param t
	 */
	public static <T> void saveOrUpdateIMMessage(T t) {
		Connection conn = null;
		try {
			String uuid = getUUID();
			conn = DBHelper.getConnection();
			
			//查询此条消息是否已经存在
			String querySql = "select msg_id from im_message where send_device_id=? and recv_device_id=? and time=? ";
			
			if(t instanceof TextMessage) {
				TextMessage message = (TextMessage) t;
				
				Object[] params = new Object[]{message.getSendDeviceId(), message.getRecvDeviceId(), message.getTime()};
				List<Map> list = DBHelper.query(conn, querySql, params);
				if(list.size() > 0) {
					Map map = list.get(0);
					String sql = "update im_message set msg_status=? where msg_id=? ";
					DBHelper.saveOrUpdate(conn, sql, new Object[]{message.getMsgStatus(), map.get("msg_id")});
				} else {
					String sql = "insert into im_message(msg_id, send_device_id, recv_device_id, text_msg, time, msg_type, msg_status, send_alias_name, recv_alias_name) values(?, ?, ?, ?, ?, 'text', ?, ?, ?) ";
					params = new Object[]{uuid, message.getSendDeviceId(), message.getRecvDeviceId(), message.getTextMsg(), message.getTime(), message.getMsgStatus(), message.getSendAliasName(), message.getRecvAliasName()};
					DBHelper.saveOrUpdate(conn, sql, params);
				}
			} else if(t instanceof AudioMessage) {
				AudioMessage message = (AudioMessage) t;
				
				Object[] params = new Object[]{message.getSendDeviceId(), message.getRecvDeviceId(), message.getTime()};
				List<Map> list = DBHelper.query(conn, querySql, params);
				if(message.getMsgId() != null) {
					Map map = list.get(0);
					String sql = "update im_message set msg_status=? where msg_id=? ";
					DBHelper.saveOrUpdate(conn, sql, new Object[]{message.getMsgStatus(), map.get("msg_id")});
				} else {
					String sql = "insert into im_message(msg_id, send_device_id, recv_device_id, file_url, duration, time, msg_type, msg_status, send_alias_name, recv_alias_name) values(?, ?, ?, ?, ?, ?, 'audio', ?, ?, ?) ";
					params = new Object[]{uuid, message.getSendDeviceId(), message.getRecvDeviceId(), message.getFileUrl(), message.getDuration(), message.getTime(), message.getMsgStatus(), message.getSendAliasName(), message.getRecvAliasName()};
					DBHelper.saveOrUpdate(conn, sql, params);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static <T> void saveOrUpdatePushMessage(T t) {
		if(!(t instanceof PushMessage)) {
			return;
		}
		Connection conn = null;
		try {
			String uuid = getUUID();
			conn = DBHelper.getConnection();
			
			//查询此条消息是否已经存在
			String querySql = "select msg_id from push_message where recv_device_id=? and tag=? and time=? ";
			
			PushMessage message = (PushMessage) t;
			
			Object[] params = new Object[]{message.getRecvDeviceId(), message.getTag(), message.getTime()};
			List<Map> list = DBHelper.query(conn, querySql, params);
			if(list.size() > 0) {
				Map map = list.get(0);
				String sql = "update push_message set msg_status=? where msg_id=? ";
				DBHelper.saveOrUpdate(conn, sql, new Object[]{message.getMsgStatus(), map.get("msg_id")});
			} else {
				String sql = "insert into push_message(msg_id, title, msg_content, extras, recv_device_id, msg_status, time, time_to_live, tag) values(?, ?, ?, ?, ?, ?, ?, ?, ?) ";
				params = new Object[]{uuid, message.getTitle(), message.getMsgContent(), message.getExtras(), message.getRecvDeviceId(), message.getMsgStatus(), message.getTime(), message.getTimeToLive(), message.getTag()};
				DBHelper.saveOrUpdate(conn, sql, params);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static List findUnSendIMMessage(String recvDeviceId, String recvAliasName) {
		Connection conn = null;
		List list = new ArrayList();
		try {
			conn = DBHelper.getConnection();
			String query = "select im_message.msg_id, im_message.send_device_id, im_message.recv_device_id, im_message.text_msg, im_message.file_url, im_message.duration, im_message.time, im_message.msg_type, im_message.msg_status, im_message.send_alias_name, im_message.recv_alias_name, alias_device.platform, alias_device.user_name from im_message, alias_device where im_message.send_device_id=alias_device.device_id and im_message.send_alias_name=alias_device.alias_name and msg_status='0' and recv_device_id=? and recv_alias_name=?  ";
			List<Map> mapList = DBHelper.query(conn, query, new Object[]{recvDeviceId, recvAliasName});
			for(Map map : mapList) {
				//TODO: 根据platform字段值，分别对android和ios设备推送消息
				if(MessageType.valueOf(((String)map.get("msg_type")).toUpperCase()).equals(MessageType.TEXT)) {
					TextMessage message = new TextMessage();
					message.setMsgId((String)map.get("msg_id"));
					message.setSendDeviceId((String)map.get("send_device_id"));
					message.setRecvDeviceId((String)map.get("recv_device_id"));
					message.setTextMsg((String)map.get("text_msg"));
					message.setTime((Long)map.get("time"));
					message.setMsgStatus((String)map.get("msg_status"));
					message.setSenderName((String)map.get("user_name"));
					message.setSendAliasName((String)map.get("send_alias_name"));
					message.setRecvAliasName((String)map.get("recv_alias_name"));
					list.add(message);
				} else if(MessageType.valueOf(((String)map.get("msg_type")).toUpperCase()).equals(MessageType.AUDIO)) {
					AudioMessage message = new AudioMessage();
					message.setMsgId((String)map.get("msg_id"));
					message.setSendDeviceId((String)map.get("send_device_id"));
					message.setRecvDeviceId((String)map.get("recv_device_id"));
					message.setTime((Long)map.get("time"));
					message.setFileUrl((String)map.get("file_url"));
					message.setDuration((Float)map.get("duration"));
					message.setMsgStatus((String)map.get("msg_status"));
					message.setSenderName((String)map.get("user_name"));
					message.setSendAliasName((String)map.get("send_alias_name"));
					message.setRecvAliasName((String)map.get("recv_alias_name"));
					list.add(message);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static int deleteIMMessage(String tag, String startTime, String endTime) {
		int deleteCount = 0;
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			StringBuffer sqlBuffer = new StringBuffer("delete from im_message where exists (select 1 from tag_device where im_message.recv_device_id=tag_device.device_id and im_message.msg_status='0' ");
			List params = new ArrayList();
			if(tag != null && !tag.equals("")) {
				sqlBuffer.append("and tag_device.tag=? ");
				params.add(tag);
			}
			if(startTime != null && !startTime.equals("")) {
				sqlBuffer.append("and im_message.time > ? ");
				params.add(startTime);
			}
			if(endTime != null && !endTime.equals("")) {
				sqlBuffer.append("and im_message.time <= ? ");
				params.add(endTime);
			}
			sqlBuffer.append(")");
			deleteCount = DBHelper.saveOrUpdate(conn, sqlBuffer.toString(), params.toArray());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return deleteCount;
	}
	
	public static List findUnSendPushMessage(String recvDeviceId, String tag) {
		Connection conn = null;
		List list = new ArrayList();
		try {
			conn = DBHelper.getConnection();
			String query = "select push_message.msg_id, push_message.recv_device_id, push_message.title, push_message.msg_content, push_message.extras, push_message.time, push_message.msg_status, push_message.time_to_live,push_message.tag from push_message where msg_status='0' and recv_device_id=? and tag=? ";
			List<Map> mapList = DBHelper.query(conn, query, new Object[]{recvDeviceId, tag});
			for(Map map : mapList) {
				//TODO: 根据platform字段值，分别对android和ios设备推送消息
				PushMessage pushMessage = new PushMessage();
				pushMessage.setTitle((String) map.get("title"));
				pushMessage.setMsgContent((String) map.get("msg_content"));
				pushMessage.setExtras((String) map.get("extras"));
				pushMessage.setMsgStatus((String) map.get("msg_status"));
				pushMessage.setRecvDeviceId((String) map.get("recv_device_id"));
				pushMessage.setTime((Long) map.get("time"));
				pushMessage.setTimeToLive((Long) map.get("time_to_live"));
				pushMessage.setTag((String) map.get("tag"));
				list.add(pushMessage);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/** 生成UUID
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return uuid;
	}
}
