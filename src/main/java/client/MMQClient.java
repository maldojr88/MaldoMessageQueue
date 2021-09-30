package client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.Message;
import net.MessageEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class MMQClient {
  private static final Logger log = LogManager.getLogger(MMQClient.class);
  private final String host;
  private final int port;

  public MMQClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .remoteAddress(new InetSocketAddress(host, port))
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new MMQClientChannelHandler());
            }
          });
      ChannelFuture f = b.connect().sync();//connect to the remote peer and wait until the connection completes
      Channel channel = f.channel();
      //f.channel().closeFuture().sync();//block until the channel closes

      //1st message

      channel.writeAndFlush(MessageEncoder.connectToPublish("Q1")).sync();
      Thread.sleep(3000);

      //2nd message
      channel.writeAndFlush(MessageEncoder.publish("Q1", "Storing MSG # 1")).sync();
      channel.writeAndFlush(MessageEncoder.publish("Q1", "Storing MSG # 2")).sync();
      channel.writeAndFlush(MessageEncoder.publish("Q1", "Storing MSG # 3")).sync();
      channel.writeAndFlush(MessageEncoder.publish("Q1", "Storing MSG # 4")).sync();

      Scanner keyboard = new Scanner(System.in);
      System.out.println("Enter instant");
      long instant = keyboard.nextLong();

      //Consume
      channel.writeAndFlush(MessageEncoder.connectToConsume("Q1", instant )).sync();
      //Thread.sleep(100000);
    } finally {
      //group.shutdownGracefully().sync();
    }
  }

  public static void main(String[] args) throws Exception {
    if(args.length != 2) {
      log.error("Usage: " + MMQClient.class.getSimpleName() + " <host> <port>");
      return;
    }

    final String host = args[0];
    final int port = Integer.parseInt(args[1]);
    log.info("Connecting MMQ client to " + host + " and port " + port);
    new MMQClient(host, port).start();
  }
}