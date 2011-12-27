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

  // ---- Note ----
  // Sequence numbers are appended to the data frame at the _last_ position in
  // the payload,
  // just before the checksum byte.
  // Not all requests use sequence numbers
  // The response from the ZW-module sets the sequence number at the _first_
  // position in
  // the payload, just after the command ID
  private static final int MIN_SEQUENCE_NUMBER = 1;
  private static final int MAX_SEQUENCE_NUMBER = 127;
  private int sequenceNumber = MIN_SEQUENCE_NUMBER;
  private FrameLayer frameLayer;
  private DataPacket request;
  private DataFrame.CommandType command;
  private SessionLayerAsyncCallback asyncCallback;
  private SessionStatistics stats;
  private DataPacketQueue queue;
  private DataFrame lastDataFrame = null;
  private boolean _isReady;

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

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayer#close()
   */
  public void close()
  {
    frameLayer.close();
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
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithNoResponse
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket)
   */
  public boolean requestWithNoResponse(CommandType cmd, DataPacket request)
  {
    // TODO Auto-generated method stub
    return false;
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
      DataPacket response, boolean sequenceCheck, int timeout)
  {
    // TODO Auto-generated method stub
    return null;
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
      DataPacket response, boolean sequenceCheck)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithResponse(net
   * .gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket)
   */
  public TXStatus requestWithResponse(CommandType cmd, DataPacket request,
      DataPacket response)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithMultipleResponses
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket[])
   */
  public TXStatus requestWithMultipleResponses(CommandType cmd,
      DataPacket request, DataPacket[] responses)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithMultipleResponses
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket[], boolean)
   */
  public TXStatus requestWithMultipleResponses(CommandType cmd,
      DataPacket request, DataPacket[] responses, boolean sequenceCheck)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithMultipleResponses
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket[], boolean, int)
   */
  public TXStatus requestWithMultipleResponses(CommandType cmd,
      DataPacket request, DataPacket[] responses, boolean sequenceCheck,
      int timeout)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.SessionLayer#requestWithVariableResponses
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket[], int[], boolean, int)
   */
  public TXStatus requestWithVariableResponses(CommandType cmd,
      DataPacket request, DataPacket[] responses, int[] breakVal,
      boolean sequenceCheck, int timeout)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayer#
   * requestWithVariableReturnsAndResponses
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket[], int[], boolean, int)
   */
  public TXStatus requestWithVariableReturnsAndResponses(CommandType cmd,
      DataPacket request, DataPacket[] responses, int[] breakVal,
      boolean sequenceCheck, int timeout)
  {
    // TODO Auto-generated method stub
    return null;
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

  private int getSequenceNumber()
  {
    return sequenceNumber;
  }

  private void setSequenceNumber(int value)
  {
    // Test if sequence number should wrap-around...
    if (sequenceNumber == MAX_SEQUENCE_NUMBER)
      sequenceNumber = MIN_SEQUENCE_NUMBER;
    else
      sequenceNumber = value;
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

  // / <summary>
  // /
  // / </summary>
  public void setReady(boolean value)
  {
    _isReady = value;
  }
}
