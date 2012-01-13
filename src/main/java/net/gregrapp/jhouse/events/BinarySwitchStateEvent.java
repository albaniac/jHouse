/**
 * 
 */
package net.gregrapp.jhouse.events;

import java.util.Calendar;

import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.states.BinarySwitchState;

/**
 * Switch state change event
 * 
 * @author Greg Rapp
 * 
 */
public class BinarySwitchStateEvent extends DeviceEvent
{
  /**
   * The new state of this device
   */
  private BinarySwitchState state;

  /**
   * The previous state of this device
   */
  private BinarySwitchState previousState;

  /**
   * @param state current state
   * @param previousState last known state
   */
  public BinarySwitchStateEvent(Device device, BinarySwitchState state,
      BinarySwitchState previousState)
  {
    this.device = device;
    this.state = state;
    this.previousState = previousState;
    this.time = Calendar.getInstance();
  }

  /**
   * @return the new state
   */
  public BinarySwitchState getState()
  {
    return state;
  }

  /**
   * @return the previous state
   */
  public BinarySwitchState getPreviousState()
  {
    return previousState;
  }
}
