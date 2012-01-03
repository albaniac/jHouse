/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.device.types.ZwaveDevice;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveInterface extends AbstractInterface implements ApplicationLayerAsyncCallback
{
  public ZwaveInterface(Transport transport)
  {
    super(transport);
  }

  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveInterface.class);
  
  private ApplicationLayer appLayer;
  private Map<Integer, ArrayList<ZwaveDevice>> devices = new HashMap<Integer, ArrayList<ZwaveDevice>>();
  
  public void attachDevice(Device device)
  {
    if (!(device instanceof ZwaveDevice))
      throw new ClassCastException("Cannot attach non ZWave device to this interface");
   
    ZwaveDevice zwaveDevice = (ZwaveDevice)device;
    
    if (devices.containsKey(zwaveDevice.getNodeId()))
    {
      if (devices.get(zwaveDevice.getNodeId()) instanceof ArrayList)
      {
        devices.get(zwaveDevice.getNodeId()).add(zwaveDevice);
      } else
      {
        ArrayList<ZwaveDevice> tmp = new ArrayList<ZwaveDevice>();
        tmp.add(zwaveDevice);
        devices.put(zwaveDevice.getNodeId(), tmp);
      }
    } else
    {
      ArrayList<ZwaveDevice> tmp = new ArrayList<ZwaveDevice>();
      tmp.add(zwaveDevice);
      devices.put(zwaveDevice.getNodeId(), tmp);
    }    
  }
  
  public void dataPacketReceived(CommandType cmd, DataPacket packet)
  {
    int[] payload = packet.getPayload();
    if (cmd == DataFrame.CommandType.CmdApplicationCommandHandler)
    {
      if (payload[3] == CommandClass.COMMAND_CLASS_BASIC.get())
      {
        logger.info("COMMAND_CLASS_BASIC");
        if (payload[4] == CommandBasic.BASIC_REPORT.get())
        {
          logger.info("Received BASIC_REPORT from node {} with a value of {}", payload[1], payload[5]);
          int nodeId = payload[1];
          if (devices.containsKey(nodeId))
          {
            int value = payload[5];
            for (Object device : devices.get(nodeId))
              if (device instanceof CommandClassBasic)
                ((CommandClassBasic)device).commandClassBasicReport(value);
          }
        }
      }
    }    
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  public void init()
  {
    try
    {
      this.transport.init();
    } catch (TransportException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    FrameLayer frameLayer = new FrameLayerImpl(this.transport);
    SessionLayer sessionLayer = new SessionLayerImpl(frameLayer);
    appLayer = new ApplicationLayerImpl(sessionLayer);
    appLayer.setCallbackHandler(this);
    
    try
    {
      appLayer.getZwaveVersion();
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
    }
    
    try
    {
      appLayer.enumerateNodes();
    } catch (FrameLayerException e)
    {
      logger.error("Error enumerating ZWave nodes", e);
    } catch (ApplicationLayerException e)
    {
      logger.error("Error enumerating ZWave nodes", e);
    }
    
    try
    {
      appLayer.zwaveSendData(14, new int[] {0x20, CommandBasic.BASIC_SET.get(), CommandBasic.BASIC_ON.get()}, new TXOption[] {TXOption.TransmitOptionAcknowledge, TXOption.TransmitOptionAutoRoute});
      Thread.sleep(2000);
      appLayer.zwaveSendData(14, new int[] {0x20, CommandBasic.BASIC_SET.get(), CommandBasic.BASIC_OFF.get()}, new TXOption[] {TXOption.TransmitOptionAcknowledge, TXOption.TransmitOptionAutoRoute});
    } catch (FrameLayerException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public boolean zwaveSendData(int nodeId, int... data)
  {
    try
    {
      TXStatus txStatus = appLayer.zwaveSendData(nodeId, data, new TXOption[] {TXOption.TransmitOptionAcknowledge, TXOption.TransmitOptionAutoRoute});
      if (txStatus == TXStatus.CompleteOk)
        return true;
    } catch (FrameLayerException e)
    {
      return false;
    }

    return false;
  }

  
}
