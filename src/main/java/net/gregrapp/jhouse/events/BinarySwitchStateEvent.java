/**
 * 
 */
package net.gregrapp.jhouse.events;

import java.util.Calendar;

import net.gregrapp.jhouse.device.types.Device;

/**
 * Switch state change event
 * 
 * @author Greg Rapp
 * 
 */
public class BinarySwitchStateEvent extends DeviceEvent
{

  public enum SwitchState
  {
    OFF,
    ON
  }

  /**
   * The new state of this device
   */
  private SwitchState newState;

  /**
   * The previous state of this device
   */
  private SwitchState previousState;

  /**
   * @param newState
   * @param previousState
   */
  public BinarySwitchStateEvent(Device device, SwitchState newState,
      SwitchState previousState)
  {
    this.device = device;
    this.newState = newState;
    this.previousState = previousState;
    this.time = Calendar.getInstance();
  }

  /**
   * @return the new state
   */
  public SwitchState getNewState()
  {
    return newState;
  }

  /**
   * @return the previous state
   */
  public SwitchState getPreviousState()
  {
    return previousState;
  }
}
