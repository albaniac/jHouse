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
  public void commandClassSwitchMultilevelStartLevelChange();
  public void commandClassSwitchMultilevelStopLevelChange();
  public void commandClassSwitchMultilevelSet(int value);
  public void commandClassSwitchMultilevelGet();
  public void commandClassSwitchMultilevelReport(int value); 
}
