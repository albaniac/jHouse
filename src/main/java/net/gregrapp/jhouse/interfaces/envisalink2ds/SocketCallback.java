/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

/**
 * @author Greg Rapp
 *
 */
public interface SocketCallback
{
  /**
   * Data received callback
   * 
   * @param data data string
   */
  public void stringReceived(String data);
}
