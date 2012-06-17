/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

import java.util.Set;

/**
 * Z-Wave COMMAND_CLASS_THERMOSTAT_SETPOINT interface
 * 
 * @author Greg Rapp
 * 
 */
public interface CommandClassThermostatSetpoint extends CommandClass
{
  public enum SetpointType
  {

    /**
     * AUTOCHANGEOVER - 0x0A
     */
    AUTOCHANGEOVER(0x0A),
    /**
     * AWAYHEATING - 0x0D
     */
    AWAYHEATING(0x0D),
    /**
     * COOLING - 0x02
     */
    COOLING(0x02),
    /**
     * COOLINGECON - 0x0C
     */
    COOLINGECON(0x0C),
    /**
     * DRYAIR - 0x08
     */
    DRYAIR(0x08),
    /**
     * FURNACE - 0x07
     */
    FURNACE(0x07),
    /**
     * HEATING - 0x01
     */
    HEATING(0x01),
    /**
     * HEATINGECON - 0x0B
     */
    HEATINGECON(0x0B),
    /**
     * MOISTAIR - 0x09
     */
    MOISTAIR(0x09);

    public static SetpointType getByVal(int value)
    {
      for (SetpointType t : SetpointType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }

    private int value;

    SetpointType(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * Request thermostat report indicating current setpoint
   * 
   * @param type
   *          setpoint type to request
   */
  public void commandClassThermostatSetpointGet(SetpointType type);

  /**
   * Thermostat setpoint report
   * 
   * @param type
   *          setpoint type
   * @param setpoint
   *          reported setpoint
   */
  public void commandClassThermostatSetpointReport(SetpointType type,
      int setpoint);

  /**
   * Set the thermostat setpoint
   * 
   * @param type
   *          setpoint type
   * @param setpoint
   *          setpoint to set
   */
  public void commandClassThermostatSetpointSet(SetpointType type, int setpoint);

  /**
   * Get thermostat supported setpoints
   */
  public void commandClassThermostatSetpointSupportedGet();

  /**
   * Thermostat supported setpoint report
   * 
   * @param setpoints
   *          supported setpoints
   */
  public void commandClassThermostatSetpointSupportedReport(
      Set<SetpointType> setpoints);
}
