package com.battleonline.demo.server.cloudServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

/**
 * @author shipengfei
 * @data 2020/2/15
 */
@Component
@ChannelHandler.Sharable
public class CloudUdpServerHandler  extends SimpleChannelInboundHandler<DatagramPacket> {
    @Autowired
    private UserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.err.println(datagramPacket);
        System.out.println("packet:"+datagramPacket.content().toString(CharsetUtil.UTF_8));

        String[] packets=datagramPacket.content().toString(CharsetUtil.UTF_8).split(",");

        //登录
        if (packets[0].equals("Login")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");
            String password=jsonObject.getString("password");

            User user=userService.findByUuid(uuid);

            if (password.equals(user.getPassword())){
                System.err.println(user.getUuid()+"\t登录成功！！！");
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
        //注册
        else if (packets[0].equals("Register")){
            User user=new User();
            user.setUuid("123345");
            user.setPassword("123789456");
            user.setUsername("aaa");
            user.setHeadImage("aa");
            user.setSender("192.168.1.1");

            if (userService.findByUuid(user.getUuid())!=null){
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
