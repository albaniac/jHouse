/**
 * 
 */
package net.gregrapp.jhouse.events;

import net.gregrapp.jhouse.device.Device;

/**
 * @author Greg Rapp
 *
 */
public class DeviceEvent extends AbstractEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected Device device;

  public DeviceEvent(Device device)
  {
    super();
    this.device = device;
  }
  
  /**
   * @return the device
   */
  public Device getDevice()
  {
    return device;
  }
  
  /**
   * @return the device unique ID
   */
  public int getId()
  {
    return device.getId();
  }
}
