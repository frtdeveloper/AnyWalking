package com.lenwotion.travel.netty.interfaces;

/**
 * netty信息回调
 * Created by John on 2018/4/25.
 */

public interface INettyMessageCallback {

    void channelRead(String message);

    void channelActive();

    void channelInactive();

}
