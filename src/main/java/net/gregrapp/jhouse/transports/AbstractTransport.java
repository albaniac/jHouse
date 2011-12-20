/**
 * 
 */
package net.gregrapp.jhouse.transports;

import java.util.List;

/**
 * @author Greg Rapp
 *
 */
public abstract class AbstractTransport implements Transport
{
  /**
   * Configuration for this AbstractTransport
   */
  protected List<String> config;
  
  public AbstractTransport(List<String> config)
  {
    this.config = config;
  }
}
