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
        else if (packets[0].equals("Login")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");
            String password=jsonObject.getString("password");

            User user=userService.findByUuid(uuid);

            if (password.equals(user.getPassword())){
                System.err.println(user.getUuid()+"\t登录成功！！！");

                //将登录成功用户放入在线用户表中
                String sender=datagramPacket.sender().toString();
                redisUtil.set(uuid,password,60);

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
        //退出  将玩家从在线列表删除
        else if (packets[0].equals("Quit")){



        }
        //查看  查看当前在线玩家，为下一步玩家匹配做准备
        else if (packets[0].equals("View")){



        }
        //匹配  即游戏匹配，选择玩家进行游戏
        else if (packets[0].equals("Match")){


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
