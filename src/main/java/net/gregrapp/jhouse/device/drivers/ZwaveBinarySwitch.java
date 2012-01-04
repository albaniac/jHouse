/**
 * 
 */
package net.gregrapp.jhouse.device.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.types.ZwaveDevice;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveBinarySwitch extends ZwaveDevice implements BinarySwitch,
    CommandClassBasic
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveBinarySwitch.class);

  private int switchState = -1;

  public ZwaveBinarySwitch(int deviceId, ZwaveInterface deviceInterface,
      int nodeId)
  {
    super(deviceId, deviceInterface, nodeId);
    init();
  }

  private void init()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOn()
   */
  public void setOn()
  {
    logger.info("Setting device ID {} to ON", this.deviceId);
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_SET.get(),
        CommandBasic.BASIC_ON.get());
    this.commandClassBasicSet(0xFF);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOff()
   */
  public void setOff()
  {
    logger.info("Setting device ID {} to OFF", this.deviceId);
    this.commandClassBasicSet(0x0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#toggleOnOff()
   */
  public void toggleOnOff()
  {
    if (this.switchState == 0xFF)
      this.setOff();
    else if (this.switchState == 0x0)
      this.setOn();
    else
      logger.warn(
          "Invalid switch state for device {}, ignoring toggle command",
          this.deviceId);
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

  public void commandClassBasicReport(int value)
  {
    logger.info("Received switch state update [{}]", value == 255 ? "ON"
        : "OFF");
    this.switchState = value;
  }

  public void commandClassBasicSet(int value)
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_SET.get(),
        value);
  }

  public void commandClassBasicGet()
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_GET.get());
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

}
