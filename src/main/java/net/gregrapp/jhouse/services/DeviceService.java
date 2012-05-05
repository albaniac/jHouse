/**
 * 
 */
package net.gregrapp.jhouse.services;

import java.util.List;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.classes.DeviceClass;

/**
 * @author Greg Rapp
 * 
 */
public interface DeviceService
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
   * @return the device's object instance, null if not found
   */
  public Device get(int deviceId);

  /**
   * @param deviceId
   *          the device's ID
   * @param type
   *          type of device to return
   * @return the device's instance cast to type, null if not found or not assignable to class
   */
  public <T extends Device> T get(int deviceId, Class<T> type);

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
   * @return List of devices
   */
  public List<Device> getDevices();

  /**
   * Get driver for device, casting the driver to a device class
   * 
   * @param deviceId
   *          device id
   * @param type
   *          device class to return
   * @return
   */
  public <T extends DeviceClass> T getDriver(int deviceId, Class<T> type);

}
