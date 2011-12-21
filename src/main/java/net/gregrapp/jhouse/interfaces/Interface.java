/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 * 
 */
public interface Interface
{
  /**
   * Initialize the Interface
   */
  public void init();

  /**
   * Set the Transport to be utilized by this Interface
   * 
   * @param transport
   *          Instance of Transport
   */
  public void setTransport(Transport transport);

  /**
   * @return A Transport instance
   */
  public Transport getTransport();

  /**
   * Destroy the Interface
   */
  public void destroy();
}
