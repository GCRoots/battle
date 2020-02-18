package com.battleonline.demo.server.cloudServer;

import com.battleonline.demo.dao.redis.RedisUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author shipengfei
 * @data 2020/2/15
 */
@Service
public class CloudUdpServer implements Runnable{
    private static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
    private Thread nserver;

    @Autowired
    CloudUdpServerHandler cloudUdpServerHandler;

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
                    .handler(cloudUdpServerHandler);

            b.bind(PORT).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
