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
  private int length;
  private int type;
  private int[] buffer;
  private int callback;
  private int checksum;

  public ZwaveFrame()
  {
  }

  public ZwaveFrame(int type, int callback, int[] buffer)
  {
    this.buffer = buffer;
    this.type = type;
    this.callback = callback;
    this.length = buffer.length + 2 + (callback > 0 ? 1 : 0);
  }

  public static ZwaveFrame parseRawFrame(int length, int[] payload)
  {
    ZwaveFrame newFrame = new ZwaveFrame();

    newFrame.setLength(length);

    newFrame.setType(payload[0]);

    int[] buf = Arrays.copyOfRange(payload, 0, length - 1);
    newFrame.setBuffer(buf);

    newFrame.setCallback(0);

    newFrame.setChecksum(payload[payload.length - 1]);

    return newFrame;
  }

  public int[] buildFrame()
  {
    int frame[] = new int[this.length + 2];
    frame[0] = ZwaveConstants.SOF;
    frame[1] = this.length;
    frame[2] = this.type;
    for (int i = 0; i < buffer.length; i++)
    {
      frame[i + 3] = buffer[i];
    }

    if (callback != 0)
      frame[buffer.length + 1] = callback;

    frame[buffer.length + 1 + 1] = calculateChecksum(this.length,
        Arrays.copyOfRange(frame, 2, frame.length));

    return frame;
  }

  private static int calculateChecksum(int payload_len, int[] payload)
  {
    int checksum = 0xFF;

    checksum ^= payload_len;
    for (int b : payload)
    {
      checksum ^= b;
    }
    return checksum;
  }

  public boolean isValidChecksum()
  {
    int[] frame = this.buildFrame();
    int calculatedChecksum = calculateChecksum(this.length,
        Arrays.copyOfRange(frame, 1, frame.length - 1));
    int receivedChecksum = this.getChecksum();
    return (calculatedChecksum == receivedChecksum);
  }

  public static boolean isValidChecksum(int payload_len, int[] payload)
  {
    int calculatedChecksum = calculateChecksum(payload_len,
        Arrays.copyOfRange(payload, 0, payload.length - 1));
    int realChecksum = payload[payload.length - 1];
    return (calculatedChecksum == realChecksum);
  }

  public int[] getBuffer()
  {
    return buffer;
  }

  public void setBuffer(int[] buffer)
  {
    this.buffer = buffer;
  }

  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public int getCallback()
  {
    return callback;
  }

  public void setCallback(int callback)
  {
    this.callback = callback;
  }

  public int getLength()
  {
    return length;
  }

  public void setLength(int length)
  {
    this.length = length;
  }

  public int getChecksum()
  {
    return checksum;
  }

  public void setChecksum(int checksum)
  {
    this.checksum = checksum;
  }
}
