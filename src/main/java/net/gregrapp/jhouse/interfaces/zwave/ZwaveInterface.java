/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.HashMap;
import java.util.Map;

import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.device.types.ZwaveDevice;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveInterface extends AbstractInterface
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveInterface.class);
  
  private ApplicationLayer appLayer;
  private Map<Integer, ZwaveDevice> devices = new HashMap<Integer, ZwaveDevice>();
  
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
      appLayer.zwaveSendData(nodeId, data, new TXOption[] {TXOption.TransmitOptionAcknowledge, TXOption.TransmitOptionAutoRoute});
    } catch (FrameLayerException e)
    {
      return false;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  public void attachDevice(Device device)
  {
    if (!(device instanceof ZwaveDevice))
      throw new ClassCastException("Cannot attach non ZWave device to this interface");
   
    ZwaveDevice zwaveDevice = (ZwaveDevice)device;
    this.devices.put(zwaveDevice.getNodeId(), zwaveDevice);
  }

}
