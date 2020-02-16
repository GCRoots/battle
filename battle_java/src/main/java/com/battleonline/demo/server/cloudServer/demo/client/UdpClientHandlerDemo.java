package com.battleonline.demo.server.cloudServer.demo.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * @author shipengfei
 * @data 2020/2/14
 */
public class UdpClientHandlerDemo extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String result = datagramPacket.content().toString(CharsetUtil.UTF_8);
        System.out.println("client>>>" + result);
        channelHandlerContext.close();
    }
}
