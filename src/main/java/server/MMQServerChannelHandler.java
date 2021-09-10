package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import net.MessageAck;
import net.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * TODO
 *  1. Clean up Encoding/Decoding
 *      - Separate different operations on the channel into different channelHandlers.
 *      - Look at ByteToMessageDecoder
 *      - Look at Protocol Buffers for Serialization (or Apache Thrift)
 *
 */

@ChannelHandler.Sharable
public class MMQServerChannelHandler extends ChannelInboundHandlerAdapter {
  private static final Logger log = LogManager.getLogger(MMQServerChannelHandler.class);
  private final MMQServer mmqServer;

  public MMQServerChannelHandler(MMQServer mmqServer) {
    this.mmqServer = mmqServer;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("Accepting client connection");
  }

  //@Override
  //public void channelInactive()
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("Closing channel connection");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {

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