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
  
  /**
   * @param direction direction of level change (0 = up, 1 = down)
   */
  public void startLevelChange(int direction);
  public void stopLevelChange();
}
