package server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.Message;
import net.MessageAck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class MMQServerChannelHandler extends ChannelInboundHandlerAdapter {
  private static final Logger log = LogManager.getLogger(MMQServerChannelHandler.class);
  private final MMQServer mmqServer;

  public MMQServerChannelHandler(MMQServer mmqServer) {
    this.mmqServer = mmqServer;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("Accepting client connection " + ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("Closing channel connection");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object obj) {
    Message msg = (Message) obj;
    msg.execute();
    ctx.writeAndFlush(MessageAck.newAck());
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
  }

  /**
   *
   * TODO
   *  1. Send response with an Error Code and close the connection
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(cause);
    //cause.printStackTrace();
    ctx.close();
  }
}