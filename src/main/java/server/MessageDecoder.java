package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import net.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * TODO
 *  1. Clean up Encoding/Decoding
 *      - Look at Protocol Buffers for Serialization (or Apache Thrift)
 */

public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger log = LogManager.getLogger(MessageDecoder.class);
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
}
