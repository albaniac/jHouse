package net.gregrapp.jhouse.managers;

import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.managers.StateManagerImpl.StateType;

public interface StateManager
{

  public void setState(Device device, StateType stateType, String stateValue);

  public void setState(final int deviceId, final StateType stateType,
      final String stateValue);

  public String getState(int deviceId, StateType stateType);

  public void addListener(StateEventListener listener);

}