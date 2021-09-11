package net;

import server.MMQServer;

import java.net.InetSocketAddress;

public class MessagePublish extends Message{

    private final String queueName;
    private final String msg;
    private final InetSocketAddress remoteAddress;

    public MessagePublish(MMQServer server, String queueName, String msg, InetSocketAddress remoteAddress){
        this.server = server;
        this.queueName = queueName;
        this.msg = msg;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void execute() {
        server.publish(remoteAddress, queueName, msg);
    }
}
