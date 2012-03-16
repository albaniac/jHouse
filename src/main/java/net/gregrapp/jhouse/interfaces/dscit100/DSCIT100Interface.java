/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.interfaces.InterfaceCallback;
import net.gregrapp.jhouse.interfaces.TransportInterface;
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for the DSC IT100 Security Panel interface
 * 
 * @author Greg Rapp
 * 
 */
public class DSCIT100Interface extends TransportInterface implements
    DSCIT100FrameLayerAsyncCallback
{
  private static final Logger logger = LoggerFactory
      .getLogger(DSCIT100Interface.class);

  private DSCIT100FrameLayer frameLayer;

  private DeviceDriver panel;

  public DSCIT100Interface(Transport transport)
  {
    super(transport);
    logger.info("Instantiating interface {}", this.getClass().getName());
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
    if (!(driver instanceof DSCIT100Callback))
    {
      throw new ClassCastException(
          "Device driver must implement DSCIT100Callback");
    } else
    {
      logger.info("Attaching device driver: {}", driver.getClass().getName());
      this.panel = driver;

      if (this.interfaceReady && (driver instanceof InterfaceCallback))
      {
        ((InterfaceCallback) driver).interfaceReady();
      }
    }
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
    logger.info("Destroying DSCIT100 interface");
    frameLayer.destroy();
  }

  @Override
  public void frameReceived(DSCIT100DataFrame frame)
  {
    if (frame.getCommand().equals("570")) // Label broadcast
    {
      int zone = Integer.parseInt(frame.getData().substring(0, 3));
      String label = itrim(frame.getData().substring(3).trim());
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).broadcastLabels(zone, label);
    }
    else if (frame.getCommand().equals("601")) // Zone alarm
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int zone = Integer.parseInt(frame.getData().substring(1, 4));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).zoneAlarm(partition, zone);
    }
    else if (frame.getCommand().equals("602")) // Zone alarm Restore
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int zone = Integer.parseInt(frame.getData().substring(1, 4));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).zoneAlarmRestore(partition, zone);
    }
    else if (frame.getCommand().equals("609")) // Zone open
    {
      int zone = Integer.parseInt(frame.getData().substring(0, 3));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).zoneOpen(zone);
    }
    else if (frame.getCommand().equals("610")) // Zone restored
    {
      int zone = Integer.parseInt(frame.getData().substring(0, 3));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).zoneRestore(zone);
    }
    else if (frame.getCommand().equals("650")) // Partition ready
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionReady(partition);
    }
    else if (frame.getCommand().equals("651")) // Partition not ready
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionNotReady(partition);
    }
    else if (frame.getCommand().equals("652")) // Partition armed
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int mode = Integer.parseInt(frame.getData().substring(1, 2));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).paritionArmed(partition, mode);
    }
    else if (frame.getCommand().equals("654")) // Partition in alarm
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionInAlarm(partition);
    }
    else if (frame.getCommand().equals("655")) // Partition disarmed
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionDisarmed(partition);
    }
    else if (frame.getCommand().equals("656")) // Exit delay in progress
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionExitDelay(partition);
    }
    else if (frame.getCommand().equals("657")) // Entry delay in progress
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionEntryDelay(partition);
    }
    else if (frame.getCommand().equals("670")) // Invalid access code
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).invalidAccessCode(partition);
    }
    else if (frame.getCommand().equals("672")) // Failed to arm
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionFailedToArm(partition);
    }
    else if (frame.getCommand().equals("673")) // Partition busy
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).partitionBusy(partition);
    }
    else if (frame.getCommand().equals("700")) // User closing
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int userCode = Integer.parseInt(frame.getData().substring(1, 5));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).userClosing(partition, userCode);
    }
    else if (frame.getCommand().equals("701")) // Special closing
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).specialClosing(partition);
    }
    else if (frame.getCommand().equals("750")) // User opening
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      int userCode = Integer.parseInt(frame.getData().substring(1, 5));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).userOpening(partition, userCode);
    }
    else if (frame.getCommand().equals("900")) // Code required
    {
      int partition = Integer.parseInt(frame.getData().substring(0, 1));
      if (panel != null && panel instanceof DSCIT100Callback)
        ((DSCIT100Callback) panel).codeRequired(partition);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  @Override
  public void init()
  {
    try
    {
      this.transport.init();
    } catch (TransportException e)
    {
      logger.error(
          "Error opening transport, aborting DSC IT100 initialization", e);
    }

    frameLayer = new DSCIT100FrameLayerImpl(transport);
    frameLayer.setCallbackHandler(this);

    this.interfaceReady = true;

    if ((panel != null) && (panel instanceof InterfaceCallback))
    {
      ((InterfaceCallback)panel).interfaceReady();
    }
  }

  /* Replaces multiple whitespace between words with single whitespace */
  private String itrim(String source)
  {
    return source.replaceAll("\\b\\s{2,}\\b", " ");
  }

  public void sendCommand(String command, String data)
  {
    try
    {
      frameLayer.write(new DSCIT100DataFrame(command, data));
    } catch (DSCIT100FrameLayerException e)
    {
      logger.warn("Error sending data to DSC security system IT100 module", e);
    }
  }
}
