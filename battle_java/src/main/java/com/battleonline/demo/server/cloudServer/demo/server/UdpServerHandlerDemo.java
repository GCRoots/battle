package com.battleonline.demo.server.cloudServer.demo.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author shipengfei
 * @data 2020/2/14
 */
public class UdpServerHandlerDemo extends SimpleChannelInboundHandler<DatagramPacket> {

    private static String[] DIRC = {"哈哈哈哈", "呵呵呵", "嘻嘻嘻"};

    private String nextQuote() {
        int quote = ThreadLocalRandom.current().nextInt(DIRC.length);
        return DIRC[quote];
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String req = datagramPacket.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        if (req.equalsIgnoreCase("QUERY")) {
            channelHandlerContext.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("RESULT:" + nextQuote(),
                            CharsetUtil.UTF_8), datagramPacket.sender()));
        }else {
            channelHandlerContext.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("RESULT:" + "ERR",
                            CharsetUtil.UTF_8), datagramPacket.sender()));
        }
    }
}
