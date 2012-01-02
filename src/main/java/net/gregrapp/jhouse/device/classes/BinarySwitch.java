/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * @author Greg Rapp
 * Binary (on/off) switch device class
 */
public interface BinarySwitch extends DeviceClass
{
  public void setOn();
  public void setOff();
  public void toggleOnOff();
}
