package com.mashibing.io.coder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.List;

public class NettyServer {

  public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  public static void main(String[] args) throws InterruptedException {

    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;
    try{
      //线程数默认为cpu的核心数
      bossGroup = new NioEventLoopGroup(1);
      workerGroup = new NioEventLoopGroup(2);
      ServerBootstrap bootstrap = new ServerBootstrap();
      ChannelFuture channelFuture = bootstrap.group(bossGroup, workerGroup)
          .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new TankMsgDecoder());
              pipeline.addLast(new ClientReadHanler());
            }
          })
          .channel(NioServerSocketChannel.class)
          .bind("localhost", 8888);
      channelFuture.sync().channel().closeFuture().sync();//closeFuture.sync在调用close后方法才返回
    }finally {
      if(bossGroup != null){
        bossGroup.shutdownGracefully();
      }
      if(workerGroup != null){
        //关闭线程池
        workerGroup.shutdownGracefully();
      }
    }

  }

}

class ClientReadHanler extends ChannelInboundHandlerAdapter {

/*
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    //ChatServer.clients.remove(ctx.channel());
  }
*/

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    //将客户端加入channel组
    NettyServer.clients.add(ctx.channel());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    TankMsg tankMsg = (TankMsg) msg;
    System.out.println(tankMsg);
  }
}

class TankMsgDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if(in.readableBytes()>=8){
      int x = in.readInt();
      int y = in.readInt();
      TankMsg tankMsg = new TankMsg(x,y);
      out.add(tankMsg);
    }
  }
}