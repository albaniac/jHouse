package net.gregrapp.jhouse.managers.state;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.managers.state.StateManagerImpl.StateType;

public interface StateManager
{

  public void setState(Device device, StateType stateType, String stateValue);

  public void setState(final int deviceId, final StateType stateType,
      final String stateValue);

  public String getState(int deviceId, StateType stateType);

  public void addListener(StateEventListener listener);

}