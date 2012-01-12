/**
 * 
 */
package net.gregrapp.jhouse.events;

import net.gregrapp.jhouse.device.types.Device;

/**
 * @author grapp
 *
 */
public class DeviceEvent extends AbstractEvent
{
  protected Device device;

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
  public int getDeviceId()
  {
    return device.getDeviceId();
  }
}
