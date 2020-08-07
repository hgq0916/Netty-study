package com.mashibing.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author gangquan.hu
 * @Package: com.mashibing.io.AIOTest
 * @Description: TODO
 * @date 2020/8/7 10:47
 */
public class AIOServerTest {

  public static void main(String[] args) throws IOException {
    AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open()
        .bind(new InetSocketAddress("192.168.68.1",9090));
    channel.accept("2132423", new CompletionHandler<AsynchronousSocketChannel, Object>() {
      @Override
      public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
        System.out.println("attachment:"+attachment);
        channel.accept(null,this);
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        socketChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {

          @Override
          public void completed(Integer result, ByteBuffer buffer) {
            System.out.println("result:"+result);
            System.out.println("attachment:"+attachment);
            buffer.flip();
            if(result>0){
              byte[] array = new byte[result];
              buffer.get(array);
              System.out.println(new String(array));
              socketChannel.write(ByteBuffer.wrap(array));
            }
          }

          @Override
          public void failed(Throwable exc, ByteBuffer attachment) {
            exc.printStackTrace();
          }
        });
      }

      @Override
      public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
      }
    });
    System.in.read();
  }

}
