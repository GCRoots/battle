package com.battleonline.demo.server.localServer.messageServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * @author shipengfei
 * @data 2020/2/11
 */
public class MessageServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.err.println(datagramPacket);
        System.out.println("packet:"+datagramPacket.content().toString(CharsetUtil.UTF_8));

        String[] packets=datagramPacket.content().toString(CharsetUtil.UTF_8).split(";");

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        // We don't close the channel because we can keep serving requests.
    }
}
