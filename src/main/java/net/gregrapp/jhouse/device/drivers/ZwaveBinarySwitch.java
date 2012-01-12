/**
 * 
 */
package net.gregrapp.jhouse.device.drivers;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.types.ZwaveDevice;
import net.gregrapp.jhouse.events.BinarySwitchStateEvent;
import net.gregrapp.jhouse.events.BinarySwitchStateEvent.SwitchState;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific;
import net.gregrapp.jhouse.managers.state.StateManagerImpl.StateType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveBinarySwitch extends ZwaveDevice implements BinarySwitch,
    CommandClassBasic, CommandClassManufacturerSpecific
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveBinarySwitch.class);

  //private int switchState = -1;
  
  public ZwaveBinarySwitch(int deviceId, ZwaveInterface deviceInterface,
      int nodeId)
  {
    super(deviceId, deviceInterface, nodeId);
    init();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicGet()
   */
  public void commandClassBasicGet()
  {
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_GET.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicReport(int)
   */
  public void commandClassBasicReport(int value)
  {
    logger.info("Received switch state update [{}]", value == 255 ? "ON"
        : "OFF");
    //this.switchState = value;
    this.deviceValue = String.valueOf(value);
    //stateManager.setState(this, StateType.SWITCHLEVEL, String.valueOf(value));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicSet(int)
   */
  public void commandClassBasicSet(int value)
  {
    if (value == 0xFF)
      eventManager.eventCallback(new BinarySwitchStateEvent(this, SwitchState.ON, SwitchState.OFF));
    else
      eventManager.eventCallback(new BinarySwitchStateEvent(this, SwitchState.OFF, SwitchState.ON));
      
    deviceInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_SET.get(),
        value);
    //this.switchState = value;
    this.deviceValue = String.valueOf(value);
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
    logger.debug("Requesting manufacturer specific report from node {}", nodeId);

    deviceInterface.zwaveSendData(nodeId, CommandClass.COMMAND_CLASS_MANUFACTURER_SPECIFIC.get(),
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
    // logger.info("Received manufacturer specific report form node {},
    // manufacturer: {}, product type: {})
  }

  private void init()
  {
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

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOff()
   */
  public void setOff()
  {
    logger.info("Setting device ID {} to OFF", this.deviceId);
    this.commandClassBasicSet(0x0);
    //stateManager.setState(this, StateType.SWITCHLEVEL, "0");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOn()
   */
  public void setOn()
  {
    logger.info("Setting device ID {} to ON", this.deviceId);
    this.commandClassBasicSet(0xFF);
    //stateManager.setState(this, StateType.SWITCHLEVEL, "255");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#toggleOnOff()
   */
  public void toggleOnOff()
  {
    logger.debug("Toggling device {}", this.deviceId);
    if (this.deviceValue == "")
    {
      logger.info("Switch state is unknown, polling switch");

      this.poll();
      long startTime = System.currentTimeMillis();
      while (this.deviceValue == ""
          && (System.currentTimeMillis() - startTime < 5000))
      {
        try
        {
          Thread.sleep(100);
        } catch (InterruptedException e)
        {
        }
      }
    }
    if (this.deviceValue == "255")
      this.setOff();
    else if (this.deviceValue == "0")
      this.setOn();
    else
      logger.warn(
          "Invalid switch state for device {}, ignoring toggle command",
          this.deviceId);
  }

}
