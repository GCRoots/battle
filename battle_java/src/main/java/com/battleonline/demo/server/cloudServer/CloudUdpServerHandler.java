package com.battleonline.demo.server.cloudServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.battleonline.demo.dao.redis.RedisUtil;
import com.battleonline.demo.pojo.User;
import com.battleonline.demo.service.UserService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * @author shipengfei
 * @data 2020/2/15
 */
@Component
@ChannelHandler.Sharable
public class CloudUdpServerHandler  extends SimpleChannelInboundHandler<DatagramPacket> {
    @Autowired
    private UserService userService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.err.println(datagramPacket);
        System.out.println("packet:"+datagramPacket.content().toString(CharsetUtil.UTF_8));

        String[] packets=datagramPacket.content().toString(CharsetUtil.UTF_8).split(";");

        System.out.println(packets[1]);

        //注册
        // "Register;{\"uuid\":\"123\",\"password\":\"aaa\",\"username\":\"aaa\",\"headImage\":\"aaa\"}"
        if (packets[0].equals("Register")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");
            String password=jsonObject.getString("password");
            String username=jsonObject.getString("username");
            String headImage=jsonObject.getString("headImage");

            User user=new User(uuid,password,username,headImage);

            if (userService.findByUuid(uuid)!=null){
                System.err.println("用户ID已存在！！！");
                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("用户ID已存在！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));
            }else {
                userService.insertUser(user);
                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("注册成功！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));
            }
        }
        //登录
        // "Login;{\"uuid\":\"123\",\"password\":\"aaa\"}"
        else if (packets[0].equals("Login")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");
            String password=jsonObject.getString("password");

            User user=userService.findByUuid(uuid);

            if (password.equals(user.getPassword())){
                System.err.println(user.getUuid()+"\t登录成功！！！");

                //将登录成功用户放入在线用户表中
                //sender格式:  /192.168.1.106:33014
                redisUtil.hset("onlone_user",uuid,datagramPacket.sender().toString().substring(1));
                System.out.println(datagramPacket.sender());

                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("TRUE:登录成功！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));
            }else{
                System.err.println("用户密码错误！！！");
                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("FALSE:用户密码错误！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));

            }


        }
        //退出  将玩家从在线列表删除        未测试，等匹配完成后同步测试
        // "Exit;{\"uuid\":\"123\"}"
        else if (packets[0].equals("Exit")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");

            //uuid在match_user表中，即当前想要下线的用户已参与匹配
            if (redisUtil.hHasKey("match_user",uuid)){
                String another= (String) redisUtil.hget("match_user",uuid);
                redisUtil.hdel("match_user",uuid);
                redisUtil.hdel("match_user",another);

                String sender= (String) redisUtil.hget("onlone_user",another);
                String[] senders=sender.split(":");

                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("对方以离开！！！",
                                CharsetUtil.UTF_8), new InetSocketAddress(senders[0], Integer.parseInt(senders[1]))));

            }
            //uuid在onlone_user表中，即当前想要下线的用户在线
            if (redisUtil.hHasKey("onlone_user",uuid)){
                redisUtil.hdel("onlone_user",uuid);

            }

            channelHandlerContext.write(new DatagramPacket(
                    Unpooled.copiedBuffer("Exit:您以离线！！！",
                            CharsetUtil.UTF_8), datagramPacket.sender()));

        }
        //查看  查看当前在线玩家，为下一步玩家匹配做准备
        else if (packets[0].equals("ViewOnline")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");


        }
        //匹配  即游戏匹配，选择玩家进行游戏
        else if (packets[0].equals("Match")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");


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
