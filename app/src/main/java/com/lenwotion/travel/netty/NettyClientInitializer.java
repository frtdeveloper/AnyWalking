package com.lenwotion.travel.netty;

import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * netty 客户端初始化
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

//    /**
//     * 读超时
//     */
//    private static final int READ_TIME_OUT = 5;
//    /**
//     * 写超时
//     */
//    private static final int WRITE_TIME_OUT = 5;
//    /**
//     * 所有超时
//     */
//    private static final int ALL_TIME_OUT = 5;

    private INettyMessageCallback mCallback;

    public NettyClientInitializer(INettyMessageCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // 这个地方的 必须和服务端对应上。否则无法正常解码和编码
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(4 * 1024, Delimiters.lineDelimiter()));

        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("decoder", new StringDecoder());

//        // 空闲状态handler
//        pipeline.addLast("idleStateHandler", new IdleStateHandler(READ_TIME_OUT, WRITE_TIME_OUT, ALL_TIME_OUT, TimeUnit.SECONDS));

        // 客户端的逻辑
        pipeline.addLast("handler", new NettyClientStringHandler(mCallback));
    }

}
