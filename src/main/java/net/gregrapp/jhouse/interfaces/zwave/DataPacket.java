//////////////////////////////////////////////////////////////// 
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
//          Revision:         $Revision: 1.5 $
//          Last Changed:     $Date: 2006/07/24 09:14:16 $
//
///////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.Calendar;

/**
 * @author Greg Rapp
 *
 */
public class DataPacket
{
  private Calendar timestamp;
  private int[] buffer;
  private int idx;
  private int sequenceNumber;

  private static final int BUFFER_SIZE = 100;

  public DataPacket()
  {
    this.idx = 0;
    this.buffer = new int[BUFFER_SIZE];
    this.sequenceNumber = 0;
  }
    
  public DataPacket(int[] payload)
  {
    if (payload == null)
      throw new NullPointerException("payload");
    this.buffer = payload;
    this.idx = payload.length; 
  }
  
  public boolean addPayload(int data)
  {
    if (idx > (buffer.length-1)) return false;
    buffer[idx++] = data;
    return true;
  }

  public boolean addPayload(int[] payload)
  {
    if (payload == null)
    {
      throw new NullPointerException("payload");
    }
    if ((idx + payload.length) > buffer.length) return false;
    System.arraycopy(payload, 0, buffer, idx, payload.length);
    idx += (int)payload.length;
    return true;
  }

  public int[] getPayload()
  {
    int[] payload = new int[idx];
    System.arraycopy(buffer, 0, payload, 0, idx);
    return payload;
  }
  
  public int getLength()
  {
    return idx;
  }
  
  public int getSequenceNumber()
  {
    return sequenceNumber;
  }

  public void setSequenceNumber(int value)
  {
    sequenceNumber = value;
  }

  public Calendar getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp(Calendar value)
  {
    timestamp = value;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(100);

    // Data payload...
    for (int i = 0; i < idx; i++)
    {
      sb.append(buffer[i]);
      sb.append(' ');
    }

    return sb.toString();
  }

  public boolean equals(DataPacket dp1, DataPacket dp2)
  {
    if (!(dp1 instanceof DataPacket) || !(dp2 instanceof DataPacket)) return false;
    if (dp1.getLength() != dp2.getLength()) return false;
    for (int i = 0; i < dp1.getLength(); i++)
      if (dp1.buffer[i] != dp2.buffer[i]) return false;
    return true;
  }

}
