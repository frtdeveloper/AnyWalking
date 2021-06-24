package com.lenwotion.travel.netty;

import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty 客户端
 */
public class NettyClient {

    private static final String WIFI_SERVER_IP = "192.168.43.1";
    private static final int WIFI_AP_NETTY_SERVER_PORT = 10086;

    private static Channel mChannel;
    private static EventLoopGroup mWorkerGroup;
    private INettyMessageCallback mCallback;

    public NettyClient(INettyMessageCallback callback) {
        mCallback = callback;
    }

    /**
     * 开始运行netty client
     */
    public void run() {
        try {
            if (mWorkerGroup == null) {
                mWorkerGroup = new NioEventLoopGroup();
            }
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(mWorkerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new NettyClientInitializer(mCallback));

            ChannelFuture channelFuture = bootstrap.connect(WIFI_SERVER_IP, WIFI_AP_NETTY_SERVER_PORT).sync();
            mChannel = channelFuture.channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendOrder(String string) {
        if (mChannel == null) {
            return;
        }
        mChannel.writeAndFlush(string + "\n");
    }

    public static void shutdownGracefully() {
        if (mChannel != null) {
            mChannel.close();
            mChannel = null;
        }
        if (mWorkerGroup != null) {
            mWorkerGroup.shutdownGracefully();
            mWorkerGroup = null;
        }
    }

}
