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
   * Initialize the Interface
   */
  public void init();

  /**
   * @param driver device driver to attach to this interface
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
