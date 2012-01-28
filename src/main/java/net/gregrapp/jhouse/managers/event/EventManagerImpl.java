/**
 * 
 */
package net.gregrapp.jhouse.managers.event;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.events.Event;
import net.gregrapp.jhouse.managers.device.DeviceManager;
import net.gregrapp.jhouse.managers.event.calendars.DayTime;
import net.gregrapp.jhouse.managers.event.calendars.NightTime;

import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class EventManagerImpl implements EventManager
{
  private static final Logger logger = LoggerFactory
      .getLogger(EventManagerImpl.class);

  private DeviceManager deviceManager;

  private StatefulKnowledgeSession session;

  /**
   * 
   */
  public EventManagerImpl(StatefulKnowledgeSession session)
  {
    this.session = session;
    
    session.addEventListener( new DebugWorkingMemoryEventListener() );
    session.addEventListener( new DebugAgendaEventListener() );    
    
    this.setCalendars();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.managers.event.EventManager#eventCallback(net.gregrapp
   * .jhouse.events.Event)
   */
  @Override
  public void eventCallback(Event event)
  {
    logger.debug("New event callback of type: {}", event.getClass().getName());
    session.insert(event);
  }
  
  public void setDeviceManager(DeviceManager deviceManager)
  {
    logger.debug("Setting event manager device manager");
    this.deviceManager = deviceManager;

    for (Device device : this.deviceManager.getDevices())
    {
      session.insert(device);
    }
    session.setGlobal("dm", deviceManager);
  }
  
  /**
   * Add the calendar implementations to the KnowledgeSession
   */
  private void setCalendars()
  {
    session.getCalendars().set("nighttime", new NightTime());
    session.getCalendars().set("daytime", new DayTime());
  }
}
