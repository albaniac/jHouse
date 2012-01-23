/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import net.gregrapp.jhouse.device.classes.SecurityPanel;
import net.gregrapp.jhouse.device.drivers.types.AbstractDeviceDriver;
import net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback;
import net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Interface;

/**
 * @author Greg Rapp
 * 
 *         Zone Value Indices
 * 
 *         10 - Partition 1 Status
 *         11 - Partition 1 Last Armed By
 *         12 - Partition 1 Last Disarmed By
 *         20 - Partition 2
 *         30 - Partition 3
 *         40 - Partition 4
 *         50 - Partition 5
 *         60 - Partition 6
 *         70 - Partition 7
 *         80 - Partition 8
 * 
 *         101-164 - Zone 1-64 Status
 *         
 */
public class DSCIT100SecurityPanel extends AbstractDeviceDriver implements
    DSCIT100Callback,SecurityPanel
{
  private DSCIT100Interface driverInterface;

  /**
   * 
   */
  public DSCIT100SecurityPanel(DSCIT100Interface driverInterface)
  {
    this.driverInterface = driverInterface;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.drivers.types.DeviceDriver#interfaceReady()
   */
  @Override
  public void interfaceReady()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#broadcastLabels
   * (int, java.lang.String)
   */
  @Override
  public void broadcastLabels(int zone, String label)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#zoneAlarm(int,
   * int)
   */
  @Override
  public void zoneAlarm(int partition, int zone)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#zoneAlarmRestore
   * (int, int)
   */
  @Override
  public void zoneAlarmRestore(int partition, int zone)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#zoneOpen(int)
   */
  @Override
  public void zoneOpen(int zone)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#zoneRestore(int)
   */
  @Override
  public void zoneRestore(int zone)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#paritionArmed(int,
   * int)
   */
  @Override
  public void paritionArmed(int partition, int mode)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#partitionDisarmed
   * (int)
   */
  @Override
  public void partitionDisarmed(int partition)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#userClosing(int,
   * int)
   */
  @Override
  public void userClosing(int partition, int userCode)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#specialClosing
   * (int)
   */
  @Override
  public void specialClosing(int partition)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#userOpening(int,
   * int)
   */
  @Override
  public void userOpening(int partition, int userCode)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void arm()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void armAway()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void armStay()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void disarm()
  {
    // TODO Auto-generated method stub
    
  }

}
