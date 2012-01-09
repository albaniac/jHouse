/**
 * 
 */
package net.gregrapp.jhouse.managers.device;

import net.gregrapp.jhouse.device.types.Device;

/**
 * @author Greg Rapp
 *
 */
public interface DeviceManager
{
  public Device getDeviceForId(int deviceId);
  public String[] getDeviceClassesForDevice(Device device);
}
