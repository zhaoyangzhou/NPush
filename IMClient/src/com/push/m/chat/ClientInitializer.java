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

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
	 
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
 
		pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
        pipeline.addLast("encoder", new ObjectEncoder());
 
        pipeline.addLast("idleStateHandler", new IdleStateHandler(Constants.READ_IDLE_TIME, 0, Constants.ALL_IDLE_TIME, TimeUnit.SECONDS));
        // 客户端的逻辑
        pipeline.addLast("handler", new ClientHandler());
    }
}
