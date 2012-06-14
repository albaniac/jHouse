/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSFrameLayerImpl extends SimpleChannelUpstreamHandler
    implements Envisalink2DSFrameLayer
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(Envisalink2DSFrameLayerImpl.class);

  // Seconds to wait before we reconnect when we haven't received any frames
  private static final int READ_TIMEOUT = 60;

  // Delay before a socket reconnection attempt.
  static final int RECONNECT_DELAY = 10;

  private final ClientBootstrap bootstrap;

  private Envisalink2DSFrameLayerAsyncCallback handler;

  private Channel socketChannel;

  private final Timer socketReconnectTimer;

  /**
   * 
   */
  public Envisalink2DSFrameLayerImpl(String host, int port)
  {
    logger.entry(host, port);

    logger.info("Instantiating frame layer");

    // Initialize the timer that schedules subsequent reconnection attempts.
    socketReconnectTimer = new HashedWheelTimer();

    // Configure the client
    bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

    // Configure the pipeline factory
    bootstrap.setPipelineFactory(new ChannelPipelineFactory()
    {
      public ChannelPipeline getPipeline() throws Exception
      {
        ChannelPipeline pipeline = pipeline();

        final ChannelHandler timeoutHandler = new ReadTimeoutHandler(
            socketReconnectTimer, READ_TIMEOUT);

        // Decoders
        pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(80,
            true, ChannelBuffers.wrappedBuffer(new byte[] { '\r', '\n' })));
        pipeline.addLast("stringDecoder", new StringDecoder());

        // Encoder
        pipeline.addLast("stringEncoder", new StringEncoder());

        // Handlers
        pipeline.addLast("timeoutHandler", timeoutHandler);
        pipeline.addLast("socketHandler", Envisalink2DSFrameLayerImpl.this);

        return pipeline;
      }
    });

    // Configure the remote address and start the connection attempt.
    bootstrap.setOption("remoteAddress", new InetSocketAddress(host, port));
    ChannelFuture future = bootstrap.connect();

    // Wait until the connection is closed or the connection attempt fails.
    socketChannel = future.awaitUninterruptibly().getChannel();

    if (!future.isSuccess())
    {
      logger.error("Error connecting to host", future.getCause());
      bootstrap.releaseExternalResources();
      socketReconnectTimer.stop();
      
      logger.exit();
      return;
    }

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

    socketReconnectTimer.newTimeout(new TimerTask()
    {
      public void run(Timeout timeout) throws Exception
      {
        logger.info("Reconnecting to host [{}:{}]", getRemoteAddress()
            .getHostName(), getRemoteAddress().getPort());

        ChannelFuture future = bootstrap.connect();

        // Wait until the connection attempt succeeds or fails.
        socketChannel = future.awaitUninterruptibly().getChannel();

        if (!future.isSuccess())
        {
          logger.error("Error reconnecting to host [{}:{}]", new Object[] {
              getRemoteAddress().getHostName(), getRemoteAddress().getPort(),
              future.getCause() });
          bootstrap.releaseExternalResources();
          return;
        }
      }
    }, RECONNECT_DELAY, TimeUnit.SECONDS);

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
   * net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#close
   * ()
   */
  @Override
  public void destroy()
  {
    logger.entry();

    logger.debug("Destroying frame layer");

    // Close and disconnect the socket
    socketChannel.close();
    
    // Clean up the socket
    bootstrap.releaseExternalResources();

    // Shut down socket reconnect timer
    socketReconnectTimer.stop();

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

  /**
   * Get the socket's remote address
   * 
   * @return the remote address
   */
  private InetSocketAddress getRemoteAddress()
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
    String message = (String) e.getMessage();

    // Remove all non-printable characters
    message = message.replaceAll("\\p{Cntrl}", "");

    this.stringReceived(message);

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#
   * setCallbackHandler (net.gregrapp.jhouse.interfaces.envisalink2ds.
   * Envisalink2DSFrameLayerAsyncCallback)
   */
  @Override
  public void setCallbackHandler(Envisalink2DSFrameLayerAsyncCallback handler)
  {
    logger.entry();

    logger.debug("Callback handler set to [{}]", handler.getClass().getName());
    this.handler = handler;

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.envisalink2ds.SocketCallback#stringReceived
   * (java.lang.String)
   */
  public void stringReceived(String data)
  {
    logger.entry(data);

    logger.trace("Received data [{}]", data);
    if (data == null)
    {
      logger.error("Null string received");
    } else
    {
      try
      {
        final Envisalink2DSDataFrame frame = new Envisalink2DSDataFrame(data);

        if (this.handler != null)
        {
          if (frame != null && frame.isValidChecksum())
          {
            logger.debug("Creating frame worker thread");
            Thread thread = new Thread(new Runnable()
            {
              @Override
              public void run()
              {
                logger.entry();

                handler.frameReceived(frame);

                logger.exit();
              }
            });

            thread.setDaemon(true);
            logger.debug("Starting frame worker thread");
            thread.start();

          } else
          {
            logger.warn("Invalid data received [{}]", data);
          }
        } else
        {
          logger.warn("Data received [{}] but handler not set", data);
        }

      } catch (Envisalink2DSDataFrameException e)
      {
        logger.warn("Error parsing raw data [{}]", data);
      }
    }

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#write
   * (net.gregrapp .jhouse.interfaces.zwave.DataFrame)
   */
  @Override
  public void write(Envisalink2DSDataFrame frame)
      throws Envisalink2DSFrameLayerException
  {
    logger.entry(frame);

    logger.debug("Writing frame to socket");
    synchronized (this)
    {
      logger.trace("Sending frame [{}]", frame.getFrameNoCrlf());

      if (socketChannel != null && socketChannel.isConnected())
      {
        socketChannel.write(frame.getFrame());
      } else
      {
        logger.warn("Unable to write frame to socket [{}]", frame);
      }
    }

    logger.exit();
  }
}
