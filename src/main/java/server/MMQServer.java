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

import java.net.InetSocketAddress;
import java.util.*;

public class MMQServer {
    private static final Logger log = LogManager.getLogger(MMQServer.class);
    private final MMQConfig config;
    private final MMQCatalog catalog;
    private final Map<InetSocketAddress, List<String>> publishers;
    private final Map<InetSocketAddress, List<String>> consumers;


    public MMQServer(MMQConfig config) {
        log.info("Loading MMQServer with the following config:\n" + config);
        this.config = config;
        this.catalog = new MMQCatalog(config.catalogDir());
        this.publishers = new HashMap<>();
        this.consumers = new HashMap<>();
    }

    public void createQueue(String queueName){
        catalog.createQueue(queueName);
    }

    public void acceptConnections() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        MMQServerHandler mmqServerHandler = new MMQServerHandler(this);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(config.port()))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            socketChannel.pipeline().addLast(mmqServerHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public void registerPublisher(InetSocketAddress remoteAddress, String queueName) {
        log.info("Adding " + remoteAddress + " to publishers for " + queueName);
        List<String> lst = publishers.getOrDefault(remoteAddress, new ArrayList<>());
        lst.add(queueName);
        publishers.put(remoteAddress, lst);
        log.info("Publishers: " + publishers);
    }

    public boolean isRegisteredToPublish(InetSocketAddress remoteAddress){
        return publishers.containsKey(remoteAddress);
    }
}
