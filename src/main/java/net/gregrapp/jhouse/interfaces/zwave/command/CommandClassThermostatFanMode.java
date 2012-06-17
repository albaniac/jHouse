/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * Z-Wave COMMAND_CLASS_THERMOSTAT_FAN_MODE interface
 * 
 * @author Greg Rapp
 * 
 */
public interface CommandClassThermostatFanMode extends CommandClass
{
  /**
   * Request thermostat report indicating current fan mode
   */
  public void commandClassThermostatFanModeGet();

  /**
   * Thermostat fan mode report
   * 
   * @param mode
   *          reported fan mode
   */
  public void commandClassThermostatFanModeReport(int mode);

  /**
   * Set the thermostat fan mode
   * 
   * @param mode
   *          fan mode to set (0 - auto/auto low, 1 - on/on low, 2 - auto high, 3 - on high)
   */
  public void commandClassThermostatFanModeSet(int mode);

  /**
   * Get thermostat supported fan modes
   */
  public void commandClassThermostatFanModeSupportedGet();

  /**
   * Thermostat supported fan mode report
   * 
   * @param mode
   *          supported fan modes
   */
  public void commandClassThermostatFanModeSupportedReport(int modes);
}
