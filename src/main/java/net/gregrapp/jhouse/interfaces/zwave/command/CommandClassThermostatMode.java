/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * Z-Wave COMMAND_CLASS_THERMOSTAT_MODE interface
 * 
 * @author Greg Rapp
 * 
 */
public interface CommandClassThermostatMode extends CommandClass
{
  /**
   * Request thermostat report indicating current mode
   */
  public void commandClassThermostatModeGet();

  /**
   * Thermostat mode report
   * 
   * @param mode
   *          reported mode
   */
  public void commandClassThermostatModeReport(int mode);

  /**
   * Set the thermostat mode
   * 
   * @param mode
   *          mode to set (0 - off, 1 - heat, 2 - cool, 3 - auto, 4 - aux/emer
   *          heat, 5 - resume, 6 - fan only, 7 - furnace, 8 - dry air, 9 -
   *          moist air, 10 - auto changeover)
   */
  public void commandClassThermostatModeSet(int mode);

  /**
   * Get thermostat supported modes
   */
  public void commandClassThermostatModeSupportedGet();

  /**
   * Thermostat supported mode report
   * 
   * @param mode
   *          supported modes
   */
  public void commandClassThermostatModeSupportedReport(int modes);
}
