/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * Multilevel switch device class
 * 
 * @author Greg Rapp
 * 
 */
public interface MultilevelSwitch extends BinarySwitch
{
  /**
   * @param level switch level as a percentage (0 - 100)
   */
  public void setLevel(Integer level);

  /**
   * Initiate a level change
   * 
   * @param direction
   *          direction of level change (0 = up, 1 = down)
   */
  public void startLevelChange(Integer direction);

  /**
   * Stop a previous started level change
   */
  public void stopLevelChange();
}
