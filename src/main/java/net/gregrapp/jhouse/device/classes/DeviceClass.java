/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * @author Greg Rapp
 * Device class interface
 */
public interface DeviceClass
{
  public String getStatus();
  public void setStatus(String status);
  
  public String getValue();
  public void setValue(String value);
}
