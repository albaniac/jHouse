/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;

/**
 * @author Greg Rapp
 * 
 */
public interface Interface
{
  /**
   * Initialize the Interface. Called when Transport for this Interface has been
   * set.
   */
  public void init();

  /**
   * @param driver
   *          device driver to attach to this interface
   */
  public void attachDeviceDriver(DeviceDriver driver);

  /**
   * @return interface status
   */
  public boolean isReady();

  /**
   * Destroy the interface
   */
  public void destroy();
}
