package net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.MMQServer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * TODO
 *  1. Clean up Encoding/Decoding
 *      - Look at Protocol Buffers for Serialization (or Apache Thrift)
 */

public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger log = LogManager.getLogger(MessageDecoder.class);
    private static final int CHECKSUM_BYTES = 8;
    private MMQServer mmqServer;

    public void setMMQServer(MMQServer server){
        this.mmqServer = server;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if(in.readableBytes() < 4){//enough to decode MessageType
            return;
        }

        try {
            in.markReaderIndex();
            MessageType messageType = MessageType.from(in.readInt());

            log.info("Message type received by client " + messageType);
            switch (messageType) {
                case CONNECT_TO_PUBLISH -> {
                    if(in.readableBytes() < 4) {
                        in.resetReaderIndex();
                        return;
                    }
                    int strLen = in.readInt();
                    if(in.readableBytes() < strLen){
                        in.resetReaderIndex();
                        return;
                    }
                    ByteBuf strBuff = in.readBytes(strLen);
                    String queueName = strBuff.toString(CharsetUtil.UTF_8);
                    Message msg = new MessageConnectPublish(mmqServer,(InetSocketAddress) ctx.channel().remoteAddress(),
                            queueName);
                    //validateChecksum(in);
                    out.add(msg);
                }
                case CONNECT_TO_CONSUME -> {
                    if(in.readableBytes() < 4) {
                        in.resetReaderIndex();
                        return;
                    }
                    int strLen = in.readInt();
                    if(in.readableBytes() < strLen){
                        in.resetReaderIndex();
                        return;
                    }
                    ByteBuf strBuff = in.readBytes(strLen);
                    String queueName = strBuff.toString(CharsetUtil.UTF_8);
                    if(in.readableBytes() < 8){
                        in.resetReaderIndex();
                        return;
                    }
                    long instant = in.readLong();
                    Message msg = new MessageConnectToConsume(mmqServer,(InetSocketAddress) ctx.channel().remoteAddress(),
                            queueName, instant);
                    //validateChecksum(in);
                    out.add(msg);
                }
                case PUBLISH -> {
                    if(in.readableBytes() < 4) {
                        in.resetReaderIndex();
                        return;
                    }
                    int queueStrLen = in.readInt();
                    if(in.readableBytes() < queueStrLen){
                        in.resetReaderIndex();
                        return;
                    }
                    ByteBuf queueNameBuff = in.readBytes(queueStrLen);
                    String queueName = queueNameBuff.toString(CharsetUtil.UTF_8);

                    if(in.readableBytes() < 4) {
                        in.resetReaderIndex();
                        return;
                    }
                    int msgStrLen = in.readInt();
                    if(in.readableBytes() < msgStrLen){
                        in.resetReaderIndex();
                        return;
                    }
                    ByteBuf msgBuff = in.readBytes(msgStrLen);
                    String msgToPublish = msgBuff.toString(CharsetUtil.UTF_8);
                    log.info("Message to Publish: " + msgToPublish);
                    Message msg = new MessagePublish(mmqServer, queueName, msgToPublish,
                            (InetSocketAddress) ctx.channel().remoteAddress());
                    //validateChecksum(in);
                    out.add(msg);
                }
                default -> throw new IllegalStateException("Unexpected value: " + messageType);
            }
        }
        finally {
            //ctx.write(in);
            //in.release();
        }
    }

    private void validateChecksum(ByteBuf in) {
        long received = in.readLong();
        int currentPos = in.readerIndex();
        in.resetReaderIndex();
        int numBytes = currentPos - in.readerIndex();
        byte[] bytes = new byte[numBytes];
        in.readBytes(bytes, 0, numBytes);

        Checksum crc32 = new CRC32();
        crc32.update(bytes);
        long computed = crc32.getValue();
        if(received != computed){
            String errorMsg = String.format("Corrupted data in CRC! Received=%d Computed=%d",
                    received, computed);
            throw new RuntimeException(errorMsg);
        }
    }
}