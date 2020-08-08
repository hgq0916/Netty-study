package com.mashibing.netty;

import com.mashibing.io.coder.TankMsg;
import com.mashibing.io.coder.TankMsgDecoder;
import com.mashibing.io.coder.TankMsgEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

public class NettyEmbedTest {

  @Test
  public void testTankMsgEncode1(){
    TankMsg tankMsg = new TankMsg(20,10);
    EmbeddedChannel channel = new EmbeddedChannel();
    ChannelPipeline pipeline = channel.pipeline();
    pipeline.addLast(new TankMsgEncoder()).addLast(new TankMsgDecoder());
    channel.writeOutbound(tankMsg);
    ByteBuf buf = channel.readOutbound();

    int x = buf.readInt();
    int y = buf.readInt();
    Assert.assertTrue(x==20&& y==10);
  }

  @Test
  public void testTankMsgEncode2(){
    TankMsg tankMsg = new TankMsg(20,10);
    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
    buf.writeInt(tankMsg.getX());
    buf.writeInt(tankMsg.getY());

    EmbeddedChannel channel = new EmbeddedChannel();
    channel.pipeline().addLast(new TankMsgEncoder()).addLast(new TankMsgDecoder());
    channel.writeInbound(buf.duplicate());
    TankMsg msg = channel.readInbound();
    Assert.assertTrue(msg.getX()==20 && msg.getY()==10);
  }

}
