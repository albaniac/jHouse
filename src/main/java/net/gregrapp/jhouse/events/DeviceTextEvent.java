/**
 * 
 */
package net.gregrapp.jhouse.events;

import net.gregrapp.jhouse.device.Device;

/**
 * @author Greg Rapp
 * 
 */
public class DeviceTextEvent extends DeviceEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String oldText;
  private String text;

  /**
   * 
   */
  public DeviceTextEvent(Device device, String text)
  {
    super(device);
    this.text = text;
    this.oldText = null;
  }

  /**
   * 
   */
  public DeviceTextEvent(Device device, String text, String oldText)
  {
    super(device);
    this.text = text;
    this.oldText = oldText;
  }

  /**
   * Returns true if text has changed from the previous text, false if the
   * same
   * 
   * @return true if text changed from previous text, false if the same
   */
  public boolean changed()
  {
    if (text == null || oldText == null)
      return true;
    
    return text != oldText;
  }

  /**
   * @return the value
   */
  public String getText()
  {
    return text;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DeviceTextEvent [oldText=");
    builder.append(oldText);
    builder.append(", text=");
    builder.append(text);
    builder.append(", device=");
    builder.append(device.getName());
    builder.append("]");
    return builder.toString();
  } 
}
