/**
 * 
 */
package net.gregrapp.jhouse.device.types;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import net.gregrapp.jhouse.managers.event.EventManager;
import net.gregrapp.jhouse.managers.state.StateManager;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Greg Rapp
 * 
 */
public abstract class AbstractDevice implements Device
{
  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
  protected int deviceId;
  protected String deviceStatus = null;

  protected Object deviceValue = null;

  @Autowired
  protected EventManager eventManager;

  @Autowired
  protected StateManager stateManager;

  /**
   * @param deviceId
   *          device's id
   */
  public AbstractDevice(int deviceId)
  {
    this.deviceId = deviceId;
  }

  public void addPropertyChangeListener(final PropertyChangeListener l)
  {
    this.changes.addPropertyChangeListener(l);
  }

  /**
   * Get application unique ID for device
   * 
   * @return device's unique id
   */
  public int getDeviceId()
  {
    return this.deviceId;
  }

  /**
   * Get device status text
   * @return the device status text
   */
  public String getStatus()
  {
    return deviceStatus;
  }

  /**
   * Get device status value
   * 
   * @return the device value
   */
  public Object getValue()
  {
    return deviceValue;
  }

  public void removePropertyChangeListener(final PropertyChangeListener l)
  {
    this.changes.removePropertyChangeListener(l);
  }

  /**
   * Set device status text
   * @param deviceStatus
   *          the device status text to set
   */
  public void setStatus(String deviceStatus)
  {
    String oldStatus = this.deviceStatus;
    this.deviceStatus = deviceStatus;

    this.changes.firePropertyChange("status", oldStatus, deviceStatus);
  }

  /**
   * Set device status value
   * 
   * @param deviceValue
   *          the device value to set
   */
  public void setValue(Object deviceValue)
  {
    Object oldValue = this.deviceValue;
    this.deviceValue = deviceValue;

    this.changes.firePropertyChange("value", oldValue, deviceValue);
  }
}
