/**
 * 
 */
package net.gregrapp.jhouse.managers;

import net.gregrapp.jhouse.managers.StateManagerImpl.StateEventType;
import net.gregrapp.jhouse.managers.StateManagerImpl.StateType;

/**
 * @author grapp
 *
 */
public interface StateEventListener
{
  public void stateEvent(int deviceId, StateType stateType, StateEventType eventType, String stateValue);
}
