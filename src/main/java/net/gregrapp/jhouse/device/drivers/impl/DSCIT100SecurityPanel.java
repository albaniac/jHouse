/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    DSCIT100Callback, SecurityPanel
{
  private static final Logger logger = LoggerFactory
      .getLogger(DSCIT100SecurityPanel.class);
  
  private DSCIT100Interface driverInterface;

  // Device value indices
  private static final int ZONE_INDEX = 100;
  private static final int PARTITION_INDEX_MULTIPLIER = 10;
  private static final int LAST_ARMED_BY_INDEX = 1;
  private static final int LAST_DISARMED_BY_INDEX_MULTIPLIER = 2;
  
  /**
   * 
   */
  public DSCIT100SecurityPanel(DSCIT100Interface driverInterface)
  {
    logger.info("Instantiating device driver {}", this.getClass().getName());
    this.driverInterface = driverInterface;
    driverInterface.attachDeviceDriver(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#arm()
   */
  @Override
  public void arm()
  {
    // TODO Make partition and code configurable
    driverInterface.sendCommand("033", "1" + "228000");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#armAway()
   */
  @Override
  public void armAway()
  {
    // TODO Make partition configurable
    driverInterface.sendCommand("030", "1");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#armNoEntryDelay()
   */
  @Override
  public void armNoEntryDelay()
  {
    // TODO Make partition configurable
    driverInterface.sendCommand("032", "1");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#armStay()
   */
  @Override
  public void armStay()
  {
    // TODO Make partition configurable
    driverInterface.sendCommand("031", "1");
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
    logger.debug("Received label broadcast for zone {}: {}", zone, label);
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#codeRequired(int)
   */
  @Override
  public void codeRequired(int partition)
  {
    // TODO Make code configurable
    driverInterface.sendCommand("200", String.valueOf(partition) + "228000");
  }

  @Override
  public void disarm()
  {
    // TODO Make partition and code configurable
    driverInterface.sendCommand("040", "1" + "228000");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.drivers.types.DeviceDriver#interfaceReady()
   */
  @Override
  public void interfaceReady()
  {
    // Request the current panel status
    driverInterface.sendCommand("001", "");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#panic()
   */
  @Override
  public void panic()
  {
    driverInterface.sendCommand("060", "3");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#panicAmbulance()
   */
  @Override
  public void panicAmbulance()
  {
    driverInterface.sendCommand("060", "2");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#panicFire()
   */
  @Override
  public void panicFire()
  {
    driverInterface.sendCommand("060", "1");
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
    logger.debug("Partition {} armed, mode {}", partition, mode);

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
    logger.debug("Partition disarmed: {}", partition);

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
    logger.debug("Special closing for partition: {}", partition);

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
    logger.debug("User closing for partition {}: {}",partition,userCode);

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
    logger.debug("User opening for partition {}: {}",partition,userCode);

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
    logger.info("Zone alarm at partition {}, zone {}", partition, zone);

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
    logger.info("Zone alarm restored at partition {}, zone {}", partition, zone);

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#zoneOpen(int)
   */
  @Override
  public void zoneOpen(int zone)
  {
    logger.debug("Zone opened: {}", zone);
    updateDeviceValue(ZONE_INDEX+zone, 255);
    updateDeviceText(ZONE_INDEX+zone, "Open");
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
    logger.debug("Zone restored: {}", zone);
    updateDeviceValue(ZONE_INDEX+zone, 0);
    updateDeviceText(ZONE_INDEX+zone, "Closed");
  }

}
