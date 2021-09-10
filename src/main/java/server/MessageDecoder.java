package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import net.MessageAck;
import net.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;

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
                    mmqServer.registerPublisher((InetSocketAddress) ctx.channel().remoteAddress(), queueName);
                    ctx.writeAndFlush(MessageAck.ACK);
                }
                case PUBLISH -> {
                    InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                    if (!mmqServer.isRegisteredToPublish(remoteAddress))
                        throw new RuntimeException("Remote address not registered to publish " + remoteAddress);

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
                    String msgToPublish = strBuff.toString(CharsetUtil.UTF_8);
                    log.info("Message to Publish: " + msgToPublish);
                    ctx.writeAndFlush(MessageAck.ACK);

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
