/**
 * 
 */
package net.gregrapp.jhouse.device;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.gregrapp.jhouse.events.DeviceTextEvent;
import net.gregrapp.jhouse.events.DeviceValueEvent;
import net.gregrapp.jhouse.services.event.EventService;

/**
 * @author Greg Rapp
 * 
 */
public class AbstractDevice implements Device
{
  private static final Logger logger = LoggerFactory
      .getLogger(AbstractDevice.class);

  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

  // Fields
  private int deviceId;
  private String deviceName;
  private String deviceText;
  private Long deviceValue;
  private Calendar lastChange;

  @Autowired
  EventService eventService;


  public AbstractDevice(int deviceId)
  {
    this.deviceId = deviceId;
    this.deviceName = "";
    this.deviceText = "";
    this.deviceValue = 0L;
    this.lastChange = Calendar.getInstance();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.Device#addPropertyChangeListener(java.beans.
   * PropertyChangeListener)
   */
  @Override
  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    logger.debug("Adding property change listener to device {}", this.deviceId);
    this.changes.addPropertyChangeListener(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#getId()
   */
  @Override
  public int getId()
  {
    return deviceId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#getLastChange()
   */
  @Override
  public Calendar getLastChange()
  {
    return lastChange;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#getDeviceName()
   */
  @Override
  public String getName()
  {
    return deviceName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#getText()
   */
  @Override
  public String getText()
  {
    return deviceText;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#getValue()
   */
  @Override
  public Long getValue()
  {
    return deviceValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.Device#removePropertyChangeListener(java.beans
   * .PropertyChangeListener)
   */
  @Override
  public void removePropertyChangeListener(final PropertyChangeListener listener)
  {
    this.changes.removePropertyChangeListener(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#setDeviceName(java.lang.String)
   */
  @Override
  public void setName(String deviceName)
  {
    logger.debug("Setting device {} name to {}", this.deviceId, deviceName);
    this.deviceName = deviceName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#setText(java.lang.String)
   */
  @Override
  public void setText(String deviceText)
  {
    logger.debug("Setting device {} text to: {}", this.deviceId, deviceText);
    
    String oldText = this.deviceText;
    
    this.deviceText = deviceText;
    this.lastChange = Calendar.getInstance();
    
    this.changes.firePropertyChange("text", oldText, deviceText);
    
    if (eventService != null)
      eventService.eventCallback(new DeviceTextEvent(this, deviceText, oldText));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.Device#setValue(long)
   */
  @Override
  public void setValue(Long deviceValue)
  {
    logger.debug("Setting device [{}] value to [{}]", this.deviceId, deviceValue);
    
    Long oldValue = this.deviceValue;

    this.deviceValue = deviceValue;
    this.lastChange = Calendar.getInstance();
    
    this.changes.firePropertyChange("value", oldValue, deviceValue);
    
    if (eventService != null)
      eventService.eventCallback(new DeviceValueEvent(this, deviceValue, oldValue));
  }
}
