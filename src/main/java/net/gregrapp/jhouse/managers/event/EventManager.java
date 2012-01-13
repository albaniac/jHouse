package net.gregrapp.jhouse.managers.event;

import net.gregrapp.jhouse.events.Event;
import net.gregrapp.jhouse.managers.device.DeviceManager;

public interface EventManager
{

  public void setDeviceManager(DeviceManager deviceManager);
  public abstract void eventCallback(Event event);
}