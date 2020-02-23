package com.battleonline.demo.server.local.localServer;

import com.battleonline.demo.dao.redis.RedisUtil;
import com.battleonline.demo.server.cloudServer.CloudUdpServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author shipengfei
 * @data 2020/2/22
 */
@Service
public class LocalServer implements Runnable{
    private static final int PORT = Integer.parseInt(
            System.getProperty("port", "9999"));
    private Thread nserver;

    @Autowired
    LocalServerHandler localServerHandler;

    @Autowired
    RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        nserver = new Thread(this);
        nserver.start();
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(localServerHandler);

            b.bind(PORT).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}