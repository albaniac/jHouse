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
  private static final Logger logger = LoggerFactory
      .getLogger(AbstractInterface.class);

  protected boolean interfaceReady;

  protected Transport transport;

  public AbstractInterface(Transport transport)
  {
    this.interfaceReady = false;
    this.setTransport(transport);
  }

  public Transport getTransport()
  {
    return this.transport;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#isReady()
   */
  public boolean isReady()
  {
    return interfaceReady;
  }

  public void setTransport(Transport transport)
  {
    logger.debug("Setting Transport on this Interface to instance of {}",
        transport.getClass().getName());
    this.transport = transport;
    this.init();
  }
}
