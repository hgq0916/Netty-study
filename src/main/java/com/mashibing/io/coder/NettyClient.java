package com.mashibing.io.coder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.Random;

public class NettyClient {

  public static void main(String[] args) throws InterruptedException {

    EventLoopGroup group = null;
    try{
      group = new NioEventLoopGroup(1);
      Bootstrap bootstrap = new Bootstrap();
      ChannelFuture channelFuture = bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new TankMsgEncoder());
              pipeline.addLast(new ClientEventHandler());
            }
          })
          .connect("localhost", 8888);

      ChannelFuture sync = channelFuture.sync();
      /*ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer().writeBytes("helloserver".getBytes());
      ChannelFuture channelFuture1 = sync.channel().writeAndFlush(byteBuf);*/
      sync.channel().closeFuture().sync();
    }finally {
      if(group != null){
        group.shutdownGracefully();
      }
    }

  }

}

class ClientEventHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channelInactive");
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
   TankMsg tankMsg = new TankMsg(100,50);
   ctx.writeAndFlush(tankMsg);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;
    if(buf.readableBytes()>0){
      byte[] data = new byte[buf.readableBytes()];
      buf.readBytes(data);
      System.out.println(new String(data));
    }
  }
}

class TankMsgEncoder extends MessageToByteEncoder<TankMsg> {

  @Override
  protected void encode(ChannelHandlerContext ctx, TankMsg msg, ByteBuf out) throws Exception {
    out.writeInt(msg.getX());
    out.writeInt(msg.getY());
  }
}