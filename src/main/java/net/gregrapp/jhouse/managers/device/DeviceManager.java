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
  /**
   * Execute a method on a device
   * 
   * @param deviceId
   *          device ID
   * @param method
   *          method to execute
   */
  public void execute(int deviceId, String method);

  /**
   * Execute a method with arguments on a device
   * 
   * @param deviceId
   *          device ID
   * @param method
   *          method to execute
   * @param args
   *          method arguments
   */
  public void execute(int deviceId, String method, Object... args);

  /**
   * Get a device by its device ID
   * 
   * @param deviceId
   *          the device's ID
   * @return the device's object instance
   */
  public Device get(int deviceId);

  /**
   * Get device classes for a device
   * 
   * @param device
   *          String array of device class names
   * @return
   */
  public String[] getDeviceClassesForDevice(Device device);

  /**
   * Get all devices
   * 
   * @return array of devices
   */
  public Device[] getDevices();
}
