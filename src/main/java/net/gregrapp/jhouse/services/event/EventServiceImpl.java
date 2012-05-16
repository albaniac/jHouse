/**
 * 
 */
package net.gregrapp.jhouse.services.event;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.events.Event;
import net.gregrapp.jhouse.events.TimeEvent;
import net.gregrapp.jhouse.services.AppleApnsService;
import net.gregrapp.jhouse.services.ConfigService;
import net.gregrapp.jhouse.services.DeviceService;
import net.gregrapp.jhouse.services.EmailService;
import net.gregrapp.jhouse.services.event.calendars.DayTime;
import net.gregrapp.jhouse.services.event.calendars.NightTime;
import net.gregrapp.jhouse.services.event.calendars.SunriseSunset;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Greg Rapp
 * 
 */
@Service
public class EventServiceImpl implements EventService
{
  private static final String CONFIG_DRL = "config.drl";
  private static final String CONFIG_NAMESPACE = "net.gregrapp.jhouse.managers.event.EventManager";
  private static final String JHOUSE_DSL = "jhouse.dsl";
  private static final Logger logger = LoggerFactory
      .getLogger(EventServiceImpl.class);
  private static final String RULES_FILE = "RULESFILE";

  @Autowired
  private AppleApnsService appleApnsService;

  @Autowired
  private ConfigService configService;

  @Autowired
  private DeviceService deviceService;

  @Autowired
  private EmailService emailService;

  private StatefulKnowledgeSession ksession;

  private Timer timeEventTimer;

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.EventService#destroy()
   */
  @Override
  @PreDestroy
  public void destroy()
  {
    logger.info("Halting KnowledgeSession");
    ksession.halt();
    logger.info("Stopping resource change scanner service");
    ResourceFactory.getResourceChangeScannerService().stop();
    logger.info("Canceling TimeEvent timer");
    timeEventTimer.cancel();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.EventService#eventCallback(net.gregrapp
   * .jhouse.events.Event)
   */
  @Override
  public void eventCallback(Event event)
  {
    logger.debug("New event callback of type: {}", event.getClass().getName());
    ksession.insert(event);
  }

  @PostConstruct
  public void init()
  {
    initDrools();
    setGlobals();
    setCalendars();
    initTimeEventTimer();
  }

  private void initDrools()
  {
    logger.info("Creating rules engine");

    logger.debug("Creating new KnowledgeBuilder");
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

    kbuilder.add(ResourceFactory.newClassPathResource(JHOUSE_DSL),
        ResourceType.DSL);
    kbuilder.add(ResourceFactory.newClassPathResource(CONFIG_DRL),
        ResourceType.DRL);

    String rulesFile = configService.get(CONFIG_NAMESPACE, RULES_FILE);

    if (rulesFile == null || "".equals(rulesFile))
    {
      logger.error("Invalid rules file specified in config [{}]", rulesFile);
    } else
    {
      logger.debug("Adding rules file to KnowledgeBuilder");
      kbuilder.add(ResourceFactory.newFileResource(rulesFile),
          ResourceType.DSLR);
    }

    logger.debug("Starting rule file change scanner service");
    ResourceFactory.getResourceChangeScannerService().setInterval(30);
    ResourceFactory.getResourceChangeScannerService().start();

    if (kbuilder.hasErrors())
    {
      logger.error("Error parsing rules file [{}]: {}", rulesFile, kbuilder
          .getErrors().toString());
    }

    logger.debug("Creating new KnowledgeBaseConfiguration");
    KnowledgeBaseConfiguration kbaseConfig = KnowledgeBaseFactory
        .newKnowledgeBaseConfiguration();
    kbaseConfig.setOption(EventProcessingOption.STREAM);

    logger.debug("Creating new KnowledgeBase");
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);
    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

    logger.debug("Creating new StatefulKnowledgeSession");
    ksession = kbase.newStatefulKnowledgeSession();

    ksession.addEventListener(new DebugWorkingMemoryEventListener());
    ksession.addEventListener(new DebugAgendaEventListener());

    logger.info("Creating new rules engine runner thread");
    Thread kthread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        ksession.fireUntilHalt();
        logger.info("Disposing KnowledgeSession");
        ksession.dispose();
      }
    });
    kthread.setDaemon(true);
    logger.info("Starting new rules engine runner thread");
    kthread.start();
  }

  /**
   * Initialize the Timer responsible for firing TimeEvents
   */
  public void initTimeEventTimer()
  {
    timeEventTimer = new Timer(true);
    Calendar now = Calendar.getInstance();

    if (now.get(Calendar.SECOND) != 0)
    {
      now.add(Calendar.MINUTE, 1);
      now.set(Calendar.SECOND, 0);
    }

    String latitudeString = configService.get("LATITUDE");
    String longitudeString = configService.get("LONGITUDE");

    if (latitudeString != null && longitudeString != null)
    {
      try
      {
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);

        timeEventTimer.scheduleAtFixedRate(new TimerTask()
        {
          @Override
          public void run()
          {
            logger.trace("Getting sunset time");
            Calendar sunset = SunriseSunset.getSunset(latitude, longitude);
            sunset.set(Calendar.SECOND, 0);
            sunset.set(Calendar.MILLISECOND, 0);

            logger.trace("Getting sunrise time");
            Calendar sunrise = SunriseSunset.getSunrise(latitude, longitude);
            sunrise.set(Calendar.SECOND, 0);
            sunrise.set(Calendar.MILLISECOND, 0);

            logger.trace("Getting noon time");
            Calendar noon = Calendar.getInstance();
            noon.set(Calendar.HOUR_OF_DAY, 12);
            noon.set(Calendar.MINUTE, 0);
            noon.set(Calendar.SECOND, 0);
            noon.set(Calendar.MILLISECOND, 0);

            logger.trace("Getting curent time");
            Calendar now = Calendar.getInstance();
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            logger.debug("Comparing time event times to current time");
            if (sunset.compareTo(now) == 0)
            {
              logger.debug("Firing sunset time event");
              eventCallback(new TimeEvent(TimeEvent.TimeEventType.SUNSET));
            } else if (sunrise.compareTo(now) == 0)
            {
              logger.debug("Firing sunrise time event");
              eventCallback(new TimeEvent(TimeEvent.TimeEventType.SUNRISE));
            } else if (noon.compareTo(now) == 0)
            {
              logger.debug("Firing noon time event");
              eventCallback(new TimeEvent(TimeEvent.TimeEventType.NOON));
            }
          }
        }, now.getTime(), 60 * 1000);

      } catch (NumberFormatException e)
      {
        logger.warn("Invalid LATITUDE or LONGITUDE values in config");
      }
    }

  }

  /**
   * Add the calendar implementations to the KnowledgeSession
   */
  private void setCalendars()
  {
    String latitudeString = configService.get("LATITUDE");
    String longitudeString = configService.get("LONGITUDE");

    if (latitudeString != null && longitudeString != null)
    {
      try
      {
        double latitude = Double.valueOf(latitudeString);
        double longitude = Double.valueOf(longitudeString);
        logger.debug("Setting calendars in rules rules engine");
        ksession.getCalendars().set("nighttime",
            new NightTime(latitude, longitude));
        ksession.getCalendars()
            .set("daytime", new DayTime(latitude, longitude));
      } catch (NumberFormatException e)
      {
        logger.warn("Invalid LATITUDE or LONGITUDE values in config");
      }
    }
  }

  /**
   * Set knowledge session globals
   */
  private void setGlobals()
  {
    logger.debug("Setting StatefulKnowledgeSession globals");
    ksession.setGlobal("email", emailService);
    ksession.setGlobal("device", deviceService);
    ksession.setGlobal("apns", appleApnsService);
  }
}
