package com.mashibing.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

/**
 * @author gangquan.hu
 * @Package: com.mashibing.io.AIOClientTest
 * @Description: TODO
 * @date 2020/8/7 11:22
 */
public class AIOClientTest {

  public static void main(String[] args) throws IOException {
    AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
    Future<Void> future = asynchronousSocketChannel.connect(new InetSocketAddress("192.168.68.1", 9090));
    if(future.isDone()){
      ByteBuffer buf = ByteBuffer.allocateDirect(1024);
      ByteBuffer writeBuf = ByteBuffer.wrap("hello server".getBytes());
      asynchronousSocketChannel.write(writeBuf);
      asynchronousSocketChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
        @Override
        public void completed(Integer result, ByteBuffer attachment) {
          if(result>0){
            byte[] data = new byte[result];
            attachment.flip();
            ByteBuffer byteBuffer = attachment.get(data);
            System.out.println(new String(data));
          }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
          exc.printStackTrace();
        }
      });
    }

    System.in.read();
  }

}
