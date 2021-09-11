package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Netty servers contain two EventLoopGroups. The first represents the server's own listening
 * socket, bound to a local port. The second will contain all the Channels that have been created
 * to handle incoming connections (one for each connection the server has accepted)
 */
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
        MMQServerChannelHandler mmqServerChannelHandler = new MMQServerChannelHandler(this);
        MessageDecoder decoder = new MessageDecoder();
        decoder.setMMQServer(this);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(config.port()))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            socketChannel.pipeline().addLast(decoder,mmqServerChannelHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) //Dont understand
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind().sync();//sync waits for the bind to complete
            f.channel().closeFuture().sync();//gets CloseFuture of the channel and blocks current thread until it completes
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
