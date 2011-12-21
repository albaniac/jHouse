/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import java.util.List;

/**
 * @author Greg Rapp
 *
 */
public interface InterfaceWriter
{
  public void sendData(List<Integer> buffer);
  public void sendData(List<Integer> buffer, List<Object> params);
}
