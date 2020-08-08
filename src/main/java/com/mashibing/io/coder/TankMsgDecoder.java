package com.mashibing.io.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class TankMsgDecoder extends ByteToMessageDecoder {

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
