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

  /*
   * private final PropertyChangeSupport changes = new
   * PropertyChangeSupport(this); protected int deviceId; protected String
   * deviceStatus = null;
   * 
   * protected Object deviceValue = null;
   */
  /*
   * @Autowired protected EventManager eventManager;
   * 
   * @Autowired protected StateManager stateManager;
   */
  /**
   * @param deviceId
   *          device's id
   */
  /*
   * public AbstractDeviceDriver(int deviceId) { this.deviceId = deviceId; }
   * 
   * public void addPropertyChangeListener(final PropertyChangeListener l) {
   * this.changes.addPropertyChangeListener(l); }
   */
  /**
   * Get application unique ID for device
   * 
   * @return device's unique id
   */
  /*
   * public int getDeviceId() { return this.deviceId; }
   */
  /**
   * Get device status text
   * 
   * @return the device status text
   */
  /*
   * public String getStatus() { return deviceStatus; }
   */
  /**
   * Get device status value
   * 
   * @return the device value
   */
  /*
   * public Object getValue() { return deviceValue; }
   * 
   * public void removePropertyChangeListener(final PropertyChangeListener l) {
   * this.changes.removePropertyChangeListener(l); }
   */
  /**
   * Set device status text
   * 
   * @param deviceStatus
   *          the device status text to set
   */
  /*
   * public void setStatus(String deviceStatus) { String oldStatus =
   * this.deviceStatus; this.deviceStatus = deviceStatus;
   * 
   * this.changes.firePropertyChange("status", oldStatus, deviceStatus); }
   */
  /**
   * Set device status value
   * 
   * @param deviceValue
   *          the device value to set
   */
  /*
   * public void setValue(Object deviceValue) { Object oldValue =
   * this.deviceValue; this.deviceValue = deviceValue;
   * 
   * this.changes.firePropertyChange("value", oldValue, deviceValue); }
   */
}
