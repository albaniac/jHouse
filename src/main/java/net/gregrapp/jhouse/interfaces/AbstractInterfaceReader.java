/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

/**
 * @author Greg Rapp
 *
 */
public abstract class AbstractInterfaceReader implements InterfaceReader, Runnable
{
  protected Interface iface = null;

  /**
   * 
   */
  public AbstractInterfaceReader(Interface iface)
  {
    this.iface = iface;
  }

}
