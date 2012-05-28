//////////////////////////////////////////////////////////////////////////////////////////////// 
//
//          #######
//          #   ##    ####   #####    #####  ##  ##   #####
//             ##    ##  ##  ##  ##  ##      ##  ##  ##
//            ##  #  ######  ##  ##   ####   ##  ##   ####
//           ##  ##  ##      ##  ##      ##   #####      ##
//          #######   ####   ##  ##  #####       ##  #####
//                                           #####
//          Z-Wave, the wireless language.
//
//          Copyright Zensys A/S, 2005
//
//          All Rights Reserved
//
//          Description:   
//
//          Author:   Morten Damsgaard, Linkage A/S
//
//          Last Changed By:  $Author: jrm $
//          Revision:         $Revision: 1.6 $
//          Last Changed:     $Date: 2007/02/15 11:34:47 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.net.InetSocketAddress;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class FrameLayerImpl implements FrameLayer, SocketCallback
{
  /**
   * The Data Frame received will be split up into the following states
   * 
   * @author Greg Rapp
   * 
   */
  private enum FrameReceiveState
  {
    FRS_CHECKSUM(0x05), FRS_COMMAND(0x03), FRS_DATA(0x04), FRS_LENGTH(0x01), FRS_RX_TIMEOUT(
        0x06), FRS_SOF_HUNT(0x00), FRS_TYPE(0x02);

    private int value;

    FrameReceiveState(int value)
    {
      this.value = value;
    }

    @SuppressWarnings("unused")
    public int get()
    {
      return this.value;
    }
  }

  private class TransmittedDataFrame
  {
    public DataFrame frame;

    public int retries;

    public TransmittedDataFrame(DataFrame frame)
    {
      this.frame = frame;
    }
  }

  private static final int ACK_WAIT_TIME = 2000; // How long in ms to wait for
                                                 // an ack

  private static final XLogger logger = XLoggerFactory
      .getXLogger(FrameLayerImpl.class);
  
  private static final int MAX_FRAME_SIZE = 88;
  
  private static final int MAX_RETRANSMISSION = 3;
  
  private static final int MIN_FRAME_SIZE = 3;
  
  // Reconnect seconds when the server sends nothing
  private static final int READ_TIMEOUT = 45;
  
  // Delay before a socket reconnection attempt
  static final int RECONNECT_DELAY = 10;
  
  private final ClientBootstrap bootstrap = null;
  
  private FrameLayerAsyncCallback callbackHandler;
  
  private DataFrame currentDataFrame;
  
  private FrameReceiveState parserState;

  private Stack<TransmittedDataFrame> retransmissionStack = new Stack<TransmittedDataFrame>();

  private ScheduledExecutorService retransmissionTimeoutExecutor;

  private ScheduledFuture<?> retransmissionTimeoutExecutorFuture;

  private Channel socketChannel;

  private final Timer socketReconnectTimer;

  private FrameStatistics stats;

  public FrameLayerImpl(String host, int port)
  {
    logger.entry();
    
    logger.info("Instantiating frame layer");

    retransmissionStack = new Stack<TransmittedDataFrame>();

    this.parserState = FrameReceiveState.FRS_SOF_HUNT;

    stats = new FrameStatistics();

    this.retransmissionTimeoutExecutor = Executors
        .newSingleThreadScheduledExecutor();

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
          socketReconnectTimer, FrameLayerImpl.this);
      private final ChannelHandler timeoutHandler = new ReadTimeoutHandler(
          socketReconnectTimer, READ_TIMEOUT);

      public ChannelPipeline getPipeline() throws Exception
      {
        return Channels.pipeline(timeoutHandler, socketHandler);
      }
    });

    // Start the connection attempt.
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

  @Override
  public void byteReceived(int data)
  {
    logger.entry(data);

    logger.debug("Byte received");
    parseRawData(0xFF & data);

    logger.exit();
  }

  private boolean checkRetransmission(boolean isRetry)
  {
    logger.entry(isRetry);
    logger.debug("{} frames awaiting retransmission",
        retransmissionStack.size());

    try
    {
      synchronized (this)
      {
        if (retransmissionStack.size() > 0)
        {
          TransmittedDataFrame tdf = (TransmittedDataFrame) retransmissionStack
              .peek();

          logger.debug("Retransmitting frame for command [{}]", tdf.frame
              .getCommand().toString());

          // Transmit the frame to the peer...
          int[] frame = tdf.frame.getFrameBuffer();
          this.writeBytes(frame);

          if (isRetry)
          {
            synchronized (stats)
            {
              stats.transmittedFrames++;
              stats.retransmittedFrames++;
            }

            // Drop the frame if retried too many times
            if (++tdf.retries >= MAX_RETRANSMISSION)
            {
              logger
                  .warn(
                      "Retransmission limit reached, dropping frame for command [{}]",
                      tdf.frame.getCommand().toString());
              retransmissionStack.pop();
              synchronized (stats)
              {
                stats.droppedFrames++;
              }
            }
          }
        }

        if (retransmissionStack.size() > 0)
        {
          resetRetransmissionTimeoutTimer();
        } else
        {
          // retransmissionTimeoutExecutor.cancel();
        }
      } // lock
    } catch (Exception e)
    {
      logger.error("Error during retransmittion operation", e);
    }

    logger.exit(true);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#close()
   */
  public void destroy()
  {
    logger.entry();
    logger.debug("Destroying frame layer");

    if (retransmissionTimeoutExecutorFuture != null)
    {
      logger.debug("Canceling retransmission timeout executor future");
      retransmissionTimeoutExecutorFuture.cancel(true);
    }

    if (retransmissionTimeoutExecutor != null)
    {
      logger.debug("Shutting down retransmission timeout executor");
      retransmissionTimeoutExecutor.shutdownNow();
    }

    // Shut down socket connection
    bootstrap.releaseExternalResources();

    // Shut down socket reconnect timer
    socketReconnectTimer.stop();

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#getStatistics()
   */
  public FrameStatistics getStatistics()
  {
    logger.entry();

    logger.debug("Getting frame layer statistics");
    synchronized (stats)
    {
      logger.exit();
      return new FrameStatistics(stats);
    }
  }

  private boolean parseRawData(int buffer)
  {
    logger.entry(buffer);

    if (parserState == FrameReceiveState.FRS_SOF_HUNT)
    {
      if (DataFrame.HeaderType.StartOfFrame == DataFrame.HeaderType
          .getByVal(buffer))
      {
        logger.debug("SOF byte received");
        parserState = FrameReceiveState.FRS_LENGTH;
      } else if (DataFrame.HeaderType.Acknowledge == DataFrame.HeaderType
          .getByVal(buffer))
      {
        logger.debug("ACK byte received");

        // Acknowledge received from peer
        // Remove the last frame from the retransmission stack
        synchronized (stats)
        {
          stats.receivedAcks++;
        }
        synchronized (this)
        {
          if (retransmissionStack.size() > 0)
            retransmissionStack.pop();
        }
      } else if (DataFrame.HeaderType.NotAcknowledged == DataFrame.HeaderType
          .getByVal(buffer))
      {
        logger.debug("NAK received");
        // Not Acknowledge received from peer
        synchronized (stats)
        {
          stats.receivedNaks++;
        }
        checkRetransmission(true);
      } else if (DataFrame.HeaderType.Can == DataFrame.HeaderType
          .getByVal(buffer))
      {
        logger.debug("CAN received");

        // CAN frame received - peer dropped a data frame transmitted by us
        synchronized (stats)
        {
          stats.droppedFrames++;
        }
      }
    } else if (parserState == FrameReceiveState.FRS_LENGTH)
    {
      if (buffer < MIN_FRAME_SIZE || buffer > MAX_FRAME_SIZE)
      {
        logger.warn("Frame length byte is not valid, aborting frame");
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      } else
      {
        logger.debug("Frame length byte received [{}]", buffer);

        currentDataFrame = new DataFrame((int) (buffer - 3)); // Payload size is
                                                              // excluding len,
                                                              // type & cmd
                                                              // field
        parserState = FrameReceiveState.FRS_TYPE;
      }
    } else if (parserState == FrameReceiveState.FRS_TYPE)
    {
      currentDataFrame.setFrameType(DataFrame.FrameType.getByVal(buffer));
      if (currentDataFrame.getFrameType() == DataFrame.FrameType.Request
          || currentDataFrame.getFrameType() == DataFrame.FrameType.Response)
      {
        logger.debug("Frame type byte received [{}]", currentDataFrame
            .getFrameType().toString());
        parserState = FrameReceiveState.FRS_COMMAND;
      } else
      {
        logger.warn("Invalid frame type byte received [{}]",
            String.format("%#04x", buffer));
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      }
    } else if (parserState == FrameReceiveState.FRS_COMMAND)
    {
      logger.debug("Command byte received [{}]", DataFrame.CommandType
          .getByVal(buffer).toString());
      currentDataFrame.setCommand(DataFrame.CommandType.getByVal(buffer));
      if (currentDataFrame.isPayloadFull())
        parserState = FrameReceiveState.FRS_CHECKSUM;
      else
        parserState = FrameReceiveState.FRS_DATA;
    } else if (parserState == FrameReceiveState.FRS_DATA)
    {
      logger
          .trace("Payload byte received [{}]", String.format("%#04x", buffer));
      if (!currentDataFrame.addPayload(buffer))
      {
        logger.warn("Error parsing payload byte [{}]",
            String.format("%#04x", buffer));
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      } else if (currentDataFrame.isPayloadFull())
        parserState = FrameReceiveState.FRS_CHECKSUM;
    } else if (parserState == FrameReceiveState.FRS_CHECKSUM)
    {
      logger.debug("Checksum byte received [{}]",
          String.format("%#04x", buffer));

      if (currentDataFrame.isChecksumValid(buffer))
      {
        logger.debug("Checksum is valid");
        // Frame received successfully -> Send acknowledge (ACK)
        transmitACK();

        logger.debug("Passing frame to frame layer callback handler");
        // Call the callbackhandler with the received frame
        callbackHandler.frameReceived(currentDataFrame);
        synchronized (stats)
        {
          stats.receivedFrames++;
        }

        checkRetransmission(true);
      } else
      {
        logger.warn("Invalid checksum");
        // Frame receive failed -> Send NAK
        transmitNAK();
      }

      parserState = FrameReceiveState.FRS_SOF_HUNT;
    } else if (parserState == FrameReceiveState.FRS_RX_TIMEOUT)
    {
      logger.warn("RX timeout, aborting frame parsing [{}]",
          String.format("%#04x", buffer));
      parserState = FrameReceiveState.FRS_SOF_HUNT;
    } else
    {
      logger.warn("Unknown frame parser state, aborting [{}]",
          String.format("%#04x", buffer));
      parserState = FrameReceiveState.FRS_SOF_HUNT;
    }

    logger.exit(true);
    return true;
  }

  private void resetRetransmissionTimeoutTimer()
  {
    logger.entry();
    logger.debug("Resetting retransmission timeout timer");

    if (retransmissionTimeoutExecutorFuture != null)
      retransmissionTimeoutExecutorFuture.cancel(false);

    retransmissionTimeoutExecutorFuture = retransmissionTimeoutExecutor
        .schedule(new Runnable()
        {
          public void run()
          {
            retransmissionTimeOutCallbackHandler();
          }
        }, ACK_WAIT_TIME, TimeUnit.MILLISECONDS);

    logger.exit();
  }

  private void retransmissionTimeOutCallbackHandler()
  {
    logger.entry();
    logger.debug("Retransmission callback handler called");
    checkRetransmission(true);
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.FrameLayer#setCallbackHandler(net.
   * gregrapp.jhouse.interfaces.zwave.FrameLayerAsyncCallback)
   */
  public void setCallbackHandler(FrameLayerAsyncCallback handler)
  {
    logger.entry(handler);

    logger.debug("Callback handler set");
    this.callbackHandler = handler;

    logger.exit();
  }

  private void transmitACK()
  {
    logger.entry();

    logger.debug("Transmitting ACK");
    this.writeByte(DataFrame.HeaderType.Acknowledge.get());

    synchronized (stats)
    {
      stats.transmittedAcks++;
    }

    logger.exit();
  }

  private void transmitNAK()
  {
    logger.entry();

    logger.debug("Transmitting NAK");
    this.writeByte(DataFrame.HeaderType.NotAcknowledged.get());

    synchronized (stats)
    {
      stats.transmittedNaks++;
    }

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.FrameLayer#write(net.gregrapp.jhouse
   * .interfaces.zwave.DataFrame)
   */
  public boolean write(DataFrame frame) throws FrameLayerException
  {
    logger.entry(frame);

    logger.debug("Writing frame to transport");
    TransmittedDataFrame tdf = new TransmittedDataFrame(frame);

    synchronized (this)
    {
      retransmissionStack.push(tdf);

      // Transmit the frame to the peer...
      int[] data = frame.getFrameBuffer();

      this.writeBytes(data);

      logger.debug("[{}] bytes written to transport", data.length);
      stats.transmittedFrames++;

      // Reset the retransmission timer...
      resetRetransmissionTimeoutTimer();

      logger.exit(true);
      return true;
    }
  }

  private void writeByte(int data)
  {
    logger.entry(data);

    this.writeBytes(new int[] { data });

    logger.exit();
  }

  private void writeBytes(int[] data)
  {
    logger.entry(data);

    ChannelBuffer buf = ChannelBuffers.directBuffer(data.length);
    for (int i = 0; i < data.length; i++)
    {
      buf.writeByte(data[i]);
    }
    if (socketChannel.isConnected())
    {
      socketChannel.write(buf);
    } else
    {
      logger.warn("Unable to write data to socket, not connected");
    }

    logger.exit();
  }
}
