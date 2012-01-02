/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * @author Greg Rapp
 * Multilevel switch device class
 */
public interface MultilevelSwitch extends BinarySwitch
{
  public void setLevel(int level);
}
