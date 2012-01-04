/**
 * 
 */
package net.gregrapp.jhouse.device.types;

import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;

/**
 * @author Greg Rapp
 * 
 */
public abstract class ZwaveDevice extends AbstractDevice
{
  protected ZwaveInterface deviceInterface;
  protected int nodeId;

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
   * Poll the device when we receive a HAIL from the device
   */
  public void hail()
  {
    this.poll();
  }

  /**
   * Poll the device to obtain its current state
   */
  public abstract void poll();

}
