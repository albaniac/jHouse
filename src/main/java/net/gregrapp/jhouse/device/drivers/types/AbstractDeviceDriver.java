/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.types;

import java.util.HashMap;
import java.util.Map;

import net.gregrapp.jhouse.device.DriverDevice;

/**
 * @author Greg Rapp
 * 
 */
public abstract class AbstractDeviceDriver implements DeviceDriver
{

  protected Map<Integer, DriverDevice> attachedDevices = new HashMap<Integer, DriverDevice>();

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.drivers.types.DeviceDriver#attachDevice(net.
   * gregrapp.jhouse.device.DriverDevice)
   */
  public void attachDevice(int index, DriverDevice device)
  {
    attachedDevices.put(index, device);
  }

  /**
   * @param index device driver value index
   * @param text device status text
   */
  protected void updateDeviceText(int index, String text)
  {
    if (attachedDevices.containsKey(index))
      attachedDevices.get(index).setText(text);
  }

  /**
   * @param index device driver value index
   * @param value device value
   */
  protected void updateDeviceValue(int index, long value)
  {
    if (attachedDevices.containsKey(index))
      attachedDevices.get(index).setValue(value);
  }


  /**
   * Set device value bit as a bit field
   * 
   * @param index device driver value index
   * @param bit bit to set
   * @param value value of bit (true = 1, false = 0)
   */
  protected void updateDeviceValueBitmask(int index, int bit, boolean value)
  {
    if (attachedDevices.containsKey(index))
    {
      Long currentVal = attachedDevices.get(index).getValue();
      if (currentVal == null) currentVal = 0L;
      
      if (value == true)
        attachedDevices.get(index).setValue(currentVal | (1 << bit));
      else
        attachedDevices.get(index).setValue(currentVal & ~(1 << bit));
    }
  }
}
