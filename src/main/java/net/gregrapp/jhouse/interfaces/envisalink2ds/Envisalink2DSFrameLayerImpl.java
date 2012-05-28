/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSFrameLayerImpl implements Envisalink2DSFrameLayer,
    SocketCallback
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(Envisalink2DSFrameLayerImpl.class);

  // Reconnect seconds when the server sends nothing
  private static final int READ_TIMEOUT = 45;

  // Delay before a socket reconnection attempt.
  static final int RECONNECT_DELAY = 10;

  private final ClientBootstrap bootstrap = null;

  private Envisalink2DSFrameLayerAsyncCallback handler;

  private Channel socketChannel;

  private final Timer socketReconnectTimer;

  /**
   * 
   */
  public Envisalink2DSFrameLayerImpl(String host, int port)
  {
    logger.entry();
    logger.info("Instantiating frame layer");

    // Initialize the timer that schedules subsequent reconnection attempts.
    socketReconnectTimer = new HashedWheelTimer();

    // Configure the client.
    final ClientBootstrap bootstrap = new ClientBootstrap(
        new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()));

    // Configure the pipeline factory.
    bootstrap.setPipelineFactory(new ChannelPipelineFactory()
    {

      private final ChannelHandler socketHandler = new SocketHandler(bootstrap,
          socketReconnectTimer, Envisalink2DSFrameLayerImpl.this);
      private final ChannelHandler timeoutHandler = new ReadTimeoutHandler(
          socketReconnectTimer, READ_TIMEOUT);

      public ChannelPipeline getPipeline() throws Exception
      {
        ChannelPipeline pipeline = pipeline();

        // Decoders
        pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(80,
            Delimiters.lineDelimiter()));
        pipeline.addLast("stringDecoder", new StringDecoder());

        // Encoder
        pipeline.addLast("stringEncoder", new StringEncoder());

        // Handlers
        pipeline.addLast("timeoutHandler", timeoutHandler);
        pipeline.addLast("socketHandler", socketHandler);

        return pipeline;
      }
    });

    // Start the connection attempt.
    bootstrap.setOption("remoteAddress", new InetSocketAddress(host,
        port));
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
   * net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#close
   * ()
   */
  @Override
  public void destroy()
  {
    logger.entry();

    logger.debug("Destroying frame layer");

    // Shut down socket connection
    bootstrap.releaseExternalResources();

    // Shut down socket reconnect timer
    socketReconnectTimer.stop();

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
  @Override
  public void stringReceived(String data)
  {
    logger.entry(data);

    logger.trace("Received data [{}]", data);
    if (data == null)
    {
      logger.error("Null string received");
    } else
    {
      Envisalink2DSDataFrame frame = null;
      try
      {
        frame = new Envisalink2DSDataFrame(data);
      } catch (Envisalink2DSDataFrameException e)
      {
        logger.warn("Error parsing raw data [{}]", data);
      }
      if (this.handler == null)
      {
        logger.warn("Data received [{}] but handler not set", data);
      } else
      {
        if (frame.isValidChecksum())
        {
          this.handler.frameReceived(frame);
        } else
        {
          logger.warn("Invalid data received [{}]", data);
        }
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
    logger.entry();

    logger.debug("Writing frame to transport");
    synchronized (this)
    {
      logger.trace("Sending frame [{}]", frame.getFrameNoCrlf());

      socketChannel.write(frame.getFrame());
    }

    logger.exit();
  }

}
