package net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class MessageEncoder {
    private static final Logger log = LogManager.getLogger(MessageEncoder.class);

    public static ByteBuf connectToPublish(String queueName){
        MessageType messageType = MessageType.CONNECT_TO_PUBLISH;
        log.info("Sending messageType to the Server " + messageType);
        ByteBuf buffer = Unpooled.copyInt(messageType.getType());
        byte[] bytes = queueName.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        //addChecksum(buffer);
        return buffer;
    }

    public static ByteBuf publish(String queueName, String msg){
        ByteBuf buffer = Unpooled.copyInt(MessageType.PUBLISH.getType());
        MessageType messageType = MessageType.PUBLISH;
        log.info("Sending messageType to the Server " + messageType);
        byte[] queueNameBytes = queueName.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(queueName.length());
        buffer.writeBytes(queueNameBytes);
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(msgBytes.length);
        buffer.writeBytes(msgBytes);
        //addChecksum(buffer);
        return buffer;
    }

    private static void addChecksum(ByteBuf buf) {
        Checksum crc32 = new CRC32();
        byte[] bytes = new byte[buf.readableBytes()];
        int readerIndex = buf.readerIndex();
        buf.getBytes(readerIndex, bytes);
        crc32.update(bytes);
        buf.writeLong(crc32.getValue());
    }
}