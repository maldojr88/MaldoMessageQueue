package net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageAck {
    public static final int valueAck = 8888;
    public static final ByteBuf ACK = Unpooled.copyInt(valueAck);

    public static boolean isAck(ByteBuf buf){
        return buf.readInt() == valueAck;
    }
}
