package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import net.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class MMQServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger log = LogManager.getLogger(MMQServerHandler.class);
  private final MMQServer mmqServer;

  public MMQServerHandler(MMQServer mmqServer) {
    this.mmqServer = mmqServer;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("Accepting client connection");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    MessageType messageType = MessageType.from(in.readInt());

    log.info("Message type received by client " + messageType);
    switch (messageType){
      case CONNECT_TO_PUBLISH -> {
        String queueName = in.toString(CharsetUtil.UTF_8);
        mmqServer.registerPublisher((InetSocketAddress) ctx.channel().remoteAddress(), queueName);
      }
      case PUBLISH -> {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if(!mmqServer.isRegisteredToPublish(remoteAddress))
          throw new RuntimeException("Remote address not registered to publish " + remoteAddress);

        String msgToPublish = in.toString(CharsetUtil.UTF_8);

      }
      default -> throw new IllegalStateException("Unexpected value: " + messageType);
    }
    ctx.write(in);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(cause);
    //cause.printStackTrace();
    ctx.close();
  }
}