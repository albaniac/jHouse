/**
 * 
 */
package net.gregrapp.jhouse.managers.event;

import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.events.Event;
import net.gregrapp.jhouse.managers.device.DeviceManager;
import net.gregrapp.jhouse.managers.event.calendars.DayTime;
import net.gregrapp.jhouse.managers.event.calendars.NightTime;
import net.gregrapp.jhouse.services.config.ConfigService;
import net.gregrapp.jhouse.services.email.EmailService;

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
public class EventManagerImpl implements EventManager
{
  private static final Logger logger = LoggerFactory
      .getLogger(EventManagerImpl.class);

  private static final String RULES_FILE = "RULESFILE";

  private ConfigService configService;

  private StatefulKnowledgeSession ksession;

  /**
   * @param configService
   */
  @Autowired
  public EventManagerImpl(ConfigService configService)
  {
    this.configService = configService;
    this.initDrools();
    this.setCalendars();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.event.EventManager#destroy()
   */
  @Override
  @PreDestroy
  public void destroy()
  {
    logger.info("Halting KnowledgeSession");
    ksession.halt();
    logger.info("Stopping resource change scanner service");
    ResourceFactory.getResourceChangeScannerService().stop();
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
    ksession.insert(event);
  }

  private void initDrools()
  {
    logger.info("Creating rules engine");

    logger.debug("Creating new KnowledgeBuilder");
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

    kbuilder.add(ResourceFactory.newClassPathResource("jhouse.dsl"),
        ResourceType.DSL);
    kbuilder.add(ResourceFactory.newClassPathResource("config.drl"),
        ResourceType.DRL);

    String rulesFile = configService.get(
        this.getClass().getInterfaces()[0].getName(), RULES_FILE);

    if (rulesFile != null && !"".equals(rulesFile))
    {
      logger.debug("Adding rules file to KnowledgeBuilder");
      kbuilder.add(ResourceFactory.newFileResource(rulesFile),
          ResourceType.DSLR);
    } else
    {
      logger.error("Invalid rules file specified in config [{}]", rulesFile);
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
    Thread kthread = new Thread(new Runnable() {
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
   * Add the calendar implementations to the KnowledgeSession
   */
  private void setCalendars()
  {
    logger.debug("Setting calendars in rules rules engine");
    ksession.getCalendars().set("nighttime", new NightTime());
    ksession.getCalendars().set("daytime", new DayTime());
  }

  /**
   * @param deviceManager
   */
  @Autowired
  public void setDeviceManager(DeviceManager deviceManager)
  {
    logger.debug("DeviceManager class injected");
    ksession.setGlobal("dm", deviceManager);
  }

  /**
   * @param emailService
   */
  @Autowired
  public void setEmailService(EmailService emailService)
  {
    logger.debug("EmailService class injected");
    ksession.setGlobal("email", emailService);
  }
}
