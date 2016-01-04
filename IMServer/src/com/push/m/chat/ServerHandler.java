package com.push.m.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.push.m.db.DBUtil;
import com.push.m.model.AliasDevice;
import com.push.m.model.AudioMessage;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.model.PushMessage;
import com.push.m.model.Terminal;
import com.push.m.model.TextMessage;
import com.sun.org.apache.commons.beanutils.BeanUtils;

/**
 * 心跳检测
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> { 
	
	private static final Logger logger = Logger.getLogger(ServerHandler.class); 
	
	// 每个chanel对应一个线程，此处用来存储对应于每个线程的一些基础数据
	private ThreadLocal<Terminal> thread = new ThreadLocal<Terminal>(); 
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {//总超时
            	ctx.channel().close();
            }/* else if (event.state() == IdleState.WRITER_IDLE) {//写超时
            	
            } else if (event.state() == IdleState.READER_IDLE) {//读超时

            }*/
        }
    }
	
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	logger.error("错误原因：" + cause.getMessage());
    	if(thread.get()!=null){
			/*
			 * 从管理集合中移除设备号等唯一标示，标示设备离线
			 */
    		// TODO
    		for(Map map : ServerEntry.THREAD_LIST) {
				ThreadLocal<Terminal> cacheThread = (ThreadLocal<Terminal>)map.get("thread");
				if(cacheThread.equals(thread)) {
					ServerEntry.THREAD_LIST.remove(map);
					offline(map);
					break;
				}
			}
	    }
    	ctx.channel().close();
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("===终端接入===");
        super.channelActive(ctx);
    }
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 关闭，等待重连
	    ctx.close();
		if(thread.get()!=null){
			/*
			 * 从管理集合中移除设备号等唯一标示，标示设备离线
			 */
    		// TODO
			for(Map map : ServerEntry.THREAD_LIST) {
				ThreadLocal<Terminal> cacheThread = (ThreadLocal<Terminal>)map.get("thread");
				if(thread.equals(cacheThread)) {
					ServerEntry.THREAD_LIST.remove(map);
					offline(map);
					break;
				}
			}
	    }
		logger.info("===终端已断开连接===");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String data)
			throws Exception {
		if(data.equals("ping")) {//如果是客户端发来的心跳检测包
    		ctx.channel().writeAndFlush("ok");
    		logger.info("===收到检测包===");
    		return;
    	} 
		MessageModel model = IMUtil.fromJson((String)data, Object.class);
		if(model.getMsgType().equals(MessageType.CONNECT)) {//如果是连接请求
			Terminal terminal = (Terminal)IMUtil.fromJson((String)data, Terminal.class).getData();
			String[] tags = terminal.getTags();
			if(tags!=null){
				if(terminal.isEnablePush()) {//如果是启用推送 
					terminal.setChannel(ctx.channel());
					if(thread.get()==null){
						thread.set(terminal);
						/*
						 * 将设备信息和进程放入集合中进行统一管理
						 */
						Map map = new HashMap();
						map.put("thread", thread);
						map.put("terminal", terminal);
						ServerEntry.THREAD_LIST.add(map);
						online(map);
						
						/*
						 * 推送离线IM消息
						 */
						List imList  = DBUtil.findUnSendIMMessage(terminal.getDeviceId(), terminal.getAliasName());
						for(Object obj : imList) {
							if(obj instanceof TextMessage) {
								TextMessage message = (TextMessage) obj;
								MessageModel unsendMessageModel = new MessageModel();
								unsendMessageModel.setMsgType(MessageType.TEXT);
								unsendMessageModel.setData(message);
								IMUtil.forwardToOne(unsendMessageModel);
							} else if(obj instanceof AudioMessage) {
								AudioMessage message = (AudioMessage) obj;
								MessageModel unsendMessageModel = new MessageModel();
								unsendMessageModel.setMsgType(MessageType.AUDIO);
								unsendMessageModel.setData(message);
								IMUtil.forwardToOne(unsendMessageModel);
							}
						}
						
						/*
						 * 推送离线Push消息
						 */
						for(String tag : tags) {
							List pushList  = DBUtil.findUnSendPushMessage(terminal.getDeviceId(), tag);
							for(Object obj : pushList) {
								PushMessage message = (PushMessage) obj;
								IMUtil.pushMessageByTag(message);
							}
						}
					}
				} else {//如果是停止推送
					for(Map map : ServerEntry.THREAD_LIST) {
						ThreadLocal<Terminal> cacheThread = (ThreadLocal<Terminal>)map.get("thread");
						if(thread.equals(cacheThread)) {
							ServerEntry.THREAD_LIST.remove(map);
							offline(map);
							break;
						}
					}
				}
			}
		} else {//文本消息、语音消息
			MessageModel msgModel = (MessageModel)IMUtil.fromJson((String)data, Object.class);
			IMUtil.forwardToOne(msgModel);
		}
	}
	
	private void online(Map map) {
		Terminal terminal = (Terminal)map.get("terminal");
		terminal.setEnablePush(true);
		DBUtil.saveOrUpdateDevice(terminal);
		
		AliasDevice device = new AliasDevice();
		device.setAliasName(terminal.getAliasName());
		device.setDeviceId(terminal.getDeviceId());
		device.setUserName(terminal.getAliasName());
		device.setOnline("1");
		MessageModel msgModel = new MessageModel();
		msgModel.setMsgType(MessageType.ONLINE_STATUS);
		msgModel.setData(device);
		String json = IMUtil.toJson(msgModel);
		IMUtil.noticeAllDeviceOnlineStatus(json);
	}
	
	private void offline(Map map) {
		Terminal terminal = (Terminal)map.get("terminal");
		terminal.setEnablePush(false);
		DBUtil.saveOrUpdateDevice(terminal);
		
		AliasDevice device = new AliasDevice();
		device.setAliasName(terminal.getAliasName());
		device.setDeviceId(terminal.getDeviceId());
		device.setUserName(terminal.getAliasName());
		device.setOnline("0");
		MessageModel msgModel = new MessageModel();
		msgModel.setMsgType(MessageType.ONLINE_STATUS);
		msgModel.setData(device);
		String json = IMUtil.toJson(msgModel);
		IMUtil.noticeAllDeviceOnlineStatus(json);
	}
}
