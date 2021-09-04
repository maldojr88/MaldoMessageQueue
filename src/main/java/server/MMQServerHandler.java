package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ChannelHandler.Sharable
public class MMQServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger log = LogManager.getLogger(MMQServerHandler.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("Accepting client connection");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    MessageType messageType = MessageType.from(in.readInt());

    //log.info("Server received: " + in.toString(CharsetUtil.UTF_8));
    log.info("Message type received by client " + messageType);
    ctx.write(in);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}