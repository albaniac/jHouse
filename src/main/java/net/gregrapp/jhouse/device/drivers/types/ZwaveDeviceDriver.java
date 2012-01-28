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
   * @param driverInterface
   *          Z-Wave device driver interface
   * @param nodeId
   *          Z-Wave node ID
   */
  public ZwaveDeviceDriver(ZwaveInterface driverInterface, int nodeId)
  {
    this.deviceInterface = driverInterface;
    this.nodeId = nodeId;
    driverInterface.attachDeviceDriver(this);
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
