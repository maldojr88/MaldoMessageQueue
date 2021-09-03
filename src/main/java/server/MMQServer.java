package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class MMQServer {

  private final int port;
  private static final Logger log = LogManager.getLogger(MMQServer.class);


  public MMQServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    if(args.length != 1) {
      log.error("Usage: " + MMQServer.class.getSimpleName() + " <port>");
      return;
    }
    String port = args[0];
    log.info("Starting up MMQ server on port " + port);
    MMQServer mmqServer = new MMQServer(Integer.parseInt(port));
    MMQCatalog catalog = new MMQCatalog();
    catalog.createQueue("DUMMY");
    mmqServer.acceptConnections();
  }


  public void acceptConnections() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(group)
          .channel(NioServerSocketChannel.class)
          .localAddress(new InetSocketAddress(port))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
              socketChannel.pipeline().addLast(new MMQServerHandler());
            }
          });
      ChannelFuture f = b.bind().sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
}