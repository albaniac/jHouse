/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

/**
 * @author Greg Rapp
 *
 */
public interface InterfaceCallback
{
  /**
   * Called by an interface to notify attached device drivers that it is ready
   * to accept data
   */
  public void interfaceReady();
}
