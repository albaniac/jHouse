/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.classes.MultilevelSwitch;
import net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandSwitchMultilevel;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveMultilevelSwitch extends ZwaveDeviceDriver implements
    BinarySwitch,
    MultilevelSwitch, CommandClassSwitchMultilevel,
    CommandClassManufacturerSpecific

{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveMultilevelSwitch.class);

  /**
   * Value indexes
   */
  private static final int SWITCH_LEVEL_VALUE_IDX = 0;

  /**
   * Internal switch state
   */
  private int switchLevel = -1;

  /**
   * @param driverInterface
   *          interface instance for this device driver
   * @param zwaveNodeId
   *          Z-Wave node id
   */
  public ZwaveMultilevelSwitch(ZwaveInterface driverInterface)
  {
    super(driverInterface);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific
   * #commandClassManufacturerSpecificGet()
   */
  @Override
  public void commandClassManufacturerSpecificGet()
  {
    logger
        .debug("Requesting manufacturer specific report from node {}", nodeId);

    driverInterface.zwaveSendData(nodeId,
        CommandClass.COMMAND_CLASS_MANUFACTURER_SPECIFIC.get(),
        CommandManufacturerSpecific.MANUFACTURER_SPECIFIC_GET.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific
   * #commandClassManufacturerSpecificReport(int, int, int)
   */
  @Override
  public void commandClassManufacturerSpecificReport(int manufacturer,
      int productType, int productId)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel
   * #commandClassSwitchMultilevelGet()
   */
  public void commandClassSwitchMultilevelGet()
  {
    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_GET.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel
   * #commandClassSwitchMultilevelReport(int)
   */
  public void commandClassSwitchMultilevelReport(int value)
  {
    logger.info("Received switch level update from node {}: {}", this.nodeId,
        value);
    updateSwitchLevel(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel
   * #commandClassSwitchMultilevelSet(int)
   */
  public void commandClassSwitchMultilevelSet(int value)
  {
    logger.info("Setting Z-Wave node {} to level: {}", this.nodeId, value);

    updateSwitchLevel(value);

    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_SET.get(), value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel
   * #commandClassSwitchMultilevelStartLevelChange(int)
   */
  public void commandClassSwitchMultilevelStartLevelChange(int direction)
  {
    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_START_LEVEL_CHANGE.get(),
        direction);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel
   * #commandClassSwitchMultilevelStopLevelChange()
   */
  public void commandClassSwitchMultilevelStopLevelChange()
  {
    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE.get());
    this.poll();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.drivers.impl.types.DeviceDriver#interfaceReady()
   */
  public void interfaceReady()
  {
    logger.debug("Interface ready callback received");
    this.poll();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver#poll()
   */
  @Override
  public void poll()
  {
    logger.info("Polling Z-Wave node [{}]", this.nodeId);
    this.commandClassSwitchMultilevelGet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.classes.MultilevelSwitch#setLevel(java.lang.
   * Integer)
   */
  public void setLevel(Integer level)
  {
    level--;

    if (level > 99)
      level = 99;
    else if (level < 0)
      level = 0;

    this.commandClassSwitchMultilevelSet(level);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.BinarySwitch#setOff()
   */
  public void setOff()
  {
    this.commandClassSwitchMultilevelSet(0x0);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.BinarySwitch#setOn()
   */
  public void setOn()
  {
    this.commandClassSwitchMultilevelSet(0xFF);
  }

  /**
   * Update the internal switch level state and associated device values
   * 
   * @param value switch level
   */
  private void updateSwitchLevel(int value)
  {
    this.switchLevel = value;

    this.updateDeviceValue(SWITCH_LEVEL_VALUE_IDX, value);
    this.updateDeviceText(SWITCH_LEVEL_VALUE_IDX, "Level " +
        String.valueOf(value));
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.MultilevelSwitch#startLevelChange(java.lang.Integer)
   */
  @Override
  public void startLevelChange(Integer direction)
  {
    this.commandClassSwitchMultilevelStartLevelChange(direction);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.MultilevelSwitch#stopLevelChange()
   */
  @Override
  public void stopLevelChange()
  {
    this.commandClassSwitchMultilevelStopLevelChange();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitch#toggleOnOff()
   */
  public void toggleOnOff()
  {
    if (this.switchLevel > 0)
      this.setOff();
    else if (this.switchLevel == 0)
      this.setOn();
    else
      logger.warn(
          "Invalid switch level for Z-Wave node {}, ignoring toggle command",
          this.nodeId);
  }
}
