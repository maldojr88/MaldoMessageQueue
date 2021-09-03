package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.MMQServer;

@ChannelHandler.Sharable
public class MMQClientHandler extends SimpleChannelInboundHandler<ByteBuf>{
  private static final Logger log = LogManager.getLogger(MMQClientHandler.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    ctx.writeAndFlush(Unpooled.copiedBuffer("Connection established!", CharsetUtil.UTF_8));
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
    log.info("Client received: " + in.toString(CharsetUtil.UTF_8));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}