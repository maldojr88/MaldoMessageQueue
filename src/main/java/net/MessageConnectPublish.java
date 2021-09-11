package net;

import server.MMQServer;

import java.net.InetSocketAddress;

public class MessageConnectPublish extends Message{
    private String queueName;
    private InetSocketAddress remoteAddress;

    public MessageConnectPublish(MMQServer server, InetSocketAddress remoteAddress, String queueName){
        this.server = server;
        this.queueName = queueName;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void execute() {
        server.registerPublisher(remoteAddress, queueName);
    }
}
