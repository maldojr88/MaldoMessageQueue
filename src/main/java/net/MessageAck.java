package net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageAck {
    public static final int valueAck = 8888;

    public static boolean isAck(ByteBuf buf){
        return buf.readInt() == valueAck;
    }
    public static ByteBuf newAck(){
        return Unpooled.copyInt(valueAck);
    }
}
