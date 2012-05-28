/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class SocketHandler extends SimpleChannelUpstreamHandler
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(SocketHandler.class);

  private final ClientBootstrap bootstrap;
  private final SocketCallback callback;
  private final Timer timer;

  public SocketHandler(ClientBootstrap bootstrap, Timer timer,
      SocketCallback callback)
  {
    logger.entry(bootstrap, timer, callback);
    this.bootstrap = bootstrap;
    this.timer = timer;
    this.callback = callback;
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelClosed(org.
   * jboss.netty.channel.ChannelHandlerContext,
   * org.jboss.netty.channel.ChannelStateEvent)
   */
  @Override
  public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
      throws Exception
  {
    logger.entry(ctx, e);
    logger.warn("Socket closed [{}:{}]", getRemoteAddress().getHostName(),
        getRemoteAddress().getPort());

    timer.newTimeout(new TimerTask()
    {
      public void run(Timeout timeout) throws Exception
      {
        logger.info("Reconnecting to host [{}:{}]", getRemoteAddress()
            .getHostName(), getRemoteAddress().getPort());

        ChannelFuture future = bootstrap.connect();

        // Wait until the connection attempt succeeds or fails.
        future.awaitUninterruptibly().getChannel();

        if (!future.isSuccess())
        {
          logger.error("Error reconnecting to host [{}:{}]", new Object[] {
              getRemoteAddress().getHostName(), getRemoteAddress().getPort(),
              future.getCause() });
          bootstrap.releaseExternalResources();
          return;
        }
      }
    }, Envisalink2DSFrameLayerImpl.RECONNECT_DELAY, TimeUnit.SECONDS);

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org
   * .jboss.netty.channel.ChannelHandlerContext,
   * org.jboss.netty.channel.ChannelStateEvent)
   */
  @Override
  public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
      throws Exception
  {
    logger.entry(ctx, e);
    logger.info("Connected to host [{}:{}]", getRemoteAddress().getHostName(),
        getRemoteAddress().getPort());
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelDisconnected
   * (org.jboss.netty.channel.ChannelHandlerContext,
   * org.jboss.netty.channel.ChannelStateEvent)
   */
  @Override
  public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
      throws Exception
  {
    logger.entry(ctx, e);
    logger.warn("Disconnected from host [{}:{}]", getRemoteAddress()
        .getHostName(), getRemoteAddress().getPort());
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org
   * .jboss.netty.channel.ChannelHandlerContext,
   * org.jboss.netty.channel.ExceptionEvent)
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
      throws Exception
  {
    logger.entry(ctx, e);
    Throwable cause = e.getCause();
    if (cause instanceof ConnectException)
    {
      logger.error("Connect error", cause);
    }
    if (cause instanceof ReadTimeoutException)
    {
      // The connection was OK but there was no traffic for last period.
      logger.error("Read timeout");
    } else
    {
      logger.error("General error", cause);
      cause.printStackTrace();
    }
    ctx.getChannel().close();
    logger.exit();
  }

  InetSocketAddress getRemoteAddress()
  {
    return (InetSocketAddress) bootstrap.getOption("remoteAddress");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org
   * .jboss.netty.channel.ChannelHandlerContext,
   * org.jboss.netty.channel.MessageEvent)
   */
  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
      throws Exception
  {
    logger.entry(ctx, e);

    logger.debug("Getting message");
    callback.stringReceived((String) e.getMessage());

    logger.exit();
  }

}
