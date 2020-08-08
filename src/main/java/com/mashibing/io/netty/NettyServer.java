package com.mashibing.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

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

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    //将客户端加入channel组
    NettyServer.clients.add(ctx.channel());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = null;
    try{
      buf = (ByteBuf) msg;
      if(buf.readableBytes()>0){
        int len = buf.readableBytes();
        byte[] data = new byte[len];
        buf.getBytes(buf.readerIndex(),data);
        System.out.println(new String(data));
        //向所有的客户端转发消息
        NettyServer.clients.writeAndFlush(buf);
       // ctx.channel().writeAndFlush(buf);
      }
    }finally{
      System.out.println(buf.refCnt());
      /*ReferenceCountUtil.release(buf);
      System.out.println(buf.refCnt());*/
    }

  }
}
