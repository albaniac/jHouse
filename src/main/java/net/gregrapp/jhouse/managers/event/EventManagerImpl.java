/**
 * 
 */
package net.gregrapp.jhouse.managers.event;

import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.events.Event;
import net.gregrapp.jhouse.managers.device.DeviceManager;

import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author grapp
 * 
 */
public class EventManagerImpl implements EventManager
{
  private static final Logger logger = LoggerFactory
      .getLogger(EventManagerImpl.class);

  private StatefulKnowledgeSession session;

  @Autowired
  private DeviceManager deviceManager;

  /**
   * 
   */
  public EventManagerImpl(final StatefulKnowledgeSession session)
  {
    this.session = session;
    
    new Thread(
        new Runnable() {
          public void run()
          {
            session.fireUntilHalt();
          }

        }).start();
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
    logger.debug("New event callback of type {}", event.getClass().getName());
    for (Device device : deviceManager.getDevices())
    {
      session.insert(device);
    }
    session.insert(event);
    //session.fireAllRules();
  }
}
