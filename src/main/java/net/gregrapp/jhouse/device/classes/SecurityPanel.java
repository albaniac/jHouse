/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * Security system panel
 * 
 * @author Greg Rapp
 *
 */
public interface SecurityPanel extends DeviceClass
{
  public void arm();
  public void armAway();
  public void armStay();
  public void disarm();
}
