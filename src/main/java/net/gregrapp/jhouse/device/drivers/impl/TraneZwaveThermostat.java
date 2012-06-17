/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import java.util.Set;

import net.gregrapp.jhouse.device.classes.Thermostat;
import net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandThermostatSetpoint;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassThermostatSetpoint;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class TraneZwaveThermostat extends ZwaveDeviceDriver implements
    Thermostat, CommandClassThermostatSetpoint
{
  private static final int COOL_SETPOINT_INDEX = 0;

  private static final int HEAT_SETPOINT_INDEX = 1;

  private static final XLogger logger = XLoggerFactory
      .getXLogger(TraneZwaveThermostat.class);

  /**
   * @param driverInterface
   */
  public TraneZwaveThermostat(ZwaveInterface driverInterface)
  {
    super(driverInterface);
  }

  @Override
  public void commandClassThermostatSetpointSupportedReport(
      Set<SetpointType> setpoints)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassThermostatSetpoint
   * #
   * commandClassThermostatSetpointGet(net.gregrapp.jhouse.interfaces.zwave.command
   * .CommandClassThermostatSetpoint.SetpointType)
   */
  @Override
  public void commandClassThermostatSetpointGet(SetpointType type)
  {
    logger.debug("Sending COMMAND_CLASS_THERMOSTAT_SETPOINT_GET");

    driverInterface.zwaveSendData(nodeId,
        CommandClass.COMMAND_CLASS_THERMOSTAT_SETPOINT.get(),
        CommandThermostatSetpoint.THERMOSTAT_SETPOINT_GET.get(), type.get());
  }

  @Override
  public void commandClassThermostatSetpointReport(SetpointType type,
      int setpoint)
  {
    logger
        .info(
            "Received COMMAND_CLASS_THERMOSTAT_SETPOINT_REPORT from Z-Wave node {}: [{} - {}]",
            new Object[] { this.nodeId, type.toString(), setpoint });

    int index = -1;

    switch (type)
    {
    case COOLING:
      index = COOL_SETPOINT_INDEX;
      break;

    case HEATING:
      index = HEAT_SETPOINT_INDEX;
      break;

    default:
      logger.warn("Unhandled setpoint type [{}]", type.toString());
      break;
    }

    if (index >= 0)
    {
      updateDeviceValue(index, setpoint);
      updateDeviceText(index, String.format("%d F", setpoint));
    }
  }

  @Override
  public void commandClassThermostatSetpointSet(SetpointType type, int setpoint)
  {
    logger.info("Setting Z-Wave thermostat node [{}] setpoint [{}] to [{}]",
        new Object[] { this.nodeId, type.toString(), setpoint });
    
    final int SCALE = 1; // 1=Fahrenheit

    driverInterface.zwaveSendData(nodeId,
        CommandClass.COMMAND_CLASS_THERMOSTAT_SETPOINT.get(),
        CommandThermostatSetpoint.THERMOSTAT_SETPOINT_SET.get(), type.get(),
        SCALE, setpoint);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassThermostatSetpoint
   * #commandClassThermostatSetpointSupportedGet()
   */
  @Override
  public void commandClassThermostatSetpointSupportedGet()
  {
    logger.debug("Sending COMMAND_CLASS_THERMOSTAT_SETPOINT_SUPPORTED_GET");

    driverInterface.zwaveSendData(nodeId,
        CommandClass.COMMAND_CLASS_THERMOSTAT_SETPOINT.get(),
        CommandThermostatSetpoint.THERMOSTAT_SETPOINT_SUPPORTED_GET.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Thermostat#getCoolSetpoint()
   */
  @Override
  public int getCoolSetpoint()
  {
    this.commandClassThermostatSetpointGet(SetpointType.COOLING);

    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Thermostat#getHeatSetpoint()
   */
  @Override
  public int getHeatSetpoint()
  {
    this.commandClassThermostatSetpointGet(SetpointType.HEATING);

    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver#poll()
   */
  @Override
  public void poll()
  {
    getCoolSetpoint();
    getHeatSetpoint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Thermostat#setCoolSetpoint(int)
   */
  @Override
  public void setCoolSetpoint(int temp)
  {
    commandClassThermostatSetpointSet(SetpointType.COOLING, temp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Thermostat#setHeatSetpoint(int)
   */
  @Override
  public void setHeatSetpoint(int temp)
  {
    commandClassThermostatSetpointSet(SetpointType.HEATING, temp);
  }

}
