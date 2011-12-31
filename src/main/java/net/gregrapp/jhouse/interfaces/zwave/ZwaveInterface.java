/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CmdBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.transports.TransportException;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveInterface extends AbstractInterface
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveInterface.class);
  
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
    ApplicationLayer appLayer = new ApplicationLayerImpl(sessionLayer);
    
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
      appLayer.zwaveSendData(14, new int[] {0x20, CmdBasic.BasicSet.get(), CmdBasic.BasicOn.get()}, TXOption.TransmitOptionAutoRoute);
      Thread.sleep(2000);
      appLayer.zwaveSendData(14, new int[] {0x20, CmdBasic.BasicSet.get(), CmdBasic.BasicOff.get()}, TXOption.TransmitOptionAutoRoute);
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

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

}
