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

import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;
import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 * 
 */
public class SessionLayerImpl implements SessionLayer, FrameLayerAsyncCallback
{
  private static final int DEFAULT_TIMEOUT = 10000; // How long in ms to wait
                                                    // for an response

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
  private DataPacketQueue queue;
  private DataPacket request;
  private int sequenceNumber = MIN_SEQUENCE_NUMBER;
  private SessionStatistics stats;

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayer#close()
   */
  public void close()
  {
    frameLayer.close();
  }

  public void frameReceived(DataFrame frame)
  {
    if (frame == null)
    {
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
      if (lastDataFrame != null && lastDataFrame == frame)
      {
        synchronized (stats)
        {
          stats.duplicatePackets++;
        }
        // TODO Implement log
        // log.Write("COM - Duplicate packet received...");
        return;
      }
      lastDataFrame = frame;
      DataPacket dp;
      // Check and extract (strip) sequence number from frame...
      if (request.getSequenceNumber() > 0
          && frame.getFrameType() == DataFrame.FrameType.Request)
      {
        int[] payload = frame.getPayloadBuffer();
        int[] data = new int[payload.length - 1];
        System.arraycopy(payload, 1, data, 0, data.length);
        dp = new DataPacket(data);
        dp.setTimestamp(frame.getTimestamp());
        dp.setSequenceNumber(payload[0]);
      } else
      {
        dp = new DataPacket(frame.getPayloadBuffer());
        dp.setTimestamp(frame.getTimestamp());
      }
      // Put the DataPacket in the Queue...
      queue.add(dp);
    } else if (asyncCallback != null)
    {
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
    synchronized (stats)
    {
      return new SessionStatistics(stats);
    }
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#open(net.gregrapp.jhouse
   * .interfaces.zwave.FrameLayer, net.gregrapp.jhouse.transports.Transport)
   */
  public void open(FrameLayer frameLayer, Transport transport)
  {
    if (frameLayer == null)
    {
      throw new NullPointerException("frameLayer");
    }
    this.sequenceNumber = MIN_SEQUENCE_NUMBER;
    this.frameLayer = frameLayer;
    frameLayer.open(transport);
    frameLayer.setCallbackHandler(this);
    queue = new DataPacketQueue();
    stats = new SessionStatistics();
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
        responses[i] = queue.poll(timeout);
        if (responses[i] != null)
        {
          // Strip the sequence number if used by the request
          // The sequence number is placed as the first byte in the payload
          if (sequenceCheck
              && responses[i].getSequenceNumber() > 0
              && responses[i].getSequenceNumber() != request
                  .getSequenceNumber())
          {
            setReady(true);
            TXStatus txStatus = TXStatus.ResMissing;
            txStatus.setResponses(responses);
            return txStatus;
          }
        } else
        {
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
    try
    {
      if (request == null)
      {
        throw new NullPointerException("request");
      }
      synchronized (this)
      {
        // Construct and setup the new data frame...
        DataFrame frame = new DataFrame();
        frame.setFrameType(DataFrame.FrameType.Request);
        frame.setCommand(cmd);
        // Add the data payload...
        frame.addPayload(request.getPayload());
        // Check if the sequence number should be appended at the last payload
        // position...
        if (request.getSequenceNumber() > 0)
          frame.addPayload(request.getSequenceNumber());
        stats.transmittedPackets++;
        return frameLayer.write(frame);
      }
    } catch (FrameLayerException e)
    {
      throw new FrameLayerException("Error in requestWithNoRepsonse :"
          + e.getMessage());
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

      // Wait for reponse from peer or timeout...
      response = queue.poll(timeout);
      if (response != null)
      {
        this.command = CommandType.None;
        // Sequence number check...
        if (sequenceCheck
            && response.getSequenceNumber() != request.getSequenceNumber())
        {
          setReady(true);
          TXStatus txStatus = TXStatus.ResMissing;
          txStatus.setResponse(response);
          return txStatus;
        }
      } else
      {
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
        responses[i] = queue.poll(timeout);
        if (responses[i] != null)
        {
          // Strip the sequence number if used by the request
          // The sequence number is placed as the first byte in the payload
          if (sequenceCheck
              && responses[i].getSequenceNumber() > 0
              && responses[i].getSequenceNumber() != request
                  .getSequenceNumber())
          {
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
                TXStatus txStatus = TXStatus.CompleteFail;
                txStatus.setResponses(responses);
                return txStatus;
              }
            }
          }
        } else
        {
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
        responses[i] = queue.poll(timeout);
        if (responses[i] != null)
        {
          // Strip the sequence number if used by the request
          // The sequence number is placed as the first byte in the payload
          if (sequenceCheck
              && responses[i].getSequenceNumber() > 0
              && responses[i].getSequenceNumber() != request
                  .getSequenceNumber())
          {
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
                TXStatus txStatus = TXStatus.CompleteFail;
                txStatus.setResponses(responses);
                return txStatus;
              }
            }
          }
        } else
        {
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
    this.asyncCallback = handler;
  }

  // / <summary>
  // /
  // / </summary>
  public void setReady(boolean value)
  {
    _isReady = value;
  }

  private int incrementSequenceNumber()
  {
    sequenceNumber++;

    // Test if sequence number should wrap-around...
    if (sequenceNumber >= MAX_SEQUENCE_NUMBER)
      sequenceNumber = MIN_SEQUENCE_NUMBER;

    return sequenceNumber;
  }
}
