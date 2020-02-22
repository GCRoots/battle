package com.battleonline.demo.server.local.localServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author shipengfei
 * @data 2020/2/22
 */
public class LocalServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    //云端服务器ip、端口
    private String IP_ADDRESS="172.0.0.1";
    private int PORT=7686;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.err.println(datagramPacket);
        System.out.println("packet:"+datagramPacket.content().toString(CharsetUtil.UTF_8));

        String[] packets=datagramPacket.content().toString(CharsetUtil.UTF_8).split(";");

        // 思路
        // 1. 本地与云端前缀分开
        // 2. 待定

        //注册
        // "Register;{\"uuid\":\"123\",\"password\":\"aaa\",\"username\":\"aaa\",\"headImage\":\"aaa\"}"
        if (packets[0].equals("Register")){

            channelHandlerContext.write(new DatagramPacket(
                    Unpooled.copiedBuffer(datagramPacket.content().toString(),
                            CharsetUtil.UTF_8), new InetSocketAddress(IP_ADDRESS,PORT)));


        }
        //登录
        // "Login;{\"uuid\":\"123\",\"password\":\"aaa\"}"
        else if (packets[0].equals("Login")){


        }
        //退出(退出游戏大厅)  将玩家从在线列表删除        未测试，等匹配完成后同步测试
        // "Exit;{\"uuid\":\"123\"}"
        else if (packets[0].equals("Exit")){

        }
        //查看  查看当前在线玩家，为下一步玩家匹配做准备
        //还需完善，比如是否分表查询？
        else if (packets[0].equals("ViewOnline")){


        }
        //匹配  即游戏匹配，选择玩家进行游戏
        //"Match;{\"uuid\":\"123\",\"another\":\"123\"}"
        else if (packets[0].equals("Match")){


        }
        //退出(退出游戏)  将玩家从匹配列表删除
        // "ExitGame;{\"uuid\":\"123\"}"
        else if (packets[0].equals("ExitGame")){


        }


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