/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.types;

import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;

/**
 * Abstract Z-Wave device driver class
 * 
 * @author Greg Rapp
 * 
 */
public abstract class ZwaveDeviceDriver extends AbstractDeviceDriver
{
  protected ZwaveInterface deviceInterface;
  protected int nodeId;

  /**
   * @param deviceInterface
   *          Z-Wave device interface
   * @param nodeId
   *          Z-Wave node ID
   */
  public ZwaveDeviceDriver(ZwaveInterface deviceInterface, int nodeId)
  {
    // super(deviceId);
    this.deviceInterface = deviceInterface;
    this.nodeId = nodeId;
    deviceInterface.attachDevice(this);
  }

  /**
   * @return the Z-Wave node ID
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

  /**
   * Request Z-Wave command classes supported by this device
   */
  public void requestNodeInfo()
  {
    deviceInterface.requestNodeInfo(this.nodeId);
  }
}
