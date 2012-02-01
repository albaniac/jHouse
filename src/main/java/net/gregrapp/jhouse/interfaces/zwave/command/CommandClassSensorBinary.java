/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * Z-Wave COMMAND_CLASS_SENSOR_BINARY interface
 *  
 * @author Greg Rapp
 * 
 */
public interface CommandClassSensorBinary extends CommandClass
{
  /**
   * Request a COMMAND_CLASS_SENSOR_BINARY_REPORT
   */
  public void commandClassSensorBinaryGet();

  /**
   * COMMAND_CLASS_SENSOR_BINARY_REPORT
   * 
   * @param value
   */
  public void commandClassSensorBinaryReport(int value);
}
