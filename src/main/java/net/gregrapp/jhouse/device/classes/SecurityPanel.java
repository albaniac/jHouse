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
  public void armNoEntryDelay();
  public void disarm();
  public void panic();
  public void panicFire();
  public void panicAmbulance();
  //public String getZoneLabel(int zone);
}
