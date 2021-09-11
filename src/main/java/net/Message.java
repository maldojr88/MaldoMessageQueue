package net;

import server.MMQServer;

public abstract class Message {
    protected MMQServer server;
    public abstract void execute();
}
