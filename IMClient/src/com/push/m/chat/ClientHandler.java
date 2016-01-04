package com.push.m.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import android.app.AliasActivity;
import android.content.Intent;
import android.util.Log;

import com.push.m.Constants;
import com.push.m.model.AliasDevice;
import com.push.m.model.AudioMessage;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.model.PushMessage;
import com.push.m.model.TextMessage;
import com.push.m.service.PushService;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
	
	private final static String TAG = "IMMessage-ClientHandler";
	 
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String data)
            throws Exception {
    	if(data.equals("ok")) {//如果是服务端的心跳反馈包
    		Log.i(TAG, "===收到反馈包===");
		} else {
    		MessageModel model = IMUtil.fromJson((String)data, Object.class);
    		if(model.getMsgType().equals(MessageType.TEXT)) {//文本类型即时消息
    			MessageModel msgModel = (MessageModel)IMUtil.fromJson(data, TextMessage.class);
    			Intent intent = new Intent(Constants.MESSAGE_RECEIVE_ACTION);
    			intent.putExtra("model", msgModel);
    	        IMUtil.getContext().sendBroadcast(intent);
    		} else if(model.getMsgType().equals(MessageType.AUDIO)) {//语音类型即时消息
    			MessageModel msgModel = (MessageModel)IMUtil.fromJson(data, AudioMessage.class);
    			Intent intent = new Intent(Constants.MESSAGE_RECEIVE_ACTION);
    			intent.putExtra("model", msgModel);
    	        IMUtil.getContext().sendBroadcast(intent);
    		} else if(model.getMsgType().equals(MessageType.PUSHTEXT)) {//文本类型推送消息
    			MessageModel msgModel = (MessageModel)IMUtil.fromJson(data.replaceAll("\\\"", "\""), PushMessage.class);
    			Intent intent = new Intent(Constants.MESSAGE_RECEIVE_ACTION);
    			intent.putExtra("model", msgModel);
    	        IMUtil.getContext().sendBroadcast(intent);
    		} else if(model.getMsgType().equals(MessageType.ONLINE_STATUS)) {//好友上线、下线通知
    			MessageModel msgModel = (MessageModel)IMUtil.fromJson(data, AliasDevice.class);
    			Intent intent = new Intent(Constants.ONLINE_RECEIVE_ACTION);
    			intent.putExtra("model", msgModel);
    	        IMUtil.getContext().sendBroadcast(intent);
    		}
    	}
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	Log.i(TAG, "===与服务器成功建立连接===");
        super.channelActive(ctx);
        PushService.isConnSucc = true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	Log.i(TAG, "===与服务器断开连接===");
        super.channelInactive(ctx);
        PushService.isConnSucc = false;
    }
    
    @Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {//读超时
                ctx.channel().disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {//总超时
                ctx.writeAndFlush("ping");
            }/* else if (event.state() == IdleState.WRITER_IDLE) {//写超时
            	
            } */
        }
    }
}