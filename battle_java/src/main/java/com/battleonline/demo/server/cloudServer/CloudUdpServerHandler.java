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
import java.util.HashMap;
import java.util.Map;

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

            if (user==null) {
                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("FALSE:用户不存在！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));
                return;
            }

            if (password.equals(user.getPassword())){
                System.err.println(user.getUuid()+"\t登录成功！！！");

                //将登录成功用户放入在线用户表中
                //sender格式:  /192.168.1.106:33014
                redisUtil.hset("onlone_user_sender",uuid,datagramPacket.sender().toString().substring(1));
                redisUtil.hset("onlone_user_username",uuid,user.getUsername());
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
        //退出(退出游戏大厅)  将玩家从在线列表删除        未测试，等匹配完成后同步测试
        // "Exit;{\"uuid\":\"123\"}"
        else if (packets[0].equals("Exit")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");

            //uuid在match_user表中，即当前想要下线的用户已参与匹配
            if (redisUtil.hHasKey("match_user",uuid)){
                String another= (String) redisUtil.hget("match_user",uuid);
                //从已匹配表删除相应数据
                redisUtil.hdel("match_user",uuid);
                redisUtil.hdel("match_user",another);

                //通知未下线玩家对方已下线
                String sender= (String) redisUtil.hget("onlone_user_sender",another);
                String[] senders=sender.split(":");

                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("对方以离开！！！",
                                CharsetUtil.UTF_8), new InetSocketAddress(senders[0], Integer.parseInt(senders[1]))));

            }
            //uuid在 onlone_user_sender 表中，即当前想要下线的用户在线
            if (redisUtil.hHasKey("onlone_user_sender",uuid)){
                redisUtil.hdel("onlone_user_sender",uuid);
            }
            //uuid在 onlone_user_username 表中，即当前想要下线的用户在线
            if (redisUtil.hHasKey("onlone_user_username",uuid)){
                redisUtil.hdel("onlone_user_username",uuid);
            }

            channelHandlerContext.write(new DatagramPacket(
                    Unpooled.copiedBuffer("Exit:您以离线！！！",
                            CharsetUtil.UTF_8), datagramPacket.sender()));

        }
        //查看  查看当前在线玩家，为下一步玩家匹配做准备
        //还需完善，比如是否分表查询？
        else if (packets[0].equals("ViewOnline")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");


//            redi测试
//            Map<String, Object> map=new HashMap<>();
//            map.put("1","a");
//            map.put("2","b");
//            map.put("3","c");
//            map.put("4","d");
//            map.put("5","e");
////            redisUtil.hmset("onlone_user_username",map);
//            System.out.println(redisUtil.hasKey("onlone_user_username"));
//            System.out.println(redisUtil.hmget("onlone_user_username").toString());
//            redisUtil.hdel("onlone_user_username","1","2","3","4","5");
//            System.out.println(redisUtil.hmget("onlone_user_username").toString());

            System.out.println();

            //onlone_user_username表中有数据，即当前有用户在线
            if (redisUtil.hasKey("onlone_user_username")){
                JSONObject jsonObject1= (JSONObject) JSONObject.toJSON(redisUtil.hmget("onlone_user_username"));;
                System.out.println(jsonObject1.toJSONString());

                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("YES:"+jsonObject1.toJSONString(),
                                CharsetUtil.UTF_8), datagramPacket.sender()));
            }else {
                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("NO:当期无玩家在线！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));
            }



        }
        //匹配  即游戏匹配，选择玩家进行游戏
        //"Match;{\"uuid\":\"123\",\"another\":\"123\"}"
        else if (packets[0].equals("Match")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");
            String another=jsonObject.getString("another");

            //双方玩家都在线
            if (redisUtil.hHasKey("onlone_user_sender",uuid) &&
                    redisUtil.hHasKey("onlone_user_sender",another)){

                //将双方地址分别对放入 match_user 表
                redisUtil.hset("match_user",uuid,another);
                redisUtil.hset("match_user",another,uuid);

                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("TRUE:匹配成功！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));

            }else {
                channelHandlerContext.write(new DatagramPacket(
                        Unpooled.copiedBuffer("FALSE:匹配失败！！！",
                                CharsetUtil.UTF_8), datagramPacket.sender()));

            }


        }
        //退出(退出游戏)  将玩家从匹配列表删除
        // "ExitGame;{\"uuid\":\"123\"}"
        else if (packets[0].equals("ExitGame")){
            JSONObject jsonObject= JSON.parseObject(packets[1]);
            String uuid=jsonObject.getString("uuid");

            if (redisUtil.hHasKey("match_user",uuid)) {
                String another= (String) redisUtil.hget("match_user",uuid);

                if (redisUtil.hHasKey("match_user",another)) {
                    redisUtil.hdel("match_user",another);
                }

                redisUtil.hdel("match_user",uuid);
            }

            channelHandlerContext.write(new DatagramPacket(
                    Unpooled.copiedBuffer("TRUE:退出游戏！！！",
                            CharsetUtil.UTF_8), datagramPacket.sender()));

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
