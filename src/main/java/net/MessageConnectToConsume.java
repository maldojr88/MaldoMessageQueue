package net;

import server.MMQServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MessageConnectToConsume extends Message{
    private String queueName;
    private InetSocketAddress remoteAddress;
    private long instant;

    public MessageConnectToConsume(MMQServer server, InetSocketAddress remoteAddress, String queueName, long instant){
        this.server = server;
        this.queueName = queueName;
        this.remoteAddress = remoteAddress;
        this.instant = instant;
    }
    @Override
    public void execute() throws IOException {
        server.registerConsumer(remoteAddress, queueName, instant);
    }
}
