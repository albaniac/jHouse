package net.gregrapp.jhouse.managers.event;

import net.gregrapp.jhouse.events.Event;

public interface EventManager
{
  /**
   * Cleanly shut down the EventManager
   */
  public void destroy();
  
  /**
   * Process an event
   * 
   * @param event event to process
   */
  public void eventCallback(Event event);
}