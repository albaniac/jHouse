/**
 * 
 */
package net.gregrapp.jhouse.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gregrapp.jhouse.device.types.Device;

/**
 * @author Greg Rapp
 * 
 */
public class StateManagerImpl implements StateManager
{
  public enum StateType
  {
    SWITCHLEVEL
  }

  public enum StateEventType
  {
    NONE, SET, CHANGED
  }

  private Map<Integer, HashMap<StateType, String>> state;
  private List<StateEventListener> wildcardListeners;

  /*
   * private Map<StateEventListener, StateType> typeListeners; private
   * Map<Integer, StateEventListener> deviceListeners;
   */
  /**
   * 
   */
  public StateManagerImpl()
  {
    state = new HashMap<Integer, HashMap<StateType, String>>();
    wildcardListeners = new ArrayList<StateEventListener>();
    /*
     * typeListeners = new HashMap<StateType, StateEventListener>();
     * deviceListeners = new HashMap<Integer, StateEventListener>();
     */
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.managers.StateManager#setState(net.gregrapp.jhouse.device.types.Device, net.gregrapp.jhouse.managers.StateManagerImpl.StateType, java.lang.String)
   */
  @Override
  public void setState(Device device, StateType stateType, String stateValue)
  {
    this.setState(device.getDeviceId(), stateType, stateValue);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.managers.StateManager#setState(int, net.gregrapp.jhouse.managers.StateManagerImpl.StateType, java.lang.String)
   */
  @Override
  public void setState(final int deviceId, final StateType stateType,
      final String stateValue)
  {
    StateEventType eventType = StateEventType.NONE;

    if (!this.state.containsKey(deviceId))
      this.state.put(deviceId, new HashMap<StateType, String>());

    HashMap<StateType, String> deviceState = this.state.get(deviceId);

    if (deviceState.containsKey(stateType))
    {
      if (deviceState.get(stateType) != null
          & deviceState.get(stateType) == stateValue)
      {
        eventType = StateEventType.SET;
      } else
      {
        eventType = StateEventType.CHANGED;
      }
    }

    deviceState.put(stateType, stateValue);

    this.state.put(deviceId, deviceState);

    final StateEventType finalEventType = eventType;

    new Thread(new Runnable()
    {
      public void run()
      {
        notifyEvent(deviceId, stateType, finalEventType, stateValue);
      }
    }).start();
  }

  public String getState(int deviceId, StateType stateType)
  {
    if (!this.state.containsKey(deviceId) || !this.state.get(deviceId).containsKey(stateType))
      return null;
    else
      return this.state.get(deviceId).get(stateType);
  }
  
  private void notifyEvent(int deviceId, StateType stateType,
      StateEventType eventType, String stateValue)
  {
    for (StateEventListener listener : this.wildcardListeners)
    {
      listener.stateEvent(deviceId, stateType, eventType, stateValue);
    }

    /*
     * for (StateEventListener listener : this.typeListeners.keySet()) {
     * 
     * listener.stateEvent(deviceId, stateType, stateValue); }
     */
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.managers.StateManager#addListener(net.gregrapp.jhouse.managers.StateEventListener)
   */
  @Override
  public void addListener(StateEventListener listener)
  {
    this.wildcardListeners.add(listener);
  }

  /*
   * public void addListener(StateEventListener listener, StateType stateType) {
   * this.typeListeners.put(stateType, listener); }
   * 
   * public void addListner(StateEventListener listener, Device device) {
   * this.addListener(listener, device.getDeviceId()); }
   * 
   * public void addListener(StateEventListener listener, int deviceId) {
   * this.deviceListeners.put(deviceId, listener); }
   */
}
