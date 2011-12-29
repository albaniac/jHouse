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
  protected int receivedBytes;
  
  protected int transmittedBytes;
  
  public AbstractTransport(List<String> config)
  {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.transports.Transport#getReceivedBytes()
   */
  public int getReceivedBytes()
  {
    return receivedBytes;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.transports.Transport#getTransmittedBytes()
   */
  public int getTransmittedBytes()
  {
    return transmittedBytes;
  }

  protected void incrementReceivedBytes()
  {
    receivedBytes++;
  }
  
  protected void incrementTransmittedBytes()
  {
    transmittedBytes++;
  }  
}
