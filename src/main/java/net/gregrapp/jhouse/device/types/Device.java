/**
 * 
 */
package net.gregrapp.jhouse.device.types;

/**
 * @author Greg Rapp
 *
 */
public interface Device
{
  public int getDeviceId();
  
  public String getStatus();
  public void setStatus(String status);
  
  public Object getValue();
  public void setValue(Object value);
  
  /**
   * Called by an interface to notify attached devices that it is ready to accept data
   */
  public void interfaceReady();
}
