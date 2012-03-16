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
public abstract class TransportInterface extends AbstractInterface
{
  private static final Logger logger = LoggerFactory
      .getLogger(TransportInterface.class);


  protected Transport transport;

  public TransportInterface(Transport transport)
  {
    super();
    this.setTransport(transport);
  }

  /**
   * @return a transport instance
   */
  public Transport getTransport()
  {
    return this.transport;
  }

  /**
   * Set the Transport to be utilized by this Interface
   * 
   * @param transport
   *          instance of transport
   */
  public void setTransport(Transport transport)
  {
    logger.debug("Setting Transport on this Interface to instance of {}",
        transport.getClass().getName());
    this.transport = transport;
    this.init();
  }
}
