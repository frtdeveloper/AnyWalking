package com.lenwotion.travel.netty;

import android.util.Log;

import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * netty string类型解析器
 */
public class NettyClientStringHandler extends SimpleChannelInboundHandler<String> {

    private INettyMessageCallback mCallback;

    public NettyClientStringHandler(INettyMessageCallback callback) {
        mCallback = callback;
    }

    /**
     * 接收到信息回调
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        Log.v(GlobalConstants.LOG_TAG, "channelRead0:" + msg);
        mCallback.channelRead(msg);
    }

    /**
     * 刚连接上回调
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.v(GlobalConstants.LOG_TAG, "channelActive");
        mCallback.channelActive();
        super.channelActive(ctx);
    }

    /**
     * 断开连接回调
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.v(GlobalConstants.LOG_TAG, "channelInactive");
        mCallback.channelInactive();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Log.v(GlobalConstants.LOG_TAG, "exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

}
