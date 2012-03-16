/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

/**
 * @author Greg Rapp
 * 
 */
public abstract class AbstractInterface implements Interface
{
  protected boolean interfaceReady;

  public AbstractInterface()
  {
    this.interfaceReady = false;
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
