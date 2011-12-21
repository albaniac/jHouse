/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 *
 */
public abstract class AbstractInterface implements Interface
{
  private static final Logger logger = LoggerFactory.getLogger(AbstractInterface.class);
  
  protected Transport transport;
  
  public void setTransport(Transport transport)
  {
    logger.debug("Setting Transport on this Interface to instance of {}", transport.getClass().getName());
    this.transport = transport;
    transport.init();
  }
  
  public Transport getTransport()
  {
    return this.transport;
  }
}
