/**
 * 
 */
package net.gregrapp.jhouse.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.gregrapp.jhouse.services.config.ConfigService;
import net.gregrapp.jhouse.services.email.EmailServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

/**
 * @author Greg Rapp
 * 
 */
public class AppleApnsServiceImpl implements AppleApnsService
{

  /*
   * Config keys
   */
  private static final String CONFIG_NAMESPACE = "net.gregrapp.jhouse.services.AppleApnsService";
  private static final String CERT_PATH = "CERT_PATH";
  private static final String CERT_PASSWORD = "CERT_PASSWORD";
  private static final String INACTIVE_DEVICE_POLL_MINUTES = "INACTIVE_DEVICE_POLL_MINUTES";

  private static final Logger logger = LoggerFactory
      .getLogger(AppleApnsServiceImpl.class);

  private ApnsService apnsService;
  private ConfigService configService;

  private ScheduledExecutorService inactiveDeviceExecutor;

  @Autowired
  private ApnsDeviceTokenRepository apnsDeviceRepository;

  private int DEFAULT_POLL_MINUTES = 60;

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
          "Config option [%s.%s] is not set, using default value of [%d]",
          new Object[] { CONFIG_NAMESPACe, INACTIVE_DEVICE_POLL_MINUTES,
              pollMinutes });
    } else
    {
      try
      {
        pollMinutes = Integer.valueOf(strPollMinutes).intValue();
      } catch (NumberFormatException e)
      {
        logger.warn("Invalid value [%s] specified for config option [%s.%s]",
            new Object[] { strPollMinutes, CONFIG_NAMESPACE,
                INACTIVE_DEVICE_POLL_MINUTES });
      }
    }

    logger.debug("Starting Apple APNs inactive device monitor executor");
    inactiveDeviceExecutor = Executors.newSingleThreadScheduledExecutor();
    inactiveDeviceExecutor.scheduleAtFixedRate(new Runnable() {
      public void run()
      {
        if (apnsDeviceRepository)
        {
          Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
          for (String deviceToken : inactiveDevices.keySet())
          {
            ApnsDevice device = apnsDeviceRepository.findByToken(deviceToken);
            apnsDeviceRepository.purge(device);
          }
          apnsDeviceRepository.flush();
        }
      }
    }, 1, 60, TimeUnit.MINUTES);

  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.services.AppleApnsService#send(java.lang.String, java.lang.String, int)
   */
  @Override
  public void send(String token, String alertBody, int badge)
  {
    String payload = APNS.newPayload().alertBody(alertBody).shrinkBody("...")
        .badge(badge).build();
    apnsService.push(token, payload);
  }

  /**
   * Sets an instance of the ConfigService
   */
  @Autowired
  public void setConfigService(ConfigService configService)
  {
    logger.info("Configuring AppleApnsService");

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

    logger.debug("Creating APNs service instance");
    apnsService = APNS.newService().withCert(certPath, certPassword)
        .withSandboxDestination().build();

    this.monitorInactiveDevices();
  }
}
