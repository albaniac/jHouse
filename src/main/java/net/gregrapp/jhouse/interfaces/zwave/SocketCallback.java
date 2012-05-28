/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

/**
 * @author Greg Rapp
 *
 */
public interface SocketCallback
{
  /**
   * Data byte received callback
   * 
   * @param data data byte
   */
  public void byteReceived(int data);
}
