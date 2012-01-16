/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * Binary (on/off) switch device class
 * 
 * @author Greg Rapp
 * 
 */
public interface BinarySwitch extends DeviceClass
{
  public void setOn();
  public void setOff();
  public void toggleOnOff();
}
