#### BIO

![image-20200807090239923](BIO、NIO、AIO-Netty.assets/image-20200807090239923.png)

每个客户端起一个线程去处理

BIO的通道是单向的，InputStream,OutputStream

accept,read.write都是阻塞的

#### NIO

![image-20200807090407673](BIO、NIO、AIO-Netty.assets/image-20200807090407673.png)

#### NIO-Reactor模式

![image-20200807095733322](BIO、NIO、AIO-Netty.assets/image-20200807095733322.png)

aio是对nio做了封装，当有事件发生时，通过执行回调来完成处理过程；