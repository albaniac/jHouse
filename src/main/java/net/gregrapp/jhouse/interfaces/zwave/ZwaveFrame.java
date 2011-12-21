/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveFrame
{
  private int[] buffer;
  private int type;
  private int callback;
  
  public ZwaveFrame(int[] buffer, int type, int callback)
  {
    this.buffer = buffer;
    this.type = type;
    this.callback = callback;
  }

  public List<Integer> getFrame()
  {
    List<Integer> frame = new ArrayList<Integer>();
    // Start of frame byte
    frame.add(ZwaveConstants.SOF);
    // Length byte
    frame.add(buffer.length + 2 + (callback>0?1:0));
    // 
    frame.add(type);
    // Payload
    for (int b : buffer)
    {
      frame.add(b);
    }
    // Callback ID
    frame.add(callback);
    // CRC
    frame.add(checksum(frame));
    
    return frame;
  }
  
  private int checksum(List<Integer> frame)
  {
    int checksum = 0xFF;
    for (int b : frame)
    {
      checksum ^= b;
    }
    return checksum;
  }
}
