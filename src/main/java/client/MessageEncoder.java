package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class MessageEncoder {
    private static final Logger log = LogManager.getLogger(MessageEncoder.class);

    public static ByteBuf connectToPublish(String queueName){
        MessageType messageType = MessageType.CONNECT_TO_PUBLISH;
        log.info("Sending messageType to the Server " + messageType);
        ByteBuf buffer = Unpooled.copyInt(messageType.getType());
        byte[] bytes = queueName.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        return buffer;
    }

    public static ByteBuf publish(String queueName, String msg){
        ByteBuf byteBuf = Unpooled.copyInt(MessageType.PUBLISH.getType());
        MessageType messageType = MessageType.PUBLISH;
        log.info("Sending messageType to the Server " + messageType);
        byte[] queueNameBytes = queueName.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(queueName.length());
        byteBuf.writeBytes(queueNameBytes);
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(msgBytes.length);
        byteBuf.writeBytes(msgBytes);
        return byteBuf;
    }
}
