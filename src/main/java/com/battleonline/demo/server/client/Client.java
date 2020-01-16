package com.battleonline.demo.server.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

public class
Client {

    public static void main(String[] args) {
        Client client=new Client();
        client.connect("localhost",55555);

    }

    public void connect(String host,int port){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new HttpClientInitializer());

            ChannelFuture f = b.connect(host, port).sync();

            URI uri = new URI("http://127.0.0.1:8089/farmWaterContral");
            String msg = "{\"humidity\":\"39\",\"tapId\":\"1\",\"camera\":\"f\"," +
                    "\"sensor1\":\"f\",\"sensor2\":\"f\",\"sensor3\":\"f\",\"sensor4\":\"f\",\"sensor5\":\"f\"}";
            FullHttpRequest request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

            // 构建http请求
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());

            // 发送http请求
            f.channel().write(request);
            f.channel().flush();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }


}


