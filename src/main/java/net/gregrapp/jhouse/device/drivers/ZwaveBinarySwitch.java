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

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveBinarySwitch extends ZwaveDevice implements BinarySwitch
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveBinarySwitch.class);
  
  private int switchState;
  
  public ZwaveBinarySwitch(int deviceId, ZwaveInterface deviceInterface, int nodeId)
  {
    super(deviceId, deviceInterface, nodeId);
    init();
  }

  private void init()
  {
    this.poll();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOn()
   */
  public void setOn()
  {
    logger.info("Setting device ID {} to ON", this.deviceId);
    deviceInterface.zwaveSendData(this.nodeId, CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_SET.get(), CommandBasic.BASIC_ON.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#setOff()
   */
  public void setOff()
  {
    logger.info("Setting device ID {} to OFF", this.deviceId);
    deviceInterface.zwaveSendData(this.nodeId, CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_SET.get(), CommandBasic.BASIC_OFF.get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.BinarySwitchClass#toggleOnOff()
   */
  public void toggleOnOff()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.types.ZwaveDevice#poll()
   */
  @Override
  public void poll()
  {
    logger.info("Polling device ID {}", this.deviceId);
    // Request a BASIC_REPORT to get the current status of the switch
    deviceInterface.zwaveSendData(this.nodeId, CommandClass.COMMAND_CLASS_BASIC.get(), CommandBasic.BASIC_GET.get());   
  }

}
