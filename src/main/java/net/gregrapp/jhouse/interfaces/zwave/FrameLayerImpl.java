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

/**
 * @author Greg Rapp
 * 
 */
public class FrameLayerImpl implements FrameLayer
{

  private Transport transport;
  private Thread receiveThread;
  private FrameLayerAsyncCallback callbackHandler;
  private boolean active;
  private ScheduledExecutorService retransmissionTimeoutExecutor;
  private Stack<TransmittedDataFrame> retransmissionStack = new Stack<TransmittedDataFrame>();
  private static final int ACK_WAIT_TIME = 2000; // How long in ms to wait for
                                                 // an ack
  private static final int MAX_RETRANSMISSION = 3;
  private FrameReceiveState parserState;
  private DataFrame currentDataFrame;
  private static final int MAX_FRAME_SIZE = 88;
  private static final int MIN_FRAME_SIZE = 3;
  private FrameStatistics stats;

  // private boolean disposed;

  // private ResourceManager resourceManager = new
  // ResourceManager("ZWave.Properties.Resources",
  // Assembly.GetExecutingAssembly());

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.FrameLayer#open(net.gregrapp.jhouse
   * .transports.Transport)
   */
  public void open(Transport transport)
  {
    if (transport == null)
    {
      throw new NullPointerException("transportLayer");
    }

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

    // Start the communication receive thread
    receiveThread = new Thread(new Runnable()
    {
      public void run()
      {
        receiveThread();
      }
    });
    receiveThread.setPriority(Thread.MAX_PRIORITY);
    receiveThread.start();
    // //// wait for the thread to actually get spun up
    try
    {
      this.wait();
    } catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#close()
   */
  public void close()
  {
    active = false;
    if (retransmissionTimeoutExecutor != null)
      retransmissionTimeoutExecutor.shutdown();
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
      // TODO Implement logging
      // log.Write(resourceManager.GetString("Transmitted") + frame.ToString());
      TransmittedDataFrame tdf = new TransmittedDataFrame(frame);
      int bytesWritten = 0;
      synchronized (this)
      {
        retransmissionStack.push(tdf);
  
        // Transmit the frame to the peer...
        int[] data = frame.getFrameBuffer();
        bytesWritten = transport.write(data);
        stats.transmittedFrames++;
        // Reset the retransmission timer...
        resetRetransmissionTimeoutTimer();
  
        return bytesWritten == data.length;
      }
    } catch (TransportException e)
    {
      throw new FrameLayerException("Error in Transport: " + e.getLocalizedMessage());
    }
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
    this.callbackHandler = handler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.FrameLayer#getStatistics()
   */
  public FrameStatistics getStatistics()
  {
    synchronized (stats)
    {
      return new FrameStatistics(stats);
    }

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

  private class TransmittedDataFrame
  {
    public TransmittedDataFrame(DataFrame frame)
    {
      this.frame = frame;
    }

    public int retries;
    public DataFrame frame;
  }

  private void retransmissionTimeOutCallbackHandler()
  {
    // TODO Implement logging
    // log.Write(resourceManager.GetString("Retransmission"));
    checkRetransmission(true);
  }

  private void transmitACK()
  {
    // TODO Implement logging
    // log.Write(resourceManager.GetString("TransmitAcknowledge"));
    try
    {
      transport.write(new int[] { (int) DataFrame.HeaderType.Acknowledge.get() });
    } catch (TransportException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    synchronized (stats)
    {
      stats.transmittedAcks++;
    }
  }

  private void transmitNAK()
  {
    // TODO Implement logging
    // log.Write(resourceManager.GetString("TransmitNoAcknowledge"));
    try
    {
      transport.write(new int[] { (int) DataFrame.HeaderType.NotAcknowledged
          .get() });
    } catch (TransportException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    synchronized (stats)
    {
      stats.transmittedNaks++;
    }
  }

  private boolean checkRetransmission(boolean isRetry)
  {
    try
    {
      synchronized (this)
      {
        if (retransmissionStack.size() > 0)
        {
          TransmittedDataFrame tdf = (TransmittedDataFrame) retransmissionStack
              .peek();
          // TODO Implement logging
          // log.Write(resourceManager.GetString("CheckRetransmission") +
          // tdf.frame);
          // Transmit the frame to the peer...
          transport.write(tdf.frame.getFrameBuffer());
          if (isRetry)
          {
            synchronized (stats)
            {
              stats.transmittedFrames++;
              stats.retransmittedFrames++;
            }

            // Drop the frame if retried to many times
            if (++tdf.retries >= MAX_RETRANSMISSION)
            {
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
      // TODO Implement logging
      // log.Write(resourceManager.GetString("CheckRetransmissionException"));
      // throw(e);
    }
    return true;
  }

  // / <summary>
  // / The Data Frame received will be splitted up into the following states
  // / </summary>
  private enum FrameReceiveState
  {
    FRS_SOF_HUNT(0x00), FRS_LENGTH(0x01), FRS_TYPE(0x02), FRS_COMMAND(0x03), FRS_DATA(
        0x04), FRS_CHECKSUM(0x05), FRS_RX_TIMEOUT(0x06);

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

  private boolean parseRawData(int buffer)
  {
    if (parserState == FrameReceiveState.FRS_SOF_HUNT)
    {
      if (DataFrame.HeaderType.StartOfFrame == DataFrame.HeaderType
          .getByVal(buffer))
      {
        parserState = FrameReceiveState.FRS_LENGTH;
      } else if (DataFrame.HeaderType.Acknowledge == DataFrame.HeaderType
          .getByVal(buffer))
      {
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
        // TODO Implement logging
        // log.Write(resourceManager.GetString("ReceivedAcknowledge"));
      } else if (DataFrame.HeaderType.NotAcknowledged == DataFrame.HeaderType
          .getByVal(buffer))
      {
        // Not Acknowledge received from peer
        synchronized (stats)
        {
          stats.receivedNaks++;
        }
        checkRetransmission(true);
        // TODO Implement logging
        // log.Write(resourceManager.GetString("ReceivedNoAcknowledge"));
      } else if (DataFrame.HeaderType.Can == DataFrame.HeaderType
          .getByVal(buffer))
      {
        // Do noting... just wait for the retransmission timer to kick-in
        // CheckRetransmission(false);
        // CAN frame received - peer dropped a data frame transmitted by us
        // TODO Implement logging
        // log.Write(resourceManager.GetString("ReceivedCancel"));
        synchronized (stats)
        {
          stats.droppedFrames++;
        }
      }
    } else if (parserState == FrameReceiveState.FRS_LENGTH)
    {
      if (buffer < MIN_FRAME_SIZE || buffer > MAX_FRAME_SIZE)
      {
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      } else
      {
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
        parserState = FrameReceiveState.FRS_COMMAND;
      } else
      {
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      }
    } else if (parserState == FrameReceiveState.FRS_COMMAND)
    {
      currentDataFrame.setCommand(DataFrame.CommandType.getByVal(buffer));
      if (currentDataFrame.isPayloadFull())
        parserState = FrameReceiveState.FRS_CHECKSUM;
      else
        parserState = FrameReceiveState.FRS_DATA;
    } else if (parserState == FrameReceiveState.FRS_DATA)
    {
      if (!currentDataFrame.addPayload(buffer))
        parserState = FrameReceiveState.FRS_SOF_HUNT;
      else if (currentDataFrame.isPayloadFull())
        parserState = FrameReceiveState.FRS_CHECKSUM;
    } else if (parserState == FrameReceiveState.FRS_CHECKSUM)
    {
      if (currentDataFrame.isChecksumValid(buffer))
      {
        // TODO Implement logging
        // log.Write(resourceManager.GetString("Received") + currentDataFrame);
        // Frame received successfully -> Send acknowledge (ACK)
        transmitACK();

        // Call the callbackhandler with the received frame
        callbackHandler.frameReceived(currentDataFrame);
        synchronized (stats)
        {
          stats.receivedFrames++;
        }

        checkRetransmission(true);
      } else
      {
        // Frame receive failed -> Send NAK
        transmitNAK();
      }

      parserState = FrameReceiveState.FRS_SOF_HUNT;
    } else if (parserState == FrameReceiveState.FRS_RX_TIMEOUT)
    {
      parserState = FrameReceiveState.FRS_SOF_HUNT;
    } else
    {
      parserState = FrameReceiveState.FRS_SOF_HUNT;
    }

    return true;
  }

  private void receiveThread()
  {
    try
    {
      this.notify();
      int[] buffer = new int[100];

      while (active)
      {
        int bytesRead = transport.read(buffer);
        for (int i = 0; i < bytesRead; i++)
          if (!parseRawData(buffer[i]))
            break;
      }
    } catch (Exception e)
    {
      // TODO Implement logging
      // log.Write(resourceManager.GetString("ReceiveThreadException"));
    } finally
    {
      // TODO Implement logging
      // log.Write(resourceManager.GetString("ReceiveThreadId") +
      // Thread.CurrentThread.GetHashCode());
    }
  }

  private ScheduledFuture<?> retransmissionTimeoutExecutorFuture;

  private void resetRetransmissionTimeoutTimer()
  {
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

}
