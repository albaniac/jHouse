/**
 * 
 */
package net.gregrapp.jhouse.services;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.models.ApnsDevice;
import net.gregrapp.jhouse.models.User;
import net.gregrapp.jhouse.repositories.ApnsDeviceRepository;
import net.gregrapp.jhouse.repositories.UserRepository;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.NetworkIOException;
import com.notnoop.exceptions.RuntimeIOException;

/**
 * @author Greg Rapp
 * 
 */

@Service
public class AppleApnsServiceImpl implements AppleApnsService
{

  // Config key
  private static final String APNS_ENVIRONMENT = "APNS_ENVIRONMENT";

  // Config key
  private static final String CERT_PASSWORD = "CERT_PASSWORD";

  // Config key
  private static final String CERT_PATH = "CERT_PATH";

  // Config namepace
  private static final String CONFIG_NAMESPACE = "net.gregrapp.jhouse.services.AppleApnsService";

  // Config key
  private static final String INACTIVE_DEVICE_POLL_MINUTES = "INACTIVE_DEVICE_POLL_MINUTES";

  private static final XLogger logger = XLoggerFactory
      .getXLogger(AppleApnsServiceImpl.class);

  @Autowired
  private ApnsDeviceRepository apnsDeviceRepository;

  private ApnsService apnsService;

  @Autowired
  private ApplicationContext appContext;

  private ConfigService configService;

  private int DEFAULT_POLL_MINUTES = 60;

  private ScheduledExecutorService inactiveDeviceExecutor;

  @Autowired
  private UserRepository userRepository;

  /**
   * Shut down APNs cleanly
   */
  @PreDestroy
  public void destroy()
  {
    logger.entry();

    logger.info("Destroying Apple Push Notification service");
    apnsService.stop();
    inactiveDeviceExecutor.shutdownNow();

    logger.exit();
  }

  /**
   * Return Spring proxy of this service
   * 
   * @return service proxy
   */
  private AppleApnsService getSpringProxy()
  {
    return appContext.getBean(AppleApnsService.class);
  }

  /**
   * Starts executor to pull down inactive APNs devices periodically
   */
  private void monitorInactiveDevices()
  {
    logger.entry();

    String strPollMinutes = this.configService.get(CONFIG_NAMESPACE,
        INACTIVE_DEVICE_POLL_MINUTES);
    int pollMinutes = DEFAULT_POLL_MINUTES;
    if (strPollMinutes == null)
    {
      logger.warn(
          "Config option [{}.{}] is not set, using default value of [{}]",
          new Object[] { CONFIG_NAMESPACE, INACTIVE_DEVICE_POLL_MINUTES,
              pollMinutes });
    } else
    {
      try
      {
        pollMinutes = Integer.valueOf(strPollMinutes).intValue();
      } catch (NumberFormatException e)
      {
        logger.warn("Invalid value [{}] specified for config option [{}.{}]",
            new Object[] { strPollMinutes, CONFIG_NAMESPACE,
                INACTIVE_DEVICE_POLL_MINUTES });
      }
    }

    logger.debug("Starting Apple APNs inactive device monitor executor");
    inactiveDeviceExecutor = Executors.newSingleThreadScheduledExecutor();
    inactiveDeviceExecutor.scheduleAtFixedRate(new Runnable()
    {
      public void run()
      {
        logger.entry();

        if (apnsDeviceRepository != null)
        {
          try
          {
            Map<String, Date> inactiveDevices = apnsService
                .getInactiveDevices();
            for (String deviceToken : inactiveDevices.keySet())
            {
              ApnsDevice device = apnsDeviceRepository.findByToken(deviceToken);
              apnsDeviceRepository.delete(device);
            }
            apnsDeviceRepository.flush();
          } catch (NetworkIOException e)
          {
            logger
                .error("Unable to connect to APNs inactive device service", e);
          }
        }

        logger.exit();
      }
    }, 1, 60, TimeUnit.MINUTES);

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.services.AppleApnsService#putToken(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void putDevice(String username, String uuid, String token,
      String description)
  {
    logger.entry(username, uuid, token, description);

    ApnsDevice device = apnsDeviceRepository.findByUuid(uuid);

    if (device != null)
    {
      logger
          .debug(
              "Existing APNs device record found, updating token and description [token={}, description={}]",
              token, description);
      device.setToken(token);
      device.setDescription(description);
      device.setLastUpdate(Calendar.getInstance());
      apnsDeviceRepository.save(device);
    } else
    {
      logger
          .debug("Existing APNs device record not found, creating new record");
      User user = userRepository.findByUsername(username);

      if (user != null)
      {
        ApnsDevice newDevice = new ApnsDevice();
        newDevice.setUuid(uuid);
        newDevice.setDescription(description);
        newDevice.setToken(token);
        newDevice.setEnabled(true);
        newDevice.setUser(user);
        newDevice.setLastUpdate(Calendar.getInstance());
        apnsDeviceRepository.save(newDevice);
      } else
      {
        logger
            .warn(
                "User [{}] not found in database, unable to create APNs device record",
                username);
      }
    }

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.AppleApnsService#send(long,
   * java.lang.String)
   */
  @Override
  public void send(long userId, String alertBody)
  {
    logger.entry(userId, alertBody);

    // Call method through Spring proxy so that a transaction is created
    getSpringProxy().send(userId, alertBody, 0, null);

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.AppleApnsService#send(long,
   * java.lang.String, int)
   */
  @Override
  public void send(long userId, String alertBody, int badge)
  {
    logger.entry(userId, alertBody, badge);

    // Call method through Spring proxy so that a transaction is created
    getSpringProxy().send(userId, alertBody, badge, null);

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.AppleApnsService#send(long,
   * java.lang.String, int, java.lang.String)
   */
  @Override
  @Transactional(readOnly = true)
  public void send(long userId, String alertBody, int badge, String sound)
  {
    logger.entry(userId, alertBody, badge, sound);

    if (userRepository != null)
    {
      User user = userRepository.findOne(userId);

      if (user == null)
      {
        logger.warn("Unable to send APNs notification, user ID [{}] not found",
            userId);
        logger.exit();
        return;
      } else
      {
        for (ApnsDevice device : user.getApnsDevices())
        {
          this.send(device.getToken(), alertBody, badge, sound);
        }
      }
    }

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.AppleApnsService#send(long,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void send(long userId, String alertBody, String sound)
  {
    logger.entry(userId, alertBody, sound);

    // Call method through Spring proxy so that a transaction is created
    getSpringProxy().send(userId, alertBody, 0, sound);

    logger.exit();
  }

  /**
   * Send Apple Push Notification service message
   * 
   * @param token
   *          device token
   * @param alertBody
   *          alert message
   * @param badge
   *          number for application icon badge (0 = no badge)
   * @param sound
   *          sound to play (default sound="default")
   */
  private void send(String token, String alertBody, int badge, String sound)
  {
    logger.entry(token, alertBody, badge, sound);

    logger.debug("Building APNs payload [alertBody={}, badge={}, sound={}]",
        new Object[] { alertBody, badge, sound });

    String payload = null;

    if (sound == null)
    {
      payload = APNS.newPayload().alertBody(alertBody).shrinkBody("...")
          .badge(badge).build();
    } else
    {
      payload = APNS.newPayload().alertBody(alertBody).shrinkBody("...")
          .badge(badge).sound(sound).build();
    }

    logger.debug("Sending APNs alert to device [token={}]", token);
    apnsService.push(token, payload);

    logger.exit();
  }

  /**
   * Sets an instance of the ConfigService
   */
  @Autowired
  public void setConfigService(ConfigService configService)
  {
    logger.entry(configService);

    logger.debug("Injecting ConfigService");

    this.configService = configService;

    String certPath = this.configService.get(CONFIG_NAMESPACE, CERT_PATH);
    String certPassword = this.configService.get(CONFIG_NAMESPACE,
        CERT_PASSWORD);

    if (certPath == null || certPassword == null)
    {
      logger
          .error("Unable to initialize Apple APNs service, cert path or cert password not set");
      logger.exit();
      return;
    }

    logger.info("Configuring Apple APNs Service");

    String apnsEnvironment = configService.get(CONFIG_NAMESPACE,
        APNS_ENVIRONMENT);

    try
    {
      if (apnsEnvironment != null
          && apnsEnvironment.equalsIgnoreCase("production"))
      {
        logger.debug("Creating production APNs service instance");
        apnsService = APNS.newService().withCert(certPath, certPassword)
            .withProductionDestination().build();
      } else
      {
        logger.debug("Creating sandbox APNs service instance");
        apnsService = APNS.newService().withCert(certPath, certPassword)
            .withSandboxDestination().build();
      }
    } catch (RuntimeIOException e)
    {
      logger.error("Error connecting to APNs service", e);
    }

    // Start polling Apple for inactive devices
    if (apnsService != null)
      this.monitorInactiveDevices();

    logger.exit();
  }
}
