/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.transports.Transport;

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
   * Set the Transport to be utilized by this Interface
   * 
   * @param transport
   *          instance of transport
   */
  public void setTransport(Transport transport);

  /**
   * @return a transport instance
   */
  public Transport getTransport();

  /**
   * @param device device driver to attach to this interface
   */
  public void attachDeviceDriver(DeviceDriver device);
  
  /**
   * @return interface status
   */
  public boolean isReady();
  
  /**
   * Destroy the interface
   */
  public void destroy();
}
