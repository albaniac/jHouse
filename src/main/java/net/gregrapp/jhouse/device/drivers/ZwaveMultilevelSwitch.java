/**
 * 
 */
package net.gregrapp.jhouse.device.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.classes.MultilevelSwitch;
import net.gregrapp.jhouse.device.types.ZwaveDevice;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandSwitchMultilevel;
import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveMultilevelSwitch extends ZwaveDevice implements BinarySwitch, MultilevelSwitch, CommandClassSwitchMultilevel
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

  public void setLevel(int level)
  {
    this.commandClassSwitchMultilevelSet(level);
  }

  public void commandClassSwitchMultilevelStartLevelChange()
  {
    // TODO Auto-generated method stub
    
  }

  public void commandClassSwitchMultilevelStopLevelChange()
  {
    // TODO Auto-generated method stub
    
  }

  public void commandClassSwitchMultilevelGet()
  {
    deviceInterface.zwaveSendData(this.nodeId, CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(), CommandSwitchMultilevel.SWITCH_MULTILEVEL_GET.get());        
  }

  public void commandClassSwitchMultilevelSet(int value)
  {
    deviceInterface.zwaveSendData(this.nodeId, CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get(), CommandSwitchMultilevel.SWITCH_MULTILEVEL_SET.get(), value);            
  }

  public void commandClassSwitchMultilevelReport(int value)
  {
    logger.info("Received switch level update [{}]", value);
    this.switchLevel = value;    
  }

  public void setOn()
  {
    this.commandClassSwitchMultilevelSet(0xFF);
  }

  public void setOff()
  {
    this.commandClassSwitchMultilevelSet(0x0);    
  }

  public void toggleOnOff()
  {
    if (this.switchLevel > 0)
      this.setOff();
    else if (this.switchLevel == 0)
      this.setOn();
    else
      logger.warn("Invalid switch level for device {}, ignoring toggle command", this.deviceId);
  }

  @Override
  public void poll()
  {
    this.commandClassSwitchMultilevelGet();
  }

}
