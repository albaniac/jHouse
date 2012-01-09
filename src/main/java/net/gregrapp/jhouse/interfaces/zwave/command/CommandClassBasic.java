/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * ZWave COMMAND_CLASS_BASIC interface
 * 
 * @author Greg Rapp
 */
public interface CommandClassBasic extends CommandClass
{
  /**
   * Request basic report
   */
  public void commandClassBasicGet();

  /**
   * Basic report callback
   * 
   * @param value
   */
  public void commandClassBasicReport(int value);

  /**
   * Command class basic set
   * 
   * @param value
   *          basic value
   */
  public void commandClassBasicSet(int value);
}
