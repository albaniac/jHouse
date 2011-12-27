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

  // <summary>
  // DataPacket()
  // </summary>
  public DataPacket()
  {
    this.idx = 0;
    this.buffer = new int[BUFFER_SIZE];
  }
  
  // <summary>
  // DataPacket(int[] payload)
  // </summary>
  // <param name="payload"></param>
  public DataPacket(int[] payload)
  {
    if (payload == null)
      throw new NullPointerException("payload");
    this.buffer = payload;
    this.idx = payload.length; 
  }
  
  // <summary>
  // addPayload(int data)
  // </summary>
  // <param name="data"></param>
  // <returns></returns>
  public boolean addPayload(int data)
  {
    if (idx > (buffer.length-1)) return false;
    buffer[idx++] = data;
    return true;
  }

  // <summary>
  // AddPayload(int[] payload)
  // </summary>
  // <param name="payload"></param>
  // <returns></returns>
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

  // <summary>
  // int[] GetPayload()
  // </summary>
  // <returns></returns>
  public int[] getPayload()
  {
    int[] payload = new int[idx];
    System.arraycopy(buffer, 0, payload, 0, idx);
    return payload;
  }
  
  // <summary>
  // 
  // </summary>
  public int getLength()
  {
    return idx;
  }

  // <summary>
  // 
  // </summary>
  public int getSequenceNumber()
  {
    return sequenceNumber;
  }

  // <summary>
  // 
  // </summary>
  public void setSequenceNumber(int value)
  {
    sequenceNumber = value;
  }

  // <summary>
  // 
  // </summary>
  public Calendar getTimestamp()
  {
    return timestamp;
  }

  // <summary>
  // 
  // </summary>
  public void setTimestamp(Calendar value)
  {
    timestamp = value;
  }

  // <summary>
  // String
  // </summary>
  // <returns></returns>
  public String ToString()
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

  // <summary>
  // 
  // </summary>
  // <param name="dp1"></param>
  // <param name="dp2"></param>
  // <returns></returns>
  public boolean equals(DataPacket dp1, DataPacket dp2)
  {
    if (!(dp1 instanceof DataPacket) || !(dp2 instanceof DataPacket)) return false;
    if (dp1.getLength() != dp2.getLength()) return false;
    for (int i = 0; i < dp1.getLength(); i++)
      if (dp1.buffer[i] != dp2.buffer[i]) return false;
    return true;
  }

}
