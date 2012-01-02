/**
 * 
 */
package net.gregrapp.jhouse.device.types;

import java.util.ArrayList;
import java.util.List;

import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;

/**
 * @author Greg Rapp
 * 
 */
public abstract class ZwaveDevice extends AbstractDevice
{
  protected ZwaveInterface deviceInterface;
  protected int nodeId;
  protected List<ZwaveDevice> childDevices = new ArrayList<ZwaveDevice>();

  public ZwaveDevice(int deviceId, ZwaveInterface deviceInterface, int nodeId)
  {
    super(deviceId);
    this.deviceInterface = deviceInterface;
    this.nodeId = nodeId;
    deviceInterface.attachDevice(this);
  }

  /**
   * @return the nodeId
   */
  public int getNodeId()
  {
    return nodeId;
  }

  /**
   * Return child devices of this device
   * 
   * @return the childDevices
   */
  public List<ZwaveDevice> getChildDevices()
  {
    return childDevices;
  }

  /**
   * Add a child device to this device
   * 
   * @param child
   */
  public void addChild(ZwaveDevice child)
  {
    this.childDevices.add(child);
  }

  /**
   * Poll the device to obtain its current state
   */
  public abstract void poll();
  
  /**
   * Poll the device when we receive a HAIL from the device
   */
  public void hail()
  {
    this.poll();
  }
  
}
