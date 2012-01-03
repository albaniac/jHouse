/**
 * 
 */
package net.gregrapp.jhouse.device.types;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Greg Rapp
 * 
 */
public abstract class AbstractDevice implements Device
{
  protected String deviceStatus = null;
  protected String deviceValue = null;
  protected int deviceId;
  
  /**
   * @param deviceId device's id
   */
  public AbstractDevice(int deviceId)
  {
    this.deviceId = deviceId;
  }

  /**
   * @return device's unique id
   */
  public int getDeviceId()
  {
    return this.deviceId;
  }
  
  /**
   * @return the device status text
   */
  public String getStatus()
  {
    return deviceStatus;
  }

  /**
   * @return the device value
   */
  public String getValue()
  {
    return deviceValue;
  }

  /**
   * @param deviceStatus
   *          the device status text to set
   */
  public void setStatus(String deviceStatus)
  {
    this.deviceStatus = deviceStatus;
  }

  /**
   * @param deviceValue
   *          the device value to set
   */
  public void setValue(String deviceValue)
  {
    this.deviceValue = deviceValue;
  }
}
