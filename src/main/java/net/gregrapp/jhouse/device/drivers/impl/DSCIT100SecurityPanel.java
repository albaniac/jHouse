/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.gregrapp.jhouse.device.classes.SecurityPanel;
import net.gregrapp.jhouse.device.drivers.types.AbstractDeviceDriver;
import net.gregrapp.jhouse.interfaces.InterfaceCallback;
import net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback;
import net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Interface;
import net.gregrapp.jhouse.services.ConfigService;

/**
 * @author Greg Rapp
 * 
 *         Partition State Value Indices
 *         ---------------------------------
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
 *         Partition State Values
 *         ------------------------------
 *         0x00 - Partition Ready
 *         0x01 - Partition Not Ready
 *         0x02 - Partition Armed
 *         0x04 - Partition Disarmed
 *         0x08 - Partition Busy
 *         0x10 - Partition Entry Delay
 *         0x20 - Partition Exit Delay
 *         0x40 - Partition Failed To Arm
 *         0x80 - Partition In Alarm

 *         Zone State Value Indices
 *         ---------------------------------
 *         101-164 - Zone 1-64 State
 * 
 *         Zone State Value Bit Field
 *         -------------------------------
 *                 Cleared        | Set
 *         -------------------------------
 *         Bit 0 - Restored       | Open
 *         Bit 1 - Alarm Restored | Alarm
 */

public class DSCIT100SecurityPanel extends AbstractDeviceDriver implements
    DSCIT100Callback, SecurityPanel, InterfaceCallback
{
  private static final Logger logger = LoggerFactory
      .getLogger(DSCIT100SecurityPanel.class);
  
  private DSCIT100Interface driverInterface;
  
  @Autowired
  private ConfigService configService;
  
  /*
   * Device value indices
   */
  private static final int ZONE_INDEX = 100;
  private static final int PARTITION_INDEX_MULTIPLIER = 10;
  private static final int LAST_ARMED_BY_INDEX = 1;
  private static final int LAST_DISARMED_BY_INDEX = 2;

  /*
   * Config options
   */
  private static final String CODE = "CODE";
 // private static final String ZONE = "ZONE%d";
  
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
    String code = configService.get(this.getClass().getName(), CODE);
    driverInterface.sendCommand("033", "1" + code);
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
    logger.debug("Received label broadcast for zone {}: [{}]", zone, label);
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
    String code = configService.get(this.getClass().getName(), CODE);
    driverInterface.sendCommand("200", String.valueOf(partition) + code);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#disarm()
   */
  @Override
  public void disarm()
  {
    String code = configService.get(this.getClass().getName(), CODE);
    driverInterface.sendCommand("040", "1" + code);
  }

  
  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.InterfaceCallback#interfaceReady()
   */
  @Override
  public void interfaceReady()
  {
    // Request the current panel status
    driverInterface.sendCommand("001", "");
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#invalidAccessCode(int)
   */
  @Override
  public void invalidAccessCode(int partition)
  {
    // TODO Auto-generated method stub

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
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x02);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Armed");
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.dscit100.DSCIT100Callback#partitionBusy(int)
   */
  @Override
  public void partitionBusy(int partition)
  {
    logger.debug("Partition busy: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x08);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Busy");
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
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x04);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Disarmed");
  }

  @Override
  public void partitionEntryDelay(int partition)
  {
    logger.debug("Partition in entry delay: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x10);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Entry Delay");
  }

  @Override
  public void partitionExitDelay(int partition)
  {
    logger.debug("Partition in exit delay: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x20);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Exit Delay");
  }

  @Override
  public void partitionFailedToArm(int partition)
  {
    logger.debug("Partition failed to arm: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x40);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Failed to Arm");
  }

  @Override
  public void partitionInAlarm(int partition)
  {
    logger.debug("Partition in alarm: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x80);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Alarm");
  }

  @Override
  public void partitionNotReady(int partition)
  {
    logger.debug("Partition not ready: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x01);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Not Ready");
  }

  @Override
  public void partitionReady(int partition)
  {
    logger.debug("Partition ready: {}", partition);
    updateDeviceValue(PARTITION_INDEX_MULTIPLIER * partition, 0x0);
    updateDeviceText(PARTITION_INDEX_MULTIPLIER * partition, "Ready");
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
    updateDeviceValue((PARTITION_INDEX_MULTIPLIER * partition) + LAST_ARMED_BY_INDEX, 0);
    updateDeviceText((PARTITION_INDEX_MULTIPLIER * partition) + LAST_ARMED_BY_INDEX, "Special Closing");
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
    logger.debug("User closing for partition {}: {}", partition, userCode);
    updateDeviceValue((PARTITION_INDEX_MULTIPLIER * partition) + LAST_ARMED_BY_INDEX, userCode);
    // TODO Add userCode to person name mapping
    updateDeviceText((PARTITION_INDEX_MULTIPLIER * partition) + LAST_ARMED_BY_INDEX, String.valueOf(userCode));
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
    logger.debug("User opening for partition {}: {}", partition, userCode);
    updateDeviceValue((PARTITION_INDEX_MULTIPLIER * partition) + LAST_DISARMED_BY_INDEX, userCode);
    // TODO Add userCode to person name mapping
    updateDeviceText((PARTITION_INDEX_MULTIPLIER * partition) + LAST_DISARMED_BY_INDEX, String.valueOf(userCode));
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
    updateDeviceValueBitmask(ZONE_INDEX + zone, 1, true);
    updateDeviceText(ZONE_INDEX + zone, "Alarm");
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
    logger
        .info("Zone alarm restored at partition {}, zone {}", partition, zone);
    updateDeviceValueBitmask(ZONE_INDEX + zone, 1, false);
    updateDeviceText(ZONE_INDEX + zone, "Alarm Restore");
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
    updateDeviceValueBitmask(ZONE_INDEX + zone, 0, true);
    updateDeviceText(ZONE_INDEX + zone, "Open");
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
    updateDeviceValueBitmask(ZONE_INDEX + zone, 0, false);
    updateDeviceText(ZONE_INDEX + zone, "Closed");
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.SecurityPanel#getZoneLabel(int)
   */
/*  @Override
  public String getZoneLabel(int zone) throws IllegalArgumentException
  {
    if (zone < 1 || zone > 64)
    {
      throw new IllegalArgumentException("invalid zone");
    }
    
    String defaultName = "Zone " + String.valueOf(zone);

    if (configService == null)
    {
      return defaultName;
    }
    
    String name = configService.get(this.getClass().getName(), String.format(ZONE,zone));
    
    if (name == null || "".equals(name))
      return defaultName;
    else
      return name;
  }*/
}
