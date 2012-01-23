/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

/**
 * @author Greg Rapp
 * 
 */
public class DSCIT100Interface extends AbstractInterface implements
    DSCIT100FrameLayerAsyncCallback
{
  private static final Logger logger = LoggerFactory
      .getLogger(DSCIT100Interface.class);

  DSCIT100FrameLayer frameLayer;

  DSCIT100Callback panel;
  
  public DSCIT100Interface(Transport transport)
  {
    super(transport);
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.Interface#attachDeviceDriver(net.gregrapp
   * .jhouse.device.drivers.impl.types.DeviceDriver)
   */
  @Override
  public void attachDeviceDriver(DeviceDriver device)
  {
    if (!(device instanceof DSCIT100Callback))
    {
      throw new ClassCastException(
          "Device driver must implement DSCIT100Callback");
    }
    else
    {
      logger.info("Attaching device driver: {}", device.getClass().getName());
      this.panel = (DSCIT100Callback)device;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  @Override
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void frameReceived(DSCIT100DataFrame frame)
  {
    if (frame.getCommand().equals("570")) // Label broadcast
    {
      int zone = Integer.parseInt(frame.getData().substring(0, 3));
      String label = itrim(frame.getData().substring(3).trim());
      logger.debug("Received label broadcast for zone {}: {}", zone, label);
      panel.broadcastLabels(zone, label);
    }
    else if (frame.getCommand().equals("601")) // Zone alarm
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      int zone = Integer.parseInt(frame.getData().substring(1,4));
      logger.info("Zone alarm at partition {}, zone {}", partition, zone);
      panel.zoneAlarm(partition, zone);
    }
    else if (frame.getCommand().equals("602")) // Zone Alarm Restore
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      int zone = Integer.parseInt(frame.getData().substring(1,4));
      logger.info("Zone alarm restored at partition {}, zone {}", partition, zone);
      panel.zoneAlarmRestore(partition, zone);
    }
    else if (frame.getCommand().equals("609")) // Zone open
    {
      int zone = Integer.parseInt(frame.getData().substring(0,3));
      logger.debug("Zone opened: {}", zone);
      panel.zoneOpen(zone);
    }
    else if (frame.getCommand().equals("610")) // Zone restored
    {
      int zone = Integer.parseInt(frame.getData().substring(0,3));
      logger.debug("Zone restored: {}", zone);
      panel.zoneRestore(zone);
    }
    else if (frame.getCommand().equals("652")) // Partition armed
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      int mode = Integer.parseInt(frame.getData().substring(1,2));
      logger.debug("Partition {} armed, mode {}", partition, mode);
      panel.paritionArmed(partition, mode);
    }
    else if (frame.getCommand().equals("655")) // Partition disarmed
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      logger.debug("Partition disarmed: {}", partition);
      panel.partitionDisarmed(partition);
    }
    else if (frame.getCommand().equals("700")) // User closing
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      int userCode = Integer.parseInt(frame.getData().substring(1,5));
      logger.debug("User closing for partition {}: {}",partition,userCode);
      panel.userClosing(partition, userCode);
    }
    else if (frame.getCommand().equals("701")) // Special closing
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      logger.debug("Special closing for partition: {}", partition);
      panel.specialClosing(partition);
    }
    else if (frame.getCommand().equals("750")) // User opening
    {
      int partition = Integer.parseInt(frame.getData().substring(0,1));
      int userCode = Integer.parseInt(frame.getData().substring(1,5));
      logger.debug("User opening for partition {}: {}",partition,userCode);
      panel.userOpening(partition, userCode);
    }
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
  
  /* Replaces multiple whitespace between words with single blank */
  private String itrim(String source) {
      return source.replaceAll("\\b\\s{2,}\\b", " ");
  }

}
