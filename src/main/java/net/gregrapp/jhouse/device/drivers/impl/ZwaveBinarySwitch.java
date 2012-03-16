/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver;
import net.gregrapp.jhouse.interfaces.InterfaceCallback;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Z-Wave binary (on/off) switch
 * 
 * @author Greg Rapp
 * 
 */
public class ZwaveBinarySwitch extends ZwaveDeviceDriver implements
    BinarySwitch,
    CommandClassBasic, CommandClassManufacturerSpecific, InterfaceCallback
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveBinarySwitch.class);

  /**
   * Internal switch state
   */
  private int switchState = -1;

  /**
   * Value indexes
   */
  private static final int SWITCH_VALUE_IDX = 0;

  /**
   * @param driverInterface
   *          interface instance for this device driver
   * @param zwaveNodeId
   *          Z-Wave node id
   */
  public ZwaveBinarySwitch(ZwaveInterface driverInterface)
  {
    super(driverInterface);
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
    logger.debug("Sending COMMAND_CLASS_BASIC_GET");
    driverInterface.zwaveSendData(this.nodeId,
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
    logger.info(
        "Received COMMAND_CLASS_BASIC_REPORT from Z-Wave node {}: [{}]",
        this.nodeId, value == 255 ? "ON"
            : "OFF");

    this.switchState = value;
    this.updateDeviceValue(SWITCH_VALUE_IDX, value);
    this.updateDeviceText(SWITCH_VALUE_IDX, value == 255 ? "On" : "Off");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic#
   * commandClassBasicSet(int)
   */
  public void commandClassBasicSet(int value)
  {
    logger.info("Setting Z-Wave node [{}] to [{}]", this.nodeId,
        value == 0xFF ? "ON" : "OFF");

    this.switchState = value;
    this.updateDeviceValue(SWITCH_VALUE_IDX, value);
    this.updateDeviceText(SWITCH_VALUE_IDX, value == 255 ? "On" : "Off");

    driverInterface.zwaveSendData(this.nodeId,
        CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_SET.get(),
        value);
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
        .debug("Requesting manufacturer specific report from Z-Wave node {}",
            nodeId);

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
    // logger.info("Received manufacturer specific report form node {},
    // manufacturer: {}, product type: {})
  }

  private void init()
  {
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.InterfaceCallback#interfaceReady()
   */
  @Override
  public void interfaceReady()
  {
    logger.debug("Interface ready callback received");
    this.poll();
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

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOff()
   */
  public void setOff()
  {
    this.commandClassBasicSet(0x0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOn()
   */
  public void setOn()
  {
    this.commandClassBasicSet(0xFF);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#toggleOnOff()
   */
  public void toggleOnOff()
  {
    logger.debug("Toggling Z-Wave node [{}]", this.nodeId);
    if (this.switchState == -1)
    {
      logger.info("Switch state is unknown, polling switch");

      this.poll();
      long startTime = System.currentTimeMillis();
      while (this.switchState == -1
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
    if (this.switchState == 255)
      this.setOff();
    else if (this.switchState == 0)
      this.setOn();
    else
      logger.warn(
          "Invalid switch state for Z-Wave node {}, ignoring toggle command",
          this.nodeId);
  }

}
