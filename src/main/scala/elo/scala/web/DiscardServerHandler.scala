package elo.scala.web

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

/**
  * Handles a server-side channel.
  */
class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = { // (2)
    // Discard the received data silently.
//    ((ByteBuf) msg).release(); // (3)
    msg.asInstanceOf[ByteBuf].release()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) { // (4)
    // Close the connection when an exception is raised.
    cause.printStackTrace()
    ctx.close()
  }
}
