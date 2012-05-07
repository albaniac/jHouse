package net.gregrapp.jhouse.device;

import java.beans.PropertyChangeListener;
import java.util.Calendar;

public interface Device
{
  /**
   * @param listener
   */
  public void addPropertyChangeListener(final PropertyChangeListener listener);

  /**
   * @return
   */
  public String getFloor();

  /**
   * @return the deviceId
   */
  public int getId();

  /**
   * @return the lastChange
   */
  public Calendar getLastChange();

  /**
   * @return the deviceName
   */
  public String getName();

  /**
   * @return
   */
  public String getRoom();

  /**
   * @return the deviceText
   */
  public String getText();

  /**
   * @return the deviceValue
   */
  public Long getValue();

  /**
   * @param listener
   */
  public void removePropertyChangeListener(final PropertyChangeListener listener);

  /**
   * @param floor
   */
  public void setFloor(String floor);

  /**
   * @param deviceName
   *          the deviceName to set
   */
  public void setName(String deviceName);

  /**
   * @param room
   */
  public void setRoom(String room);

  /**
   * @param deviceText
   *          the deviceText to set
   */
  public void setText(String deviceText);

  /**
   * @param deviceValue
   *          the deviceValue to set
   */

  /**
   * @param deviceValue
   */
  public void setValue(Long deviceValue);
}
