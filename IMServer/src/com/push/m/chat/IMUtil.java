package com.push.m.chat;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.push.m.db.DBUtil;
import com.push.m.model.AudioMessage;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.model.PushMessage;
import com.push.m.model.Terminal;
import com.push.m.model.TextMessage;
import com.sun.org.apache.commons.beanutils.BeanUtils;

public class IMUtil {
	private static final Logger logger = Logger.getLogger(IMUtil.class); 
	
	/** 获取所有的标签
	 * @return
	 */
	public static List<Map> findAllTags() {
		List<Map> list = DBUtil.findAllTags();
		return list;
	}
	/** 获取连接的终端列表
	 * @return
	 */
	public static List<Map> findAllDevice() {
		List<Map> list = DBUtil.findAllDevice();
		return list;
	}
	/** 聊天发送给多个设备
	 * @param <T>
	 * @param t
	 * @param deviceList
	 */
	public static <T> void forwardToMany(T t, List<Map> deviceList) {
		for(Map map : deviceList) {
			MessageModel model = new MessageModel();
			if(t instanceof TextMessage) {
				TextMessage message = (TextMessage) t;
				message.setRecvDeviceId((String)map.get("device_id"));
				model.setMsgType(MessageType.TEXT);
				model.setData(message);
			} else if(t instanceof AudioMessage) {
				AudioMessage message = (AudioMessage) t;
				message.setRecvDeviceId((String)map.get("device_id"));
				model.setMsgType(MessageType.AUDIO);
				model.setData(message);
			}
			IMUtil.forwardToOne(model);
		}
	}
	
	/** 聊天发送给单个设备
	 * @param modelData
	 */
	public static void forwardToOne(MessageModel modelData) {
		if(modelData.getMsgType().equals(MessageType.TEXT)) {
			String json = IMUtil.toJson(modelData);
			final TextMessage message = (TextMessage)IMUtil.fromJson(json, TextMessage.class).getData();
			String recvDeviceId = message.getRecvDeviceId();
			String recvAliasName = message.getRecvAliasName();
			//将消息保存到消息表中，如果发送成功则修改消息状态
			message.setMsgStatus("0");
			DBUtil.saveOrUpdateIMMessage(message);
			
			for(Map map : ServerEntry.THREAD_LIST) {
				Terminal terminal = (Terminal)map.get("terminal");
				if(terminal.getDeviceId().equals(recvDeviceId) && terminal.getAliasName().equals(recvAliasName)) {
					try {
						final TextMessage syncMessage = (TextMessage)BeanUtils.cloneBean(message);
						terminal.getChannel().writeAndFlush(json).addListener(new ChannelFutureListener() {
							public void operationComplete(ChannelFuture f)
									throws Exception {
								if (f.isSuccess()) {//发送成功 
									syncMessage.setMsgStatus("1");
									DBUtil.saveOrUpdateIMMessage(syncMessage);
								} else {//发送失败  
									syncMessage.setMsgStatus("0");
									DBUtil.saveOrUpdateIMMessage(syncMessage);
								}
							}
						});
					} catch (IllegalAccessException e) {
						logger.error(e.getMessage());
					} catch (InstantiationException e) {
						logger.error(e.getMessage());
					} catch (InvocationTargetException e) {
						logger.error(e.getMessage());
					} catch (NoSuchMethodException e) {
						logger.error(e.getMessage());
					}
					break;
				}
			}
		} else if(modelData.getMsgType().equals(MessageType.AUDIO)) {
			String json = IMUtil.toJson(modelData);
			final AudioMessage message = (AudioMessage)IMUtil.fromJson(json, AudioMessage.class).getData();
			String recvDeviceId = message.getRecvDeviceId();
			String recvAliasName = message.getRecvAliasName();
			message.setMsgStatus("0");
			DBUtil.saveOrUpdateIMMessage(message);
			
			for(Map map : ServerEntry.THREAD_LIST) {
				Terminal terminal = (Terminal)map.get("terminal");
				if(terminal.getDeviceId().equals(recvDeviceId) && terminal.getAliasName().equals(recvAliasName)) {
					try {
						final AudioMessage syncMessage = (AudioMessage)BeanUtils.cloneBean(message);
						terminal.getChannel().writeAndFlush(json).addListener(new ChannelFutureListener() {
							public void operationComplete(ChannelFuture f)
									throws Exception {
								if (f.isSuccess()) {//发送成功 
									syncMessage.setMsgStatus("1");
									DBUtil.saveOrUpdateIMMessage(syncMessage);
								} else {//发送失败  
									syncMessage.setMsgStatus("0");
									DBUtil.saveOrUpdateIMMessage(syncMessage);
								}
							}
						});
					} catch (IllegalAccessException e) {
						logger.error(e.getMessage());
					} catch (InstantiationException e) {
						logger.error(e.getMessage());
					} catch (InvocationTargetException e) {
						logger.error(e.getMessage());
					} catch (NoSuchMethodException e) {
						logger.error(e.getMessage());
					}
					break;
				}
			}
		}
	}
	
	/**
	 * <p>Method ：noticeAllDeviceOnlineStatus
	 * <p>Description : 通知所有设备自己上线或下线
	 *
	 * @param json 
	 */
	public static void noticeAllDeviceOnlineStatus(String json) {
		for(Map threadMap : ServerEntry.THREAD_LIST) {
			Terminal terminal = (Terminal)threadMap.get("terminal");
			try {
				terminal.getChannel().writeAndFlush(json).addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture f)
							throws Exception {
						if (f.isSuccess()) {//发送成功 
							
						} else {//发送失败  
							
						}
					}
				});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			break;
		}
	}
	
	/**
	 * <p>Method ：pushMessageByTag
	 * <p>Description : 通过API按标签推送消息
	 *
	 * @param message
	 * @param tag 
	 */
	public static void pushMessageByTag(PushMessage message) {
		MessageModel modelData = new MessageModel();
		modelData.setMsgType(MessageType.PUSHTEXT);
		String tag = message.getTag();
		//标签下的所有设备
		List<Map> deviceList = DBUtil.findDevicesByTag(tag);
		for(Map deviceMap : deviceList) {
			String recvDeviceId = (String)deviceMap.get("device_id");
			message.setRecvDeviceId(recvDeviceId);
			//将消息保存到消息表中，如果发送成功则修改消息状态
			message.setMsgStatus("0");
			DBUtil.saveOrUpdatePushMessage(message);
			
			modelData.setData(message);
			String json = IMUtil.toJson(modelData);
			
			for(Map map : ServerEntry.THREAD_LIST) {
				Terminal terminal = (Terminal)map.get("terminal");
				if(terminal.getDeviceId().equals(recvDeviceId)) {//如果在线设备ID与目标设备ID相同
					for(String onlineTag : terminal.getTags()) {
						if(onlineTag.equals(tag)) {//如果在线设备Tag与目标设备Tag相同
							try {
								final PushMessage syncMessage = (PushMessage)BeanUtils.cloneBean(message);
								terminal.getChannel().writeAndFlush(json).addListener(new ChannelFutureListener() {
									public void operationComplete(ChannelFuture f)
											throws Exception {
										if (f.isSuccess()) {//发送成功 
											syncMessage.setMsgStatus("1");
											DBUtil.saveOrUpdatePushMessage(syncMessage);
										} else {//发送失败  
											syncMessage.setMsgStatus("0");
											DBUtil.saveOrUpdatePushMessage(syncMessage);
										}
									}
								});
							} catch (IllegalAccessException e) {
								logger.error(e.getMessage());
							} catch (InstantiationException e) {
								logger.error(e.getMessage());
							} catch (InvocationTargetException e) {
								logger.error(e.getMessage());
							} catch (NoSuchMethodException e) {
								logger.error(e.getMessage());
							}
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * <p>Method ：pushMessageByAliasName
	 * <p>Description : 通过API按别名推送消息
	 *
	 * @param message
	 * @param aliasName 
	 */
	public static void pushMessageByAliasName(PushMessage message, String aliasName) {
		//指定别名的设备
		Map deviceMap = DBUtil.findDevicesByAliasName(aliasName);
		String recvDeviceId = (String)deviceMap.get("device_id");
		message.setRecvDeviceId(recvDeviceId);
		//将消息保存到消息表中，如果发送成功则修改消息状态
		message.setMsgStatus("0");
		DBUtil.saveOrUpdatePushMessage(message);
		
		MessageModel modelData = new MessageModel();
		modelData.setMsgType(MessageType.PUSHTEXT);
		modelData.setData(message);
		String json = IMUtil.toJson(modelData);
		
		for(Map map : ServerEntry.THREAD_LIST) {
			Terminal terminal = (Terminal)map.get("terminal");
			if(terminal.getDeviceId().equals(recvDeviceId)) {
				try {
					final PushMessage syncMessage = (PushMessage)BeanUtils.cloneBean(message);
					terminal.getChannel().writeAndFlush(json).addListener(new ChannelFutureListener() {
						public void operationComplete(ChannelFuture f)
								throws Exception {
							if (f.isSuccess()) {//发送成功 
								syncMessage.setMsgStatus("1");
								DBUtil.saveOrUpdatePushMessage(syncMessage);
							} else {//发送失败  
								syncMessage.setMsgStatus("0");
								DBUtil.saveOrUpdatePushMessage(syncMessage);
							}
						}
					});
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage());
				} catch (InstantiationException e) {
					logger.error(e.getMessage());
				} catch (InvocationTargetException e) {
					logger.error(e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.error(e.getMessage());
				}
				break;
			}
		}
	}
	
	/** 撤销离线消息
	 * @param tag
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int cancelMessage(String tag, String startTime, String endTime) {
		return DBUtil.deleteIMMessage(tag, startTime, endTime);
	}
	
	public static MessageModel fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(MessageModel.class, clazz);
        return gson.fromJson(json, objectType);
    }

    public static <T> String toJson(T clazz) {
        Gson gson = new Gson();
        Type objectType = type(MessageModel.class, clazz.getClass());
        return gson.toJson(clazz, objectType);
    }

    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    public static Map jsonToMap(String json) {
    	Gson gson = new Gson();
    	Map map = gson.fromJson(json, Map.class);
    	return map;
    }
    
    public static String mapToJson(Map map) {
    	Gson gson = new Gson();
    	return gson.toJson(map);
    }
	
}
