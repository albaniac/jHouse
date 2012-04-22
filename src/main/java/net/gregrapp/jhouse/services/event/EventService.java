package net.gregrapp.jhouse.services.event;

import net.gregrapp.jhouse.events.Event;

public interface EventService
{
  /**
   * Cleanly shut down the EventService
   */
  public void destroy();
  
  /**
   * Process an event
   * 
   * @param event event to process
   */
  public void eventCallback(Event event);
}