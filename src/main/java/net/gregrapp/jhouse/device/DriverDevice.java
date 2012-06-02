/**
 * 
 */
package net.gregrapp.jhouse.device;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;

/**
 * A device that has an associated device driver
 * 
 * @author Greg Rapp
 * 
 */
public class DriverDevice extends AbstractDevice
{
  private DeviceDriver driver;
  private int driverIndex;

  /**
   * @param deviceId
   *          the unique ID of this device in jHouse
   * @param driver
   *          the driver to attach to
   */
  public DriverDevice(int deviceId, DeviceDriver driver)
  {
    super(deviceId);

    // Use zero as the default driver value index
    this.driverIndex = 0;

    this.driver = driver;
    this.attachToDriver();
  }

  /**
   * @param deviceId
   *          the unique ID of this device in jHouse
   * @param driverIndex
   *          the driver value index to attach to (zero based)
   * @param driver
   *          the driver to attach to
   */
  public DriverDevice(int deviceId, int driverIndex, DeviceDriver driver)
  {
    super(deviceId);
    this.driverIndex = driverIndex;
    this.driver = driver;
    this.attachToDriver();
  }

  /**
   * Attach this device to a driver instance
   */
  private void attachToDriver()
  {
    if (this.driver != null)
    {
      this.driver.attachDevice(driverIndex, this);
    }
  }

  /**
   * @return the driver that this device is registered with
   */
  public DeviceDriver getDriver()
  {
    return this.driver;
  }

  /**
   * Driver index
   * 
   * @return the driverIndex
   */
  public int getDriverIndex()
  {
    return driverIndex;
  }

  /**
   * @param driver
   *          the driver to register this device with
   */
  public void setDriver(DeviceDriver driver)
  {
    this.driver = driver;
    this.attachToDriver();
  }

  /**
   * Driver index
   * 
   * @param driverIndex
   *          the driverIndex to set
   */
  public void setDriverIndex(int driverIndex)
  {
    this.driverIndex = driverIndex;
  }

}
