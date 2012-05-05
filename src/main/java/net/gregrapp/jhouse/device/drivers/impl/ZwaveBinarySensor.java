/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandSensorBinary;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSensorBinary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Z-Wave binary sensor
 * 
 * @author Greg Rapp
 * 
 */
public class ZwaveBinarySensor extends ZwaveDeviceDriver implements
    CommandClassSensorBinary, CommandClassBasic
{

  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveBinarySensor.class);

  /**
   * Value indexes
   */
  private static final int SENSOR_VALUE_IDX = 0;

  /**
   * @param driverInterface
   *          interface instance for this device driver
   * @param zwaveNodeId
   *          Z-Wave node id
   */
  public ZwaveBinarySensor(ZwaveInterface driverInterface)
  {
    super(driverInterface);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicGet()
   */
  @Override
  public void commandClassBasicGet()
  {
    logger.debug("Sending COMMAND_CLASS_BASIC_GET to Z-Wave node {}",
        this.nodeId);
    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_GET.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicReport(int)
   */
  @Override
  public void commandClassBasicReport(int value)
  {
    logger.info(
        "Received COMMAND_CLASS_BASIC_REPORT from Z-Wave switch {}: {}",
        this.nodeId,
        value == 255 ? "OPEN" : "CLOSED");
    this.updateSensorState(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicSet(int)
   */
  @Override
  public void commandClassBasicSet(int value)
  {
    logger.info("Received COMMAND_CLASS_BASIC_SET from Z-Wave switch {}: {}",
        this.nodeId,
        value == 255 ? "OPEN" : "CLOSED");
    this.updateSensorState(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSensorBinary#
   * commandClassSensorBinaryGet()
   */
  @Override
  public void commandClassSensorBinaryGet()
  {
    logger.debug("Sending COMMAND_CLASS_SENSOR_BINARY_GET to Z-Wave node {}",
        this.nodeId);
    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SENSOR_BINARY.get(),
        CommandSensorBinary.SENSOR_BINARY_GET.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSensorBinary#
   * commandClassSensorBinaryReport(int)
   */
  @Override
  public void commandClassSensorBinaryReport(int value)
  {
    logger.info(
        "Received COMMAND_CLASS_SENSOR_BINARY_REPORT from Z-Wave node {}: {}",
        this.nodeId, value == 255 ? "OPEN"
            : "CLOSED");
    this.updateSensorState(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.drivers.impl.types.ZwaveDeviceDriver#poll()
   */
  @Override
  public void poll()
  {
    logger.info("Polling Z-Wave node [{}]", this.nodeId);
    // Request a BASIC_REPORT to get the current status of the switch
    this.commandClassBasicGet();
  }

  /**
   * Update the internal sensor state and associated device values
   * 
   * @param value
   *          sensor state
   */
  private void updateSensorState(int value)
  {
    this.updateDeviceValue(SENSOR_VALUE_IDX, value);
    this.updateDeviceText(SENSOR_VALUE_IDX,
        String.valueOf(value == 255 ? "Open" : "Closed"));
  }
}
