package com.push.m.chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.push.m.Constants;

/**
 * 消息处理器
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		/*
		 * 使用ObjectDecoder和ObjectEncoder
		 * 因为双向都有写数据和读数据，所以这里需要两个都设置
		 * 如果只读，那么只需要ObjectDecoder即可
		 */
		pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
		pipeline.addLast("encoder", new ObjectEncoder());
		
		/*
		 * 这里只监听读操作
		 * 可以根据需求，监听写操作和总得操作
		 */
		pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, Constants.ALL_IDLE_TIME, TimeUnit.SECONDS));
		
		pipeline.addLast("handler", new ServerHandler());
	}
}
