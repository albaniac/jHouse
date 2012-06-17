/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * @author Greg Rapp
 * 
 */
public interface Thermostat extends DeviceClass
{
  /**
   * Get the current cool setpoint
   * 
   * @return cool setpoint in degrees F
   */
  public int getCoolSetpoint();

  /**
   * Get the current heat setpoint
   * 
   * @return heat setpoint in degress F
   */
  public int getHeatSetpoint();

  /**
   * Set the cool setpoint
   * 
   * @param temp
   *          setpoint in degrees F
   */
  public void setCoolSetpoint(int temp);

  /**
   * Set the heat setpoint
   * 
   * @param temp
   *          setpoint in degrees F
   */
  public void setHeatSetpoint(int temp);
}
