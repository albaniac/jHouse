/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.InterfaceCallback;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Interface for the Envisalink 2DS DSC security panel interface
 * 
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSInterface extends AbstractInterface implements
    Envisalink2DSFrameLayerAsyncCallback
{
  // Amount of time that must pass before connection is restarted
  private static final int keepaliveHoldtimeSeconds = 60;

  // Interval between keep alive attempts
  private static final int keepaliveIntervalSeconds = 20;

  private static final XLogger logger = XLoggerFactory
      .getXLogger(Envisalink2DSInterface.class);

  // Property - HOST
  private static final String PROPERTY_HOST = "HOST";

  // Property - PORT
  private static final String PROPERTY_PORT = "PORT";

  private Envisalink2DSFrameLayer frameLayer;

  // Keep alive executor
  private ScheduledExecutorService keepaliveExecutor;

  // Last frame received epoch time
  private long lastFrameReceiveTime = 0;

  private DeviceDriver panel;

  public Envisalink2DSInterface(Map<String, String> properties)
  {
    super(properties);

    logger.entry();

    this.init();

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.Interface#attachDeviceDriver(net.gregrapp
   * .jhouse.device.drivers.impl.types.DeviceDriver)
   */
  @Override
  public void attachDeviceDriver(DeviceDriver driver)
  {
    logger.entry(driver);

    if (!(driver instanceof Envisalink2DSCallback))
    {
      throw new ClassCastException(
          "Device driver must implement Envisalink2DSCallback");
    } else
    {
      logger.info("Attaching device driver [{}]", driver.getClass().getName());
      this.panel = driver;

      if (this.interfaceReady && (driver instanceof InterfaceCallback))
      {
        ((InterfaceCallback) driver).interfaceReady();
      }
    }

    logger.exit();
  }

  /**
   * Connect to the Envisalink 2DS
   * 
   * @return true if successful, false if unsuccessful
   */
  private boolean connect()
  {
    logger.entry();

    if (this.properties != null && this.properties.containsKey(PROPERTY_HOST)
        && this.properties.containsKey(PROPERTY_PORT))
    {
      String host = this.properties.get(PROPERTY_HOST);
      logger.debug("Read property [{}] with value [{}]", PROPERTY_HOST, host);

      int port;

      try
      {
        port = Integer.valueOf(this.properties.get(PROPERTY_PORT));
        logger.debug("Read property [{}] with value [{}]", PROPERTY_PORT, port);
      } catch (NumberFormatException e)
      {
        logger.error(
            "Property [{}] is invalid - [{}] is not a valid TCP port number",
            PROPERTY_PORT, this.properties.get(PROPERTY_PORT));
        logger.exit(false);
        return false;
      }

      frameLayer = new Envisalink2DSFrameLayerImpl(host, port);
      frameLayer.setCallbackHandler(this);

      logger.exit(true);
      return true;
    }

    logger.exit(false);
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  @Override
  @PreDestroy
  public void destroy()
  {
    logger.entry();

    logger.info("Destroying Envisalink 2DS interface");
    keepaliveExecutor.shutdownNow();
    frameLayer.destroy();

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.envisalink2ds.
   * Envisalink2DSFrameLayerAsyncCallback
   * #frameReceived(net.gregrapp.jhouse.interfaces
   * .envisalink2ds.Envisalink2DSDataFrame)
   */
  @Override
  public void frameReceived(Envisalink2DSDataFrame frame)
  {
    logger.entry(frame);

    // Set last frame received time to now
    this.lastFrameReceiveTime = System.currentTimeMillis();

    if (frame.getCommand().equals("505")) // Login interaction
    {
      int type = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).loginInteraction(type);
    } else if (frame.getCommand().equals("601")) // Zone alarm
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      int zone = safeParseInt(frame.getData(), 1, 4);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneAlarm(partition, zone);
    } else if (frame.getCommand().equals("602")) // Zone alarm Restore
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      int zone = safeParseInt(frame.getData(), 1, 4);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneAlarmRestore(partition, zone);
    } else if (frame.getCommand().equals("609")) // Zone open
    {
      int zone = safeParseInt(frame.getData(), 0, 3);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneOpen(zone);
    } else if (frame.getCommand().equals("610")) // Zone restored
    {
      int zone = safeParseInt(frame.getData(), 0, 3);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneRestore(zone);
    } else if (frame.getCommand().equals("650")) // Partition ready
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionReady(partition);
    } else if (frame.getCommand().equals("651")) // Partition not ready
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionNotReady(partition);
    } else if (frame.getCommand().equals("652")) // Partition armed
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      int mode = safeParseInt(frame.getData(), 1, 2);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).paritionArmed(partition, mode);
    } else if (frame.getCommand().equals("654")) // Partition in alarm
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionInAlarm(partition);
    } else if (frame.getCommand().equals("655")) // Partition disarmed
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionDisarmed(partition);
    } else if (frame.getCommand().equals("656")) // Exit delay in progress
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionExitDelay(partition);
    } else if (frame.getCommand().equals("657")) // Entry delay in progress
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionEntryDelay(partition);
    } else if (frame.getCommand().equals("670")) // Invalid access code
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).invalidAccessCode(partition);
    } else if (frame.getCommand().equals("672")) // Failed to arm
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionFailedToArm(partition);
    } else if (frame.getCommand().equals("673")) // Partition busy
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionBusy(partition);
    } else if (frame.getCommand().equals("700")) // User closing
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      int userCode = safeParseInt(frame.getData(), 1, 5);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).userClosing(partition, userCode);
    } else if (frame.getCommand().equals("701")) // Special closing
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).specialClosing(partition);
    } else if (frame.getCommand().equals("750")) // User opening
    {
      int partition = safeParseInt(frame.getData(), 0, 1);
      int userCode = safeParseInt(frame.getData(), 1, 5);
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).userOpening(partition, userCode);
    } else if (frame.getCommand().equals("900")) // Code required
    {
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).codeRequired();
    }

    logger.exit();
  }

  /* Replaces multiple whitespace between words with single whitespace */
  /*
   * private String itrim(String source) { return
   * source.replaceAll("\\b\\s{2,}\\b", " "); }
   */

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  @Override
  public void init()
  {
    logger.entry();

    if (this.connect())
    {
      this.interfaceReady = true;

      if ((panel != null) && (panel instanceof InterfaceCallback))
      {
        ((InterfaceCallback) panel).interfaceReady();
      }

      this.startKeepalives();
    }

    logger.exit();
  }

  /**
   * Safely parse a string of integers
   * 
   * @param data
   *          integer string to parse
   * @param beginIndex
   *          the beginning index, inclusive
   * @param endIndex
   *          the ending index, exclusive
   * @return the parsed number or null if invalid
   */
  public Integer safeParseInt(String data, int beginIndex, int endIndex)
  {
    int retVal = 0;

    if ((beginIndex >= 0) && (endIndex > 0) && (endIndex > beginIndex)
        && data != null && data.length() >= (endIndex - beginIndex))
    {
      String subData = null;
      try
      {
        subData = data.substring(beginIndex, endIndex);
      } catch (IndexOutOfBoundsException e)
      {
        return null;
      }

      if (subData != null)
      {
        try
        {
          retVal = Integer.parseInt(subData);
          return (Integer) retVal;
        } catch (NumberFormatException e)
        {
          return null;
        }
      } else
      {
        return null;
      }
    } else
    {
      return null;
    }
  }

  /**
   * Send command to panel
   * 
   * @param command
   */
  public void sendCommand(String command)
  {
    logger.entry(command);

    this.sendCommand(command, "");

    logger.exit();
  }

  /**
   * Send command to panel
   * 
   * @param command
   * @param data
   */
  public void sendCommand(String command, String data)
  {
    logger.entry(command, data);

    try
    {
      frameLayer.write(new Envisalink2DSDataFrame(command, data));
    } catch (Envisalink2DSFrameLayerException e)
    {
      logger.warn(
          "Error sending data to DSC security system Envisalink 2DS module", e);
    }

    logger.exit();
  }

  /**
   * Start keep alive executor
   */
  private void startKeepalives()
  {
    logger.entry();

    logger.debug("Starting keepalive executor");

    // Set last frame time to now before we start so we don't freak out when we
    // start
    this.lastFrameReceiveTime = System.currentTimeMillis();

    keepaliveExecutor = Executors.newSingleThreadScheduledExecutor();
    keepaliveExecutor.scheduleWithFixedDelay(new Runnable()
    {
      public void run()
      {
        logger.entry();

        if ((System.currentTimeMillis() - lastFrameReceiveTime) > (keepaliveHoldtimeSeconds * 1000))
        {
          logger.error("Keep alive holdtime expired, resetting transport");

          frameLayer.destroy();

          connect();

          try
          {
            // Give the interface time to reconnect before resuming keepalives
            Thread.sleep(20000);
          } catch (InterruptedException e)
          {
          }
        } else
        {
          if ((System.currentTimeMillis() - lastFrameReceiveTime) > keepaliveIntervalSeconds)
          {
            logger.debug("Sending keep alive request");
            sendCommand("000");
          }
        }

        logger.exit();
      }
    }, 0, keepaliveIntervalSeconds, TimeUnit.SECONDS);

    logger.exit();
  }
}
