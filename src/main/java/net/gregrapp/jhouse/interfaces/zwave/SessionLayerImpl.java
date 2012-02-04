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
//          Revision:         $Revision: 1.7 $
//          Last Changed:     $Date: 2007/02/15 11:34:47 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class SessionLayerImpl implements SessionLayer, FrameLayerAsyncCallback
{
  private static final int DEFAULT_TIMEOUT = 10000; // How long in ms to wait
                                                    // for an response

  private static final Logger logger = LoggerFactory
      .getLogger(SessionLayerImpl.class);

  private static final int MAX_SEQUENCE_NUMBER = 127;
  // ---- Note ----
  // Sequence numbers are appended to the data frame at the _last_ position in
  // the payload,
  // just before the checksum byte.
  // Not all requests use sequence numbers
  // The response from the ZW-module sets the sequence number at the _first_
  // position in
  // the payload, just after the command ID
  private static final int MIN_SEQUENCE_NUMBER = 1;
  private boolean _isReady;
  private SessionLayerAsyncCallback asyncCallback;
  private DataFrame.CommandType command;
  private FrameLayer frameLayer;
  private DataFrame lastDataFrame = null;
  private BlockingQueue<DataPacket> queue;
  private DataPacket request;
  private int sequenceNumber = MIN_SEQUENCE_NUMBER;
  private SessionStatistics stats;

  public SessionLayerImpl(FrameLayer frameLayer)
  {
    logger.info("Instantiating session layer");
    this.frameLayer = frameLayer;
    frameLayer.setCallbackHandler(this);
    queue = new LinkedBlockingQueue<DataPacket>();
    stats = new SessionStatistics();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayer#close()
   */
  @Override
  public void destroy()
  {
    logger.debug("Destroying session layer");
    frameLayer.destroy();
  }

  public void frameReceived(DataFrame frame)
  {
    if (frame == null)
    {
      logger.warn("Null frame received");
      throw new NullPointerException("frame");
    }
    // Called from FrameLayer thread scope...
    synchronized (stats)
    {
      stats.receivedPackets++;
    }

    if (frame.getCommand() == command)
    {
      // The peer ZWave module could be tranmitting the same frame twice.
      // so check if the frame was retransmitted from module
      if (lastDataFrame != null && lastDataFrame.equals(frame))
      {
        synchronized (stats)
        {
          stats.duplicatePackets++;
        }
        logger.info("Duplicate frame received");
        return;
      }
      lastDataFrame = frame;
      DataPacket dp;
      // Check and extract (strip) sequence number from frame...
      if (request.getSequenceNumber() > 0
          && frame.getFrameType() == DataFrame.FrameType.Request)
      {
        logger.trace("Stripping sequence number [{}] from payload",
            request.getSequenceNumber());
        int[] payload = frame.getPayloadBuffer();
        int[] data = new int[payload.length - 1];
        System.arraycopy(payload, 1, data, 0, data.length);
        dp = new DataPacket(data);
        dp.setTimestamp(frame.getTimestamp());
        dp.setSequenceNumber(payload[0]);
      } else
      {
        logger.trace("Frame does not contain a sequence number");
        dp = new DataPacket(frame.getPayloadBuffer());
        dp.setTimestamp(frame.getTimestamp());
      }
      // Put the DataPacket in the Queue...
      logger.debug(
          "Adding data packet to multiple response queue for command [{}]",
          frame.getCommand().toString());
      queue.add(dp);
    } else if (asyncCallback != null)
    {
      logger.debug("Passing data packet to session layer callback handler");
      DataPacket dp = new DataPacket(frame.getPayloadBuffer());
      dp.setTimestamp(frame.getTimestamp());
      asyncCallback.dataPacketReceived(frame.getCommand(), dp);
      synchronized (stats)
      {
        stats.asyncPackets++;
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayer#getStatistics()
   */
  public SessionStatistics getStatistics()
  {
    logger.debug("Getting session layer statistics");
    synchronized (stats)
    {
      return new SessionStatistics(stats);
    }
  }

  private int incrementSequenceNumber()
  {
    int oldSequenceNumber = sequenceNumber;
    sequenceNumber++;

    // Test if sequence number should wrap-around...
    if (sequenceNumber >= MAX_SEQUENCE_NUMBER)
      sequenceNumber = MIN_SEQUENCE_NUMBER;

    logger.trace("Incrementing sequence number from [{}] to [{}] ",
        oldSequenceNumber, sequenceNumber);
    return sequenceNumber;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayer#isReady()
   */
  public boolean isReady()
  {
    return _isReady;
  }

  public TXStatus requestWithMultipleResponses(CommandType cmd,
      DataPacket request, int maxResponses) throws FrameLayerException
  {
    if (request == null)
    {
      throw new NullPointerException("request");
    }
    return requestWithMultipleResponses(cmd, request, maxResponses, false,
        DEFAULT_TIMEOUT);
  }

  public TXStatus requestWithMultipleResponses(CommandType cmd,
      DataPacket request, int maxResponses, boolean sequenceCheck)
      throws FrameLayerException
  {
    if (request == null)
    {
      throw new NullPointerException("request");
    }
    return requestWithMultipleResponses(cmd, request, maxResponses,
        sequenceCheck, DEFAULT_TIMEOUT);
  }

  public TXStatus requestWithMultipleResponses(CommandType cmd,
      DataPacket request, int maxResponses, boolean sequenceCheck, int timeout)
      throws FrameLayerException
  {
    logger.debug(
        "New request submitted expecting multiple responses for command [{}]",
        cmd.toString());

    if (request == null)
    {
      throw new NullPointerException("request");
    }
    if (maxResponses < 1)
    {
      throw new IllegalArgumentException(String.format("maxResponses - %d",
          maxResponses));
    }
    setReady(false);
    synchronized (this)
    {
      if (sequenceCheck)
        request.setSequenceNumber(incrementSequenceNumber());

      lastDataFrame = null;
      queue.clear();

      // Store the command...
      this.command = cmd;

      // Store the request...
      this.request = request;

      requestWithNoResponse(cmd, request);

      DataPacket[] responses = new DataPacket[maxResponses];

      // Wait for response from peer or timeout...
      for (int i = 0; i < maxResponses; i++)
      {
        logger.debug("Waiting for response [timeout={}]", timeout);
        try
        {
          responses[i] = queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (responses[i] != null)
        {
          logger.debug("Response {} out of a max of {} received", i + 1,
              maxResponses);

          // Strip the sequence number if used by the request
          // The sequence number is placed as the first byte in the payload
          if (sequenceCheck
              && responses[i].getSequenceNumber() > 0
              && responses[i].getSequenceNumber() != request
                  .getSequenceNumber())
          {
            logger
                .warn("Response sequence number does not match request, aborting");
            setReady(true);
            TXStatus txStatus = TXStatus.ResMissing;
            txStatus.setResponses(responses);
            return txStatus;
          }
        } else
        {
          logger.debug("No responses received before timeout");
          synchronized (stats)
          {
            stats.receiveTimeouts++;
          }
          setReady(true);
          return TXStatus.NoAcknowledge;
        }
      } // for
      setReady(true);
      TXStatus txStatus = TXStatus.CompleteOk;
      txStatus.setResponses(responses);
      return txStatus;
    } // lock
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithNoResponse
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket)
   */
  public boolean requestWithNoResponse(CommandType cmd, DataPacket request)
      throws FrameLayerException
  {
    logger.debug("New request submitted for command [{}]", cmd.toString());
    try
    {
      if (request == null)
      {
        throw new NullPointerException("request");
      }
      synchronized (this)
      {
        logger.debug("Constructing data frame");
        // Construct and setup the new data frame...
        DataFrame frame = new DataFrame();
        frame.setFrameType(DataFrame.FrameType.Request);
        frame.setCommand(cmd);
        // Add the data payload...
        frame.addPayload(request.getPayload());
        // Check if the sequence number should be appended at the last payload
        // position...
        if (request.getSequenceNumber() > 0)
          logger.trace("Adding sequence number [{}] to data frame",
              request.getSequenceNumber());
        frame.addPayload(request.getSequenceNumber());
        stats.transmittedPackets++;
        return frameLayer.write(frame);
      }
    } catch (FrameLayerException e)
    {
      throw new FrameLayerException("Error in requestWithNoRepsonse", e);
    }
  }

  public TXStatus requestWithResponse(CommandType cmd, DataPacket request)
      throws FrameLayerException
  {
    if (request == null)
    {
      throw new NullPointerException("request");
    }
    return requestWithResponse(cmd, request, false, DEFAULT_TIMEOUT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithResponse(net
   * .gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket, boolean)
   */
  public TXStatus requestWithResponse(CommandType cmd, DataPacket request,
      boolean sequenceCheck) throws FrameLayerException
  {
    if (request == null)
    {
      throw new NullPointerException("request");
    }

    return requestWithResponse(cmd, request, sequenceCheck, DEFAULT_TIMEOUT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithResponse(net
   * .gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket, boolean, int)
   */
  public TXStatus requestWithResponse(CommandType cmd, DataPacket request,
      boolean sequenceCheck, int timeout) throws FrameLayerException
  {
    logger.debug(
        "New request submitted expecting a single response for command [{}]",
        cmd.toString());
    if (request == null)
    {
      throw new NullPointerException("request");
    }

    setReady(false);
    synchronized (this)
    {
      if (sequenceCheck)
        request.setSequenceNumber(incrementSequenceNumber());

      lastDataFrame = null;
      queue.clear();

      // Store the command...
      this.command = cmd;

      // Store the request...
      this.request = request;

      requestWithNoResponse(cmd, request);

      DataPacket response = null;

      logger.debug("Waiting for response [timeout={}]", timeout);
      // Wait for reponse from peer or timeout...
      try
      {
        response = queue.poll(timeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (response != null)
      {
        logger.debug("Response received");
        this.command = CommandType.None;

        // Sequence number check...
        if (sequenceCheck
            && response.getSequenceNumber() != request.getSequenceNumber())
        {
          logger
              .warn("Response sequence number does not match request, aborting");
          setReady(true);
          TXStatus txStatus = TXStatus.ResMissing;
          txStatus.setResponse(response);
          return txStatus;
        }
      } else
      {
        logger.debug("No response received before timeout");
        synchronized (stats)
        {
          stats.receiveTimeouts++;
        }
        setReady(true);
        return TXStatus.NoAcknowledge;
      }
      setReady(true);
      TXStatus txStatus = TXStatus.CompleteOk;
      txStatus.setResponse(response);
      return txStatus;
    } // lock
  }

  public TXStatus requestWithVariableResponses(CommandType cmd,
      DataPacket request, int maxResponses, int[] breakVal,
      boolean sequenceCheck, int timeout) throws FrameLayerException
  {
    logger
        .debug(
            "New request submitted expecting a variable number of responses for command [{}]",
            cmd.toString());

    if (request == null)
    {
      throw new NullPointerException("request");
    }
    if (maxResponses < 1)
    {
      throw new IllegalArgumentException("maxResponses");
    }

    setReady(false);
    synchronized (this)
    {
      if (sequenceCheck)
        request.setSequenceNumber(incrementSequenceNumber());

      lastDataFrame = null;
      queue.clear();

      // Store the command...
      this.command = cmd;

      // Store the request...
      this.request = request;

      requestWithNoResponse(cmd, request);

      DataPacket[] responses = new DataPacket[maxResponses];

      // Wait for response from peer or timeout...
      for (int i = 0; i < maxResponses; i++)
      {
        logger.debug("Waiting for response [timeout={}]", timeout);
        try
        {
          responses[i] = queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (responses[i] != null)
        {
          logger.debug("Response received");
          // Strip the sequence number if used by the request
          // The sequence number is placed as the first byte in the payload
          if (sequenceCheck
              && responses[i].getSequenceNumber() > 0
              && responses[i].getSequenceNumber() != request
                  .getSequenceNumber())
          {
            logger
                .warn("Response sequence number does not match request, aborting");
            setReady(true);
            TXStatus txStatus = TXStatus.ResMissing;
            txStatus.setResponses(responses);
            return txStatus;
          }
          if (i > 0)
          {
            for (int n = 0; n < breakVal.length; n++)
            {
              if (responses[i].getPayload()[0] == breakVal[n])
              {
                logger.debug("Break value found in response, aborting");
                TXStatus txStatus = TXStatus.CompleteFail;
                txStatus.setResponses(responses);
                return txStatus;
              }
            }
          }
        } else
        {
          logger.debug("No response received before timeout");
          synchronized (stats)
          {
            stats.receiveTimeouts++;
          }
          setReady(true);
          return TXStatus.NoAcknowledge;
        }
      } // for
      setReady(true);
      TXStatus txStatus = TXStatus.CompleteOk;
      txStatus.setResponses(responses);
      return txStatus;
    } // lock
  }

  public TXStatus requestWithVariableReturnsAndResponses(CommandType cmd,
      DataPacket request, int maxResponses, int[] breakVal,
      boolean sequenceCheck, int timeout) throws FrameLayerException
  {
    logger
        .debug(
            "New request submitted expecting a variable number of returns and responses for command [{}]",
            cmd.toString());
    if (request == null)
    {
      throw new NullPointerException("request");
    }
    if (maxResponses < 1)
    {
      throw new IllegalArgumentException("maxResponses");
    }
    setReady(false);
    synchronized (this)
    {
      if (sequenceCheck)
        request.setSequenceNumber(incrementSequenceNumber());

      lastDataFrame = null;
      queue.clear();

      // Store the command...
      this.command = cmd;

      // Store the request...
      this.request = request;

      requestWithNoResponse(cmd, request);

      DataPacket[] responses = new DataPacket[maxResponses];

      // Wait for reponse from peer or timeout...
      for (int i = 0; i < maxResponses; i++)
      {
        logger.debug("Waiting for response [timeout={}]", timeout);
        try
        {
          responses[i] = queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (responses[i] != null)
        {
          // Strip the sequence number if used by the request
          // The sequence number is placed as the first byte in the payload
          if (sequenceCheck
              && responses[i].getSequenceNumber() > 0
              && responses[i].getSequenceNumber() != request
                  .getSequenceNumber())
          {
            logger
                .warn("Response sequence number does not match request, aborting");
            setReady(true);
            TXStatus txStatus = TXStatus.ResMissing;
            txStatus.setResponses(responses);
            return txStatus;
          }
          if (i >= 0)
          {
            for (int n = 0; n < breakVal.length; n++)
            {
              if (responses[i].getPayload()[0] == breakVal[n])
              {
                logger.debug("Break value found in response, aborting");
                TXStatus txStatus = TXStatus.CompleteFail;
                txStatus.setResponses(responses);
                return txStatus;
              }
            }
          }
        } else
        {
          logger.debug("No response received before timeout");
          synchronized (stats)
          {
            stats.receiveTimeouts++;
          }
          setReady(true);

          return TXStatus.NoAcknowledge;
        }
      } // for
      setReady(true);

      TXStatus txStatus = TXStatus.CompleteOk;
      txStatus.setResponses(responses);
      return txStatus;
    } // lock
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#setCallbackHandler(net
   * .gregrapp.jhouse.interfaces.zwave.SessionLayerAsyncCallback)
   */
  public void setCallbackHandler(SessionLayerAsyncCallback handler)
  {
    logger.debug("Callback handler set");
    this.asyncCallback = handler;
  }

  // / <summary>
  // /
  // / </summary>
  public void setReady(boolean value)
  {
    _isReady = value;
  }
}
