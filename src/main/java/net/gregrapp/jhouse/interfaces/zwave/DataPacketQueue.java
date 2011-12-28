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

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Greg Rapp
 *
 */
class DataPacketQueue
{
  private Queue<DataPacket> rxQueue;
  private TimeoutEvent responseEvent;

  public DataPacketQueue()
  {
    rxQueue = new LinkedList<DataPacket>();
    responseEvent = new TimeoutEvent();
  }

  public void add(DataPacket packet)
  {
    synchronized (rxQueue)
    {
      rxQueue.add(packet);
    }
    // Signal any waiting calls
    responseEvent.set();
  }

  public DataPacket poll(int timeout)
  {
    DataPacket packet = null;
    
    // First check if there is something in the queue...
    synchronized (rxQueue)
    {
      if (rxQueue.size() > 0)
      {
        packet = (DataPacket)rxQueue.poll();
      }
    }
    
    if (packet == null && responseEvent.wait(timeout))
    {
      // Check that sequence number from the received packet equals the one transmitted...
      synchronized (rxQueue)
      {
        if (rxQueue.size() > 0)
        {
          packet = (DataPacket)rxQueue.poll();
        }
      }
    }
    return packet;
  }

  public void clear()
  {
    synchronized (rxQueue)
    {
      rxQueue.clear();
    }
  }

}
