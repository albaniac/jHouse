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

import net.gregrapp.jhouse.models.ApnsDevice;
import net.gregrapp.jhouse.models.User;
import net.gregrapp.jhouse.repositories.ApnsDeviceRepository;
import net.gregrapp.jhouse.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  /*
   * Config keys
   */
  private static final String CONFIG_NAMESPACE = "net.gregrapp.jhouse.services.AppleApnsService";
  private static final String CERT_PATH = "CERT_PATH";
  private static final String CERT_PASSWORD = "CERT_PASSWORD";
  private static final String INACTIVE_DEVICE_POLL_MINUTES = "INACTIVE_DEVICE_POLL_MINUTES";
  private static final String APNS_ENVIRONMENT = "APNS_ENVIRONMENT";

  private static final Logger logger = LoggerFactory
      .getLogger(AppleApnsServiceImpl.class);

  private ApnsService apnsService;
  private ConfigService configService;

  private ScheduledExecutorService inactiveDeviceExecutor;

  @Autowired
  private ApnsDeviceRepository apnsDeviceRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ApplicationContext appContext;

  private int DEFAULT_POLL_MINUTES = 60;

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
        if (apnsDeviceRepository != null)
        {
          try
          {
            Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
            for (String deviceToken : inactiveDevices.keySet())
            {
              ApnsDevice device = apnsDeviceRepository.findByToken(deviceToken);
              apnsDeviceRepository.delete(device);
            }
            apnsDeviceRepository.flush();
          }
          catch (NetworkIOException e)
          {
            logger.error("Unable to connect to APNs inactive device service", e);
          }
        }
      }
    }, 1, 60, TimeUnit.MINUTES);

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.AppleApnsService#send(java.lang.String,
   * java.lang.String, int)
   */
  @Override
  public void send(String token, String alertBody, int badge)
  {
    logger.debug("Building APNs payload [alertBody={}, badge={}]", alertBody,
        badge);
    String payload = APNS.newPayload().alertBody(alertBody).shrinkBody("...")
        .badge(badge).build();

    logger.debug("Sending APNs alert to device [token={}]", token);
    apnsService.push(token, payload);
  }

  @Override
  @Transactional(readOnly = true)
  public void send(long userId, String alertBody, int badge)
  {
    if (userRepository != null)
    {
      User user = userRepository.findOne(userId);

      if (user == null)
      {
        logger.warn("Unable to send APNs notification, user ID [{}] not found",
            userId);
        return;
      } else
      {
        for (ApnsDevice device : user.getApnsDevices())
        {
          this.send(device.getToken(), alertBody, badge);
        }
      }
    }
  }

  @Override
  public void send(long userId, String alertBody)
  {
    // Call method through Spring proxy so that a transaction is created
    getSpringProxy().send(userId, alertBody, 0);
  }

  /**
   * Sets an instance of the ConfigService
   */
  @Autowired
  public void setConfigService(ConfigService configService)
  {
    logger.debug("Injecting ConfigService");

    this.configService = configService;

    String certPath = this.configService.get(CONFIG_NAMESPACE, CERT_PATH);
    String certPassword = this.configService.get(CONFIG_NAMESPACE,
        CERT_PASSWORD);

    if (certPath == null || certPassword == null)
    {
      logger
          .error("Unable to initialize Apple APNs service, cert path or cert password not set");
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
    }
    catch (RuntimeIOException e)
    {
      logger.error("Error connecting to APNs service", e);
    }
  
    // Start polling Apple for inactive devices
    if (apnsService != null)
      this.monitorInactiveDevices();
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.services.AppleApnsService#putToken(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void putDevice(String username, String uuid, String token,
      String description)
  {
    ApnsDevice device = apnsDeviceRepository.findByUuid(uuid);

    if (device != null)
    {
      logger.debug("Existing APNs device record found, updating token and description [token={}, description={}]", token, description);
      device.setToken(token);
      device.setDescription(description);
      device.setLastUpdate(Calendar.getInstance());
      apnsDeviceRepository.save(device);
    } 
    else
    {
      logger.debug("Existing APNs device record not found, creating new record");
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
      }
      else
      {
        logger.warn("User [{}] not found in database, unable to create APNs device record", username);
      }
    }
  }
}
