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
import queue.Queue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;

/**
 * Netty servers contain two EventLoopGroups. The first represents the server's own listening
 * socket, bound to a local port. The second will contain all the Channels that have been created
 * to handle incoming connections (one for each connection the server has accepted)
 */
public class MMQServer {
    private static final Logger log = LogManager.getLogger(MMQServer.class);
    private final MMQConfig config;
    private final MMQCatalog catalog;

    public MMQServer(MMQConfig config) throws IOException {
        log.info("Loading MMQServer with the following config:\n" + config);
        this.config = config;
        this.catalog = new MMQCatalog(config.queuesDir());
    }

    public void createQueue(String queueName){
        try {
            catalog.createQueue(queueName);
        } catch (IOException e) {
            log.error("Failed to create queue", e);
        }
    }

    public void registerPublisher(InetSocketAddress remoteAddress, String queueName) {
        Queue queue = catalog.getQueue(queueName);
        queue.addPublisher(remoteAddress);
    }

    public void registerConsumer(InetSocketAddress remoteAddress, String queueName, long instant) {
        Queue queue = catalog.getQueue(queueName);
        queue.addConsumer(remoteAddress);
    }

    public void publish(InetSocketAddress address, String queueName, String msg) throws IOException {
        Queue queue = catalog.getQueue(queueName);
        queue.publish(address, msg);
    }

    public void initialize() throws Exception {
        createWorkspace();
        bootstrapQueues();
        acceptConnections();
    }

    private void createWorkspace() throws IOException {
        log.info("Creating workspace dirs");
        Files.createDirectories(config.serverDir());
        Files.createDirectories(config.catalogDir());
        Files.createDirectories(config.queuesDir());
        log.info("Creating workspace dirs - Complete");
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

    private void bootstrapQueues() {
        createQueue("Q1");
        createQueue("Q2");
    }
}
