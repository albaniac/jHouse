/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * @author Greg Rapp
 * ZWave COMMAND_CLASS_BASIC interface
 */
public interface CommandClassBasic extends CommandClass
{  
  public void commandClassBasicSet(int value);
  public void commandClassBasicGet();
  public void commandClassBasicReport(int value);
}
