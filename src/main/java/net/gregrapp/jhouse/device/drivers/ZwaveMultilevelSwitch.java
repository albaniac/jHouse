/**
 * 
 */
package net.gregrapp.jhouse.device.drivers;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.classes.MultilevelSwitch;
import net.gregrapp.jhouse.device.types.ZwaveDevice;
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
public class ZwaveMultilevelSwitch extends ZwaveDevice implements BinarySwitch,
    MultilevelSwitch, CommandClassSwitchMultilevel,
    CommandClassManufacturerSpecific

{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveMultilevelSwitch.class);

  private int switchLevel = -1;

  /**
   * @param deviceId
   * @param deviceInterface
   * @param nodeId
   */
  public ZwaveMultilevelSwitch(int deviceId, ZwaveInterface deviceInterface,
      int nodeId)
  {
    super(deviceId, deviceInterface, nodeId);
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

    deviceInterface.zwaveSendData(nodeId,
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

  public void commandClassSwitchMultilevelGet()
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_GET.get());
  }

  public void commandClassSwitchMultilevelReport(int value)
  {
    logger.info("Received switch level update [{}]", value);
    this.switchLevel = value;
  }

  public void commandClassSwitchMultilevelSet(int value)
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_SET.get(), value);
  }

  public void commandClassSwitchMultilevelStartLevelChange(int direction)
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_START_LEVEL_CHANGE.get(),
        direction);
  }

  public void commandClassSwitchMultilevelStopLevelChange()
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(),
        CommandSwitchMultilevel.SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE.get());
    this.poll();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.types.Device#interfaceReady()
   */
  public void interfaceReady()
  {
    logger.debug("Interface ready callback received");
    this.poll();
  }

  @Override
  public void poll()
  {
    this.commandClassSwitchMultilevelGet();
  }

  public void setLevel(int level)
  {
    this.commandClassSwitchMultilevelSet(level);
  }

  public void setOff()
  {
    this.commandClassSwitchMultilevelSet(0x0);
    this.switchLevel = 0x0;
  }

  public void setOn()
  {
    this.commandClassSwitchMultilevelSet(0xFF);
    this.switchLevel = 0xFF;
  }

  @Override
  public void startLevelChange(int direction)
  {
    this.commandClassSwitchMultilevelStartLevelChange(direction);
  }

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
          "Invalid switch level for device {}, ignoring toggle command",
          this.deviceId);
  }
}
