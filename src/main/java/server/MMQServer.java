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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Properties;

public class MMQServer {

  private static final Logger log = LogManager.getLogger(MMQServer.class);
  private static final String PROP_FILENAME = "mmq-server.properties";
  private final MMQConfig config;
  private final MMQCatalog catalog;

  public static void main(String[] args) throws Exception {
    MMQConfig config = loadProperties();
    MMQServer mmqServer = new MMQServer(config);
    mmqServer.createQueue("MMQ");
    mmqServer.acceptConnections();
  }

  private static MMQConfig loadProperties() throws Exception {
    Properties prop = new Properties();
    try {
      prop.load(MMQServer.class.getClassLoader().getResourceAsStream(PROP_FILENAME));
    } catch (IOException e) {
      throw new Exception("Failed to load MMQ Server properties");
    }
    return new MMQConfig(Path.of(prop.getProperty("catalog.dir")),
            Integer.parseInt(prop.getProperty("port")));
  }

  public MMQServer(MMQConfig config) {
    log.info("Loading MMQServer with the following config:\n" + config);
    this.config = config;
    this.catalog = new MMQCatalog(config.catalogDir());
  }

  public void createQueue(String queueName){
    catalog.createQueue(queueName);
  }

  public void acceptConnections() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(group)
          .channel(NioServerSocketChannel.class)
          .localAddress(new InetSocketAddress(config.port()))
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