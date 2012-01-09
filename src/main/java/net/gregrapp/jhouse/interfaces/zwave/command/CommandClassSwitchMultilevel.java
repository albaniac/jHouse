/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * @author Greg Rapp
 * 
 */
public interface CommandClassSwitchMultilevel extends CommandClass
{
  /**
   * Request switch report indicating current level
   */
  public void commandClassSwitchMultilevelGet();

  /**
   * Switch report callback indicating current level
   * 
   * @param value
   *          level
   */
  public void commandClassSwitchMultilevelReport(int value);

  /**
   * Set switch level
   * 
   * @param value
   *          level
   */
  public void commandClassSwitchMultilevelSet(int value);

  /**
   * Start switch level change
   * 
   * @param direction
   */
  public void commandClassSwitchMultilevelStartLevelChange(int direction);

  /**
   * Stop switch level change
   */
  public void commandClassSwitchMultilevelStopLevelChange();
}
