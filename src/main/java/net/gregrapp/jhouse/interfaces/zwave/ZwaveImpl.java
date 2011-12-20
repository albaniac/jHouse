/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveImpl extends AbstractInterface
{
  private static final Logger logger = LoggerFactory.getLogger(ZwaveImpl.class);

  /**
   * 
   */
  public ZwaveImpl()
  {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  public void init()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#setTransport(net.gregrapp.jhouse.transports.Transport)
   */
  public void setTransport(Transport transport)
  {
    logger.debug("Transport set to instance of {}", transport.getClass().getName());
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

}
