/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import java.util.Map;

/**
 * @author Greg Rapp
 * 
 */
public abstract class AbstractInterface implements Interface
{
  /**
   * Interface properties
   */
  protected Map<String, String> properties;

  /**
   * Interface initialization status
   */
  protected boolean interfaceReady;

  public AbstractInterface(Map<String, String> properties)
  {
    this.interfaceReady = false;
    this.properties = properties;
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
}
