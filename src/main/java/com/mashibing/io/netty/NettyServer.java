package com.mashibing.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer {

  public static void main(String[] args) throws InterruptedException {
    //线程数默认为cpu的核心数
    EventLoopGroup group = new NioEventLoopGroup(2);
    ServerBootstrap bootstrap = new ServerBootstrap();
    ChannelFuture channelFuture = bootstrap.group(group, group)
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
          @Override
          protected void initChannel(NioSocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new ClientReadHanler());
          }
        })
        .channel(NioServerSocketChannel.class)
        .bind("localhost", 8888);
    channelFuture.sync().channel().closeFuture().sync();
  }

}

class ClientReadHanler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;
    if(buf.readableBytes()>0){
      int len = buf.readableBytes();
      byte[] data = new byte[len];
      buf.readBytes(data);
      System.out.println(new String(data));
      ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer().writeBytes(data);
      ctx.channel().writeAndFlush(byteBuf);
    }
  }
}
