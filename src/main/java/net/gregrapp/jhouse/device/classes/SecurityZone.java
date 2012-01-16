/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * Security system zone
 * 
 * @author Greg Rapp
 *
 */
public interface SecurityZone extends DeviceClass
{

  public void opened();
  public void closed();
}
