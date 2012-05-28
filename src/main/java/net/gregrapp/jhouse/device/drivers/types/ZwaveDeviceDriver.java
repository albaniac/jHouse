/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.types;

import net.gregrapp.jhouse.interfaces.zwave.ZwaveInterface;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassHail;

/**
 * Abstract Z-Wave device driver class
 * 
 * @author Greg Rapp
 * 
 */
public abstract class ZwaveDeviceDriver extends AbstractDeviceDriver implements
    CommandClassHail
{
  protected ZwaveInterface driverInterface;
  protected int nodeId;

  /**
   * @param driverInterface
   *          Z-Wave device driver interface
   * @param nodeId
   *          Z-Wave node ID
   */
  public ZwaveDeviceDriver(ZwaveInterface driverInterface)
  {
    this.driverInterface = driverInterface;
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
   * Optimize the Z-Wave node
   * (Node neighbor update + Delete return route + Assign return route)
   * @return
   */
  public String optimizeNode()
  {
    StringBuffer status = new StringBuffer();

    boolean result = driverInterface
        .zwaveRequestNodeNeighborUpdate(this.nodeId);

    if (result)
    {
      status.append("Request node neighbor update succeeded.");
      result = driverInterface.zwaveDeleteReturnRoute(this.nodeId);

      if (result)
      {
        status.append(" Delete return route succeeded.");
        result = driverInterface.zwaveAssignReturnRoute(this.nodeId);

        if (result)
        {
          status.append(" Assign return route succeeded.");
          status.append(" Neighbors: ");
          status.append(driverInterface.getNodeNeighbors(getNodeId()));
        } else
        {
          status.append(" Assign return route failed.");
        }
      } else
      {
        status.append(" Delete return route failed.");
      }
    } else
    {
      status.append("Request node neighbor update failed.");
    }

    return status.toString();
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
    driverInterface.requestNodeInfo(this.nodeId);
  }

  /**
   * Set this driver's ZWave node ID
   */
  public void setNodeId(int nodeId)
  {
    this.nodeId = nodeId;
    driverInterface.attachDeviceDriver(this);
  }
}
