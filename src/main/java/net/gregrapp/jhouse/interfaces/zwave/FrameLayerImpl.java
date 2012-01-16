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

import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class FrameLayerImpl implements FrameLayer
{
  private static final Logger logger = LoggerFactory
      .getLogger(FrameLayerImpl.class);

  // / <summary>
  // / The Data Frame received will be splitted up into the following states
  // / </summary>
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
  private static final int MAX_FRAME_SIZE = 88;
  // an ack
  private static final int MAX_RETRANSMISSION = 3;
  private static final int MIN_FRAME_SIZE = 3;
  private boolean active;
  private FrameLayerAsyncCallback callbackHandler;
  private DataFrame currentDataFrame;
  private FrameReceiveState parserState;
  private Thread receiveThread;
  private Stack<TransmittedDataFrame> retransmissionStack = new Stack<TransmittedDataFrame>();
  private ScheduledExecutorService retransmissionTimeoutExecutor;

  // private boolean disposed;

  // private ResourceManager resourceManager = new
  // ResourceManager("ZWave.Properties.Resources",
  // Assembly.GetExecutingAssembly());

  private ScheduledFuture<?> retransmissionTimeoutExecutorFuture;

  private FrameStatistics stats;

  private Transport transport;

  public FrameLayerImpl(Transport transport)
  {
    logger.info("Instantiating frame layer");
    this.transport = transport;

    retransmissionStack = new Stack<TransmittedDataFrame>();
    this.transport = transport;

    this.parserState = FrameReceiveState.FRS_SOF_HUNT;

    stats = new FrameStatistics();

    // TimerCallback tc = new
    // TimerCallback(this.ReTransmissionTimeOutCallbackHandler);
    // this.retransmissionTimeoutTimer = new Timer(tc, this,
    // System.Threading.Timeout.Infinite, System.Threading.Timeout.Infinite);
    this.retransmissionTimeoutExecutor = Executors
        .newSingleThreadScheduledExecutor();

    active = true;

    logger.debug("Creating receive thread");
    // Start the communication receive thread
    receiveThread = new Thread(new Runnable()
    {
      public void run()
      {
        receiveThread();
      }
    });
    receiveThread.setPriority(Thread.MAX_PRIORITY);
    logger.debug("Starting receive thread");
    receiveThread.start();
    // Wait for the thread to actually get spun up
    try
    {
      synchronized (this)
      {
        logger.debug("Waiting for receive thread to start");
        this.wait();
      }
    } catch (InterruptedException e)
    {
      logger.error("Error waiting for receive thread to start", e);
    }
  }

  private boolean checkRetransmission(boolean isRetry)
  {
    logger.debug("{} frames awaiting retransmission", retransmissionStack.size());

    try
    {
      synchronized (this)
      {
        if (retransmissionStack.size() > 0)
        {
          TransmittedDataFrame tdf = (TransmittedDataFrame) retransmissionStack
              .peek();

          logger.debug("Retransmitting frame for command [{}]", tdf.frame.getCommand().toString());

          // Transmit the frame to the peer...
          transport.write(tdf.frame.getFrameBuffer());
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
              logger.warn("Retransmission limit reached, dropping frame for command [{}]", tdf.frame.getCommand().toString());
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
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.FrameLayer#open(net.gregrapp.jhouse
   * .transports.Transport)
   */
  /*
   * public void open(Transport transport) { if (transport == null) { throw new
   * NullPointerException("transportLayer"); }
   * 
   * retransmissionStack = new Stack<TransmittedDataFrame>(); this.transport =
   * transport;
   * 
   * this.parserState = FrameReceiveState.FRS_SOF_HUNT;
   * 
   * stats = new FrameStatistics();
   * 
   * // TimerCallback tc = new //
   * TimerCallback(this.ReTransmissionTimeOutCallbackHandler); //
   * this.retransmissionTimeoutTimer = new Timer(tc, this, //
   * System.Threading.Timeout.Infinite, System.Threading.Timeout.Infinite);
   * this.retransmissionTimeoutExecutor = Executors
   * .newSingleThreadScheduledExecutor();
   * 
   * active = true;
   * 
   * // Start the communication receive thread receiveThread = new Thread(new
   * Runnable() { public void run() { receiveThread(); } });
   * receiveThread.setPriority(Thread.MAX_PRIORITY); receiveThread.start(); //
   * //// wait for the thread to actually get spun up try { this.wait(); } catch
   * (InterruptedException e) { // TODO Auto-generated catch block
   * e.printStackTrace(); } }
   */
  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#close()
   */
  public void close()
  {
    logger.info("Closing frame layer");
    active = false;
    if (retransmissionTimeoutExecutor != null)
      retransmissionTimeoutExecutor.shutdown();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#enableTracing(boolean)
   */
  public void enableTracing(boolean enable)
  {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#getStatistics()
   */
  public FrameStatistics getStatistics()
  {
    logger.debug("Getting frame layer statistics");
    synchronized (stats)
    {
      return new FrameStatistics(stats);
    }

  }
  
  private boolean parseRawData(int buffer)
  {
    //logger.trace("Parsing raw frame byte [{}]", String.format("%#04x", buffer));
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
        
        // Do noting... just wait for the retransmission timer to kick-in
        // CheckRetransmission(false);
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
        logger.debug("Frame type byte received [{}]", currentDataFrame.getFrameType().toString());
        parserState = FrameReceiveState.FRS_COMMAND;
      } else
      {
        logger.warn("Invalid frame type byte received [{}]", String.format("%#04x", buffer));
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      }
    } else if (parserState == FrameReceiveState.FRS_COMMAND)
    {
      logger.debug("Command byte received [{}]", DataFrame.CommandType.getByVal(buffer).toString());
      currentDataFrame.setCommand(DataFrame.CommandType.getByVal(buffer));
      if (currentDataFrame.isPayloadFull())
        parserState = FrameReceiveState.FRS_CHECKSUM;
      else
        parserState = FrameReceiveState.FRS_DATA;
    } else if (parserState == FrameReceiveState.FRS_DATA)
    {
      logger.trace("Payload byte received [{}]", String.format("%#04x", buffer));
      if (!currentDataFrame.addPayload(buffer))
      {
        logger.warn("Error parsing payload byte [{}]", String.format("%#04x", buffer));
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      }
      else if (currentDataFrame.isPayloadFull())
        parserState = FrameReceiveState.FRS_CHECKSUM;
    } else if (parserState == FrameReceiveState.FRS_CHECKSUM)
    {
      logger.debug("Checksum byte received [{}]", String.format("%#04x", buffer));

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
      logger.warn("RX timeout, aborting frame parsing [{}]", String.format("%#04x", buffer));
      parserState = FrameReceiveState.FRS_SOF_HUNT;
    } else
    {
      logger.warn("Unknown frame parser state, aborting [{}]", String.format("%#04x", buffer));
      parserState = FrameReceiveState.FRS_SOF_HUNT;
    }

    return true;
  }

  private void receiveThread()
  {
    logger.info("Receiver thread started");
    try
    {
      synchronized (this)
      {
        this.notify();
      }
      int[] buffer = new int[100];

      logger.debug("Starting receiver thread loop");
      while (active)
      {
        int bytesRead = transport.read(buffer);
        for (int i = 0; i < bytesRead; i++)
          if (!parseRawData(0xFF & buffer[i]))
            break;
      }
    } catch (Exception e)
    {
      logger.error("Error in receive thread", e);
    } 
  }

  private void resetRetransmissionTimeoutTimer()
  {
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
  }

  private void retransmissionTimeOutCallbackHandler()
  {
    logger.debug("Retransmission callback handler called");
    checkRetransmission(true);
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
    logger.debug("Callback handler set");
    this.callbackHandler = handler;
  }

  private void transmitACK()
  {
    logger.debug("Transmitting ACK");
    try
    {
      transport
          .write(new int[] { (int) DataFrame.HeaderType.Acknowledge.get() });
    } catch (TransportException e)
    {
      logger.error("Error transmitting ACK", e);
    }
    synchronized (stats)
    {
      stats.transmittedAcks++;
    }
  }

  private void transmitNAK()
  {
    logger.debug("Transmitting NAK");
    
    try
    {
      transport.write(new int[] { (int) DataFrame.HeaderType.NotAcknowledged
          .get() });
    } catch (TransportException e)
    {
      logger.error("Error transmitting NAK");
    }
    synchronized (stats)
    {
      stats.transmittedNaks++;
    }
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
    try
    {
      logger.debug("Writting frame to transport");
      TransmittedDataFrame tdf = new TransmittedDataFrame(frame);
      int bytesWritten = 0;
      synchronized (this)
      {
        retransmissionStack.push(tdf);

        // Transmit the frame to the peer...
        int[] data = frame.getFrameBuffer();
        bytesWritten = transport.write(data);
        logger.debug("{} bytes written to transport", bytesWritten);
        stats.transmittedFrames++;
        // Reset the retransmission timer...
        resetRetransmissionTimeoutTimer();

        return bytesWritten == data.length;
      }
    } catch (TransportException e)
    {
      throw new FrameLayerException("Error writing data to transport: "
          + e.getLocalizedMessage());
    }
  }

}
