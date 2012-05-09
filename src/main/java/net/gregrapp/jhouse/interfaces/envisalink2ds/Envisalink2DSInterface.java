/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.interfaces.InterfaceCallback;
import net.gregrapp.jhouse.interfaces.TransportInterface;
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for the Envisalink 2DS DSC security panel interface
 * 
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSInterface extends TransportInterface implements
    Envisalink2DSFrameLayerAsyncCallback
{
  // Amount of time that must pass before keepalive trips
  private static final int keepaliveHoldtimeSeconds = 30;

  // Interval between keepalives
  private static final int keepaliveIntervalSeconds = 10;

  private static final Logger logger = LoggerFactory
      .getLogger(Envisalink2DSInterface.class);

  private Envisalink2DSFrameLayer frameLayer;

  // Keepalive executor
  private ScheduledExecutorService keepaliveExecutor;

  // Last frame receive epoch time
  private long lastFrameReceiveTime = 0;

  private DeviceDriver panel;

  public Envisalink2DSInterface(Transport transport)
  {
    super(transport);
    logger.info("Instantiating interface [{}]", this.getClass().getName());
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
  }

  private void connect()
  {
    try
    {
      this.transport.init();
    } catch (TransportException e)
    {
      logger.error(
          "Error opening transport, aborting Envisalink 2DS initialization", e);
    }

    frameLayer = new Envisalink2DSFrameLayerImpl(transport);
    frameLayer.setCallbackHandler(this);
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
    logger.info("Destroying Envisalink 2DS interface");
    keepaliveExecutor.shutdownNow();
    frameLayer.destroy();
  }

  @Override
  public void frameReceived(Envisalink2DSDataFrame frame)
  {
    // Set last frame received time to now
    this.lastFrameReceiveTime = System.currentTimeMillis();

    if (frame.getCommand().equals("505")) // Login interaction
    {
      int type = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).loginInteraction(type);
    } /*
       * else if (frame.getCommand().equals("570")) // Label broadcast { int
       * zone = Integer.parseInt(frame.getData().substring(0, 3)); String label
       * = itrim(frame.getData().substring(3).trim()); if (panel != null &&
       * panel instanceof Envisalink2DSCallback) ((Envisalink2DSCallback)
       * panel).broadcastLabels(zone, label); }
       */else if (frame.getCommand().equals("601")) // Zone alarm
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int zone = Integer.parseInt(frame.getData().substring(1, 4));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneAlarm(partition, zone);
    } else if (frame.getCommand().equals("602")) // Zone alarm Restore
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int zone = Integer.parseInt(frame.getData().substring(1, 4));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneAlarmRestore(partition, zone);
    } else if (frame.getCommand().equals("609")) // Zone open
    {
      int zone = Integer.parseInt(frame.getData().substring(0, 3));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneOpen(zone);
    } else if (frame.getCommand().equals("610")) // Zone restored
    {
      int zone = Integer.parseInt(frame.getData().substring(0, 3));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).zoneRestore(zone);
    } else if (frame.getCommand().equals("650")) // Partition ready
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionReady(partition);
    } else if (frame.getCommand().equals("651")) // Partition not ready
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionNotReady(partition);
    } else if (frame.getCommand().equals("652")) // Partition armed
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int mode = Integer.parseInt(frame.getData().substring(1, 2));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).paritionArmed(partition, mode);
    } else if (frame.getCommand().equals("654")) // Partition in alarm
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionInAlarm(partition);
    } else if (frame.getCommand().equals("655")) // Partition disarmed
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionDisarmed(partition);
    } else if (frame.getCommand().equals("656")) // Exit delay in progress
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionExitDelay(partition);
    } else if (frame.getCommand().equals("657")) // Entry delay in progress
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionEntryDelay(partition);
    } else if (frame.getCommand().equals("670")) // Invalid access code
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).invalidAccessCode(partition);
    } else if (frame.getCommand().equals("672")) // Failed to arm
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionFailedToArm(partition);
    } else if (frame.getCommand().equals("673")) // Partition busy
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).partitionBusy(partition);
    } else if (frame.getCommand().equals("700")) // User closing
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int userCode = Integer.parseInt(frame.getData().substring(1, 5));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).userClosing(partition, userCode);
    } else if (frame.getCommand().equals("701")) // Special closing
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).specialClosing(partition);
    } else if (frame.getCommand().equals("750")) // User opening
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int userCode = Integer.parseInt(frame.getData().substring(1, 5));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).userOpening(partition, userCode);
    } else if (frame.getCommand().equals("900")) // Code required
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof Envisalink2DSCallback)
        ((Envisalink2DSCallback) panel).codeRequired(partition);
    }
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
    this.connect();

    this.interfaceReady = true;

    if ((panel != null) && (panel instanceof InterfaceCallback))
    {
      ((InterfaceCallback) panel).interfaceReady();
    }

    this.startKeepalives();
  }

  /**
   * Send command to panel
   * 
   * @param command
   */
  public void sendCommand(String command)
  {
    this.sendCommand(command, "");
  }

  /**
   * Send command to panel
   * 
   * @param command
   * @param data
   */
  public void sendCommand(String command, String data)
  {
    try
    {
      frameLayer.write(new Envisalink2DSDataFrame(command, data));
    } catch (Envisalink2DSFrameLayerException e)
    {
      logger.warn(
          "Error sending data to DSC security system Envisalink 2DS module", e);
    }
  }

  /**
   * Start keepalive executor
   */
  private void startKeepalives()
  {
    logger.debug("Starting keepalive executor");

    // Set last frame time to now before we start so we don't freak out when we
    // start
    this.lastFrameReceiveTime = System.currentTimeMillis();

    keepaliveExecutor = Executors.newSingleThreadScheduledExecutor();
    keepaliveExecutor.scheduleWithFixedDelay(new Runnable()
    {
      public void run()
      {
        if ((System.currentTimeMillis() - lastFrameReceiveTime) > (keepaliveHoldtimeSeconds * 1000))
        {
          logger.error("Keepalive holdtime expired, resetting transport");

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
          logger.debug("Sending keepalive request");
          sendCommand("000");
        }

      }
    }, 0, keepaliveIntervalSeconds, TimeUnit.SECONDS);
  }
}
