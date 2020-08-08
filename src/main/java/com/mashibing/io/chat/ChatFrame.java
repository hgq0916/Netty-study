package com.mashibing.io.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatFrame extends Frame {

  private TextArea ta = new TextArea();
  private TextField tf = new TextField();


  public TextArea getTa() {
    return ta;
  }

  public TextField getTf() {
    return tf;
  }

  MsgSender msgSender = new MsgSender(this);

  public ChatFrame(){
    this.setSize(600,500);
    this.setLocation(200,200);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        msgSender.close();
        System.exit(0);
      }
    });
    this.add(ta, BorderLayout.CENTER);
    this.add(tf,BorderLayout.SOUTH);
    tf.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String text = tf.getText();
        //ta.append(text+"\n");
        tf.setText("");

        //向服务端发送消息
        msgSender.send(text);
      }
    });
    this.setVisible(true);
    try {
      msgSender.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new ChatFrame();
  }

  public void updateMsg(String str) {
    ta.append(str+System.getProperty("line.separator"));
  }
}

class MsgSender {

  private ChatFrame chatFrame;

  private Channel channel;

  public MsgSender(ChatFrame chatFrame) {
    this.chatFrame = chatFrame;
  }

  public void init() {
    EventLoopGroup group = null;
    try{
      group = new NioEventLoopGroup(1);
      Bootstrap bs = new Bootstrap();
      ChannelFuture channelFuture = bs.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new ClientMsgHandler());
            }
          })
          .connect("localhost", 9090)
          .addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
              if(future.isSuccess()){
                channel = future.channel();
              }else {
                System.out.println("connect server fail");
              }
            }
          })
          .sync();
      channelFuture.channel().closeFuture().sync();
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      if(group!=null){
        group.shutdownGracefully();
      }
    }

  }

  public void send(String str){
    ByteBuf byteBuf = Unpooled.copiedBuffer(str.getBytes());
    channel.writeAndFlush(byteBuf);
  }

  public void close() {
    send("~bye~");
    channel.close();
  }

  class ClientMsgHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ByteBuf byteBuf = (ByteBuf) msg;
      byte[] data = new byte[byteBuf.readableBytes()];
      byteBuf.getBytes(byteBuf.readerIndex(),data);

      String str = new String(data);
      chatFrame.updateMsg(str);

    }
  }

}
