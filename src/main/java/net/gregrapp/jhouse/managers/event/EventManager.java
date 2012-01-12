package net.gregrapp.jhouse.managers.event;

import net.gregrapp.jhouse.events.Event;

public interface EventManager
{

  public abstract void eventCallback(Event event);
}