/**
 * 
 */
package net.gregrapp.jhouse.device.drivers;

import net.gregrapp.jhouse.device.types.ZwaveDevice;
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
public class ZwaveBinarySensor extends ZwaveDevice implements
    CommandClassSensorBinary, CommandClassBasic
{

  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveBinarySensor.class);

  /**
   * @param deviceId
   * @param deviceInterface
   * @param nodeId
   */
  public ZwaveBinarySensor(int deviceId, ZwaveInterface deviceInterface,
      int nodeId)
  {
    super(deviceId, deviceInterface, nodeId);
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
    logger.debug("Sending COMMAND_CLASS_BASIC_GET");
    deviceInterface.zwaveSendData(this.nodeId,
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
    logger.info("Received COMMAND_CLASS_BASIC_REPORT from switch [{}]",
        value == 255 ? "OPEN" : "CLOSED");
    this.setValue(value);
    this.setStatus(value == 255 ? "ON" : "OFF");
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
    logger.info("Received COMMAND_CLASS_BASIC_SET from switch [{}]",
        value == 255 ? "OPEN" : "CLOSED");
    this.setValue(value);
    this.setStatus(value == 255 ? "ON" : "OFF");
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
    logger.debug("Sending COMMAND_CLASS_SENSOR_BINARY_GET");
    deviceInterface.zwaveSendData(this.nodeId,
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
        "Received COMMAND_CLASS_SENSOR_BINARY_REPORT from device: {} [{}]",
        this.deviceId, value == 255 ? "OPEN"
            : "CLOSED");
    this.setValue(value);
    this.setStatus(value == 255 ? "OPEN" : "CLOSED");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.types.Device#interfaceReady()
   */
  @Override
  public void interfaceReady()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.types.ZwaveDevice#poll()
   */
  @Override
  public void poll()
  {
    logger.info("Polling device ID {}", this.deviceId);
    // Request a BASIC_REPORT to get the current status of the switch
    this.commandClassBasicGet();
  }

}
