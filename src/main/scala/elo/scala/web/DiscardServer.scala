package elo.scala.web

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption, EventLoopGroup}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

import scala.util.{Try, Success, Failure}

/**
  * Discards any incoming data.
  */
class DiscardServer private(val port: Int) {

  def run(): Try[ServerBootstrap] = {
    val bossGroup = new NioEventLoopGroup() // (1)
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap(); // (2)
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel]) // (3)
        .childHandler(new ChannelInitializer[SocketChannel]() { // (4)
          def initChannel(ch: SocketChannel): Unit = {
           ch.pipeline().addLast(new DiscardServerHandler())
          }
        })
        .option(ChannelOption.SO_BACKLOG, 128)          // (5)
        .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      // Bind and start to accept incoming connections.
      val f: ChannelFuture = b.bind(port).sync() // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel().closeFuture().sync()
      Success(b)
    } catch {
      case ex: Exception =>
        Failure(ex)
    } finally {
      try {
        workerGroup.shutdownGracefully()
      } finally {
        bossGroup.shutdownGracefully()
      }
    }
  }

}

object DiscardServer {

  def apply(port: Int): Try[DiscardServer] = {
    if(port < 1024)
      Success(new DiscardServer(port))
    else
      Failure(new Exception("Invalid port"))
  }

  def doSmthMeaningless(server: Try[ServerBootstrap]): Try[ServerBootstrap] =
    server.map(_ => _)

  def logServerStatus(server: Try[ServerBootstrap]): Unit = server match {
    case Success(_) => println("Server started")
    case Failure(ex) => println("error: " + ex)
  }

  def main(args: Array[String]): Unit = {
    val port =
      if (args.length > 0)
        try { args(0).toInt } catch { case _: Exception => 8080}
      else 8080

    val discardServerTry = DiscardServer(port)
    val myServer: Try[ServerBootstrap] = discardServerTry.flatMap(_.run())

    val mynewserver: Try[ServerBootstrap] = for {
      discardServer <- DiscardServer(port)
      myServer <- discardServer.run()
    } yield myServer

    logServerStatus(doSmthMeaningless(myServer))
  }
}
