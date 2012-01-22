/**
 * 
 */
package net.gregrapp.jhouse.events;

import net.gregrapp.jhouse.device.Device;

/**
 * @author Greg Rapp
 * 
 */
public class DeviceValueEvent extends DeviceEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long oldValue;
  private Long value;

  /**
   * 
   */
  public DeviceValueEvent(Device device, Long value)
  {
    super(device);
    this.value = value;
    this.oldValue = null;
  }

  /**
   * 
   */
  public DeviceValueEvent(Device device, Long value, Long oldValue)
  {
    super(device);
    this.value = value;
    this.oldValue = oldValue;
  }

  /**
   * Returns true if value has changed from the previous value, false if the
   * same
   * 
   * @return true if value changed from previous value, false if the same
   */
  public boolean changed()
  {
    if (value == null || oldValue == null)
      return true;
    
    return value.longValue() != oldValue.longValue();
  }

  /**
   * @return the value
   */
  public Long getValue()
  {
    return value;
  }
}
