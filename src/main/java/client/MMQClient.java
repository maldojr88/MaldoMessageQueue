package client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

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
      //ChannelFuture f = b.connect().sync();
      ChannelFuture f = b.connect();
      Channel channel = f.channel();
      //f.channel().closeFuture().sync();

      //1st message
      channel.writeAndFlush(MessageEncoder.connectToPublish("Q1")).sync();
      Thread.sleep(3000);

      //2nd message
      channel.writeAndFlush(MessageEncoder.publish("MMG Tooo")).sync();
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