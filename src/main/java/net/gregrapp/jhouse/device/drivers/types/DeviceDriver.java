/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.types;

import net.gregrapp.jhouse.device.DriverDevice;

/**
 * @author Greg Rapp
 * 
 */
public interface DeviceDriver
{
  /**
   * Used by a device to register itself with a driver
   * 
   * @param index
   *          device driver value index to link to this device (zero based)
   * @param device
   *          DriverDevice instance
   */
  public void attachDevice(int index, DriverDevice device);

  /**
   * Called by an interface to notify attached device drivers that it is ready
   * to accept data
   */
  public void interfaceReady();
}
