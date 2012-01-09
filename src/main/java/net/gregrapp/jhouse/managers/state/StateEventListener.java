/**
 * 
 */
package net.gregrapp.jhouse.managers.state;

import net.gregrapp.jhouse.managers.state.StateManagerImpl.StateEventType;
import net.gregrapp.jhouse.managers.state.StateManagerImpl.StateType;

/**
 * @author grapp
 *
 */
public interface StateEventListener
{
  public void stateEvent(int deviceId, StateType stateType, StateEventType eventType, String stateValue);
}
