/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * Z-Wave COMMAND_CLASS_HAIL interface
 *  
 * @author Greg Rapp
 *
 */
public interface CommandClassHail extends CommandClass
{

  /**
   * Hail received from device
   */
  public void hail();
}
