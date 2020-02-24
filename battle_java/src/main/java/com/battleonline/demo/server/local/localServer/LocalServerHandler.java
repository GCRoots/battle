package com.battleonline.demo.server.local.localServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author shipengfei
 * @data 2020/2/22
 */
@Component
@ChannelHandler.Sharable
public class LocalServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    //云端服务器ip、端口
    private String CLOUD_IP ="255.255.255.255";
    private int CLOUD_PORT =7686;

    //本地大厅端ip、端口
    private String LOCAL_IP ="255.255.255.255";
    private int LOCAL_PORT =7686;

    //本地游戏端ip、端口
    private String GAME_IP ="255.255.255.255";
    private int GAME_PORT =7686;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.err.println(datagramPacket);
        System.out.println("packet:"+datagramPacket.content().toString(CharsetUtil.UTF_8));

        String[] packets=datagramPacket.content().toString(CharsetUtil.UTF_8).split(";");

        // 思路
        // 1. 本地与云端前缀分开
        // 2. 待定

        //本地大厅信息端与游戏端发送来的数据，去除前缀后直接发送至云端
        //消息格式："Local/Game;方法名;{JSON字符串}"
        if (packets[0].equals("Local")||packets[0].equals("Game")){

            channelHandlerContext.write(new DatagramPacket(
                    Unpooled.copiedBuffer(packets[1]+";"+packets[2],
                            CharsetUtil.UTF_8), new InetSocketAddress(CLOUD_IP, CLOUD_PORT)));

        }
        //接受到来自云端的消息
        //云端到本地大厅端
        //消息格式："C-L/C-G;方法名;True/False;{JSON字符串}/字符串"
        else if (packets[0].equals("C-L")) {


        }
        //云端到本地游戏端
        else if (packets[0].equals("C-G")){



        }

        
        //后续，转入本地大厅端用于对返回数据进行处理
        {

            //注册
            // "Register;{\"uuid\":\"123\",\"password\":\"aaa\",\"username\":\"aaa\",\"headImage\":\"aaa\"}"
            if (packets[0].equals("Registered")){
                if (packets[1].equals("True")){

                }else {

                }

            }
            //登录
            // "Login;{\"uuid\":\"123\",\"password\":\"aaa\"}"
            else if (packets[0].equals("Logined")){
                if (packets[1].equals("True")){

                }else {

                }

            }
            //退出(退出游戏大厅)  将玩家从在线列表删除        未测试，等匹配完成后同步测试
            // "Exit;{\"uuid\":\"123\"}"
            else if (packets[0].equals("Exited")){
                if (packets[1].equals("True")){

                }else {

                }

            }
            //查看  查看当前在线玩家，为下一步玩家匹配做准备
            //还需完善，比如是否分表查询？
            else if (packets[0].equals("ViewOnlined")){
                if (packets[1].equals("True")){

                }else {

                }

            }
            //匹配  即游戏匹配，选择玩家进行游戏
            //"Match;{\"uuid\":\"123\",\"another\":\"123\"}"
            else if (packets[0].equals("Matched")){
                if (packets[1].equals("True")){

                }else {

                }

            }
            //退出(退出游戏)  将玩家从匹配列表删除
            // "ExitGame;{\"uuid\":\"123\"}"
            else if (packets[0].equals("ExitGamed")) {
                if (packets[1].equals("True")) {

                } else {

                }

            }

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