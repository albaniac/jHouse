/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

/**
 * @author Greg Rapp
 *
 */
public class DSCIT100Interface extends AbstractInterface implements DSCIT100FrameLayerAsyncCallback
{
  private static final Logger logger = LoggerFactory
      .getLogger(DSCIT100Interface.class);
  
  public DSCIT100Interface(Transport transport)
  {
    super(transport);
  }

  /* (non-Javadoc)
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
      logger.error("Error opening transport, aborting DSC IT100 initialization", e);
    }

    DSCIT100FrameLayer frameLayer = new DSCIT100FrameLayerImpl(transport);
    frameLayer.setCallbackHandler(this);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#attachDevice(net.gregrapp.jhouse.device.types.Device)
   */
  @Override
  public void attachDevice(Device device)
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
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
    // TODO Auto-generated method stub
    
  }

}
