/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.InterfaceCallback;
import net.gregrapp.jhouse.interfaces.NodeInterface;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.MemoryGetId;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.SerialApiCapabilities;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandSensorBinary;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandSwitchMultilevel;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Manufacturer;
import net.gregrapp.jhouse.interfaces.zwave.Constants.RequestNeighbor;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassHail;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSensorBinary;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSwitchMultilevel;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Z-Wave network interface
 * 
 * @author Greg Rapp
 * 
 */
public class ZwaveInterface extends AbstractInterface implements
    ApplicationLayerAsyncCallback, NodeInterface
{
  // Amount of time that must pass before connection is restarted
  private static final int keepaliveHoldtimeSeconds = 60;

  // Interval between keep alive attempts
  private static final int keepaliveIntervalSeconds = 20;

  private static final XLogger logger = XLoggerFactory
      .getXLogger(ZwaveInterface.class);

  private ApplicationLayer appLayer;

  private Map<Integer, ArrayList<ZwaveDeviceDriver>> drivers = new HashMap<Integer, ArrayList<ZwaveDeviceDriver>>();

  private ScheduledExecutorService interfaceReadyExecutor;

  // Keep alive executor
  private ScheduledExecutorService keepaliveExecutor;

  private SessionLayer sessionLayer;

  // Property - HOST
  private static final String PROPERTY_HOST = "HOST";

  // Property - PORT
  private static final String PROPERTY_PORT = "PORT";

  /*
   * public ZwaveInterface(Transport transport) { super(transport); }
   */
  public ZwaveInterface(Map<String, String> properties)
  {
    super(properties);

    logger.entry();
    this.init();
    logger.exit();
  }

  public void attachDeviceDriver(final DeviceDriver driver)
  {
    logger.entry(driver);
    if (!(driver instanceof ZwaveDeviceDriver))
    {
      throw new ClassCastException(
          "Cannot attach non ZWave device to this interface");
    } else
    {
      logger.info("Attaching device driver [{}]", driver.getClass().getName());
      ZwaveDeviceDriver zwaveDevice = (ZwaveDeviceDriver) driver;

      if (drivers.containsKey(zwaveDevice.getNodeId()))
      {
        if (drivers.get(zwaveDevice.getNodeId()) instanceof ArrayList)
        {
          drivers.get(zwaveDevice.getNodeId()).add(zwaveDevice);
        } else
        {
          ArrayList<ZwaveDeviceDriver> tmp = new ArrayList<ZwaveDeviceDriver>();
          tmp.add(zwaveDevice);
          drivers.put(zwaveDevice.getNodeId(), tmp);
        }
      } else
      {
        ArrayList<ZwaveDeviceDriver> tmp = new ArrayList<ZwaveDeviceDriver>();
        tmp.add(zwaveDevice);
        drivers.put(zwaveDevice.getNodeId(), tmp);
      }

      if (this.interfaceReady && (driver instanceof InterfaceCallback))
      {
        interfaceReadyExecutor.schedule(new Runnable()
        {
          public void run()
          {
            try
            {
              // Delay so these don't run so fast
              Thread.sleep(2000);
            } catch (InterruptedException e)
            {
            }
            ((InterfaceCallback) driver).interfaceReady();
          }
        }, 0, TimeUnit.SECONDS);

      }
    }
    logger.exit();
  }

  private void cmdApplicationCommandHandler(int[] payload)
  {
    logger.entry(payload);
    int nodeId = payload[1];

    if (payload[3] == CommandClass.COMMAND_CLASS_BASIC.get())
    {
      logger.debug("Received COMMAND_CLASS_BASIC from node [{}]", nodeId);
      if (payload[4] == CommandBasic.BASIC_SET.get())
      {
        int value = payload[5];
        logger.debug("Received BASIC_SET from node [{}] with a value of [{}]",
            nodeId, value);

        if (drivers.containsKey(nodeId))
        {
          for (Object device : drivers.get(nodeId))
            if (device instanceof CommandClassBasic)
              ((CommandClassBasic) device).commandClassBasicSet(value);
        }
      } else if (payload[4] == CommandBasic.BASIC_REPORT.get())
      {
        int value = payload[5];
        logger.debug(
            "Received BASIC_REPORT from node [{}] with a value of [{}]",
            nodeId, value);

        if (drivers.containsKey(nodeId))
        {
          for (Object device : drivers.get(nodeId))
            if (device instanceof CommandClassBasic)
              ((CommandClassBasic) device).commandClassBasicReport(value);
        }
      }
    } else if (payload[3] == CommandClass.COMMAND_CLASS_SWITCH_MULTILEVEL.get())
    {
      logger.debug("Received COMMAND_CLASS_SWITCH_MULTILEVEL from node [{}]",
          nodeId);
      if (payload[4] == CommandSwitchMultilevel.SWITCH_MULTILEVEL_SET.get())
      {
        int value = payload[5];
        logger
            .debug(
                "Received SWITCH_MULTILEVEL_SET from node [{}] with a value of [{}]",
                nodeId, value);

        if (drivers.containsKey(nodeId))
        {
          for (Object device : drivers.get(nodeId))
            if (device instanceof CommandClassSwitchMultilevel)
              ((CommandClassSwitchMultilevel) device)
                  .commandClassSwitchMultilevelSet(value);
        }
      } else if (payload[4] == CommandSwitchMultilevel.SWITCH_MULTILEVEL_REPORT
          .get())
      {
        int value = payload[5];
        logger
            .debug(
                "Received SWITCH_MULTILEVEL_REPORT from node [{}] with a value of [{}]",
                nodeId, value);

        if (drivers.containsKey(nodeId))
        {
          for (Object device : drivers.get(nodeId))
            if (device instanceof CommandClassSwitchMultilevel)
              ((CommandClassSwitchMultilevel) device)
                  .commandClassSwitchMultilevelReport(value);
        }
      }
    } else if (payload[3] == CommandClass.COMMAND_CLASS_MANUFACTURER_SPECIFIC
        .get())
    {
      if (payload[4] == CommandManufacturerSpecific.MANUFACTURER_SPECIFIC_REPORT
          .get())
      {
        logger.debug("Received MANUFACTURER_SPECIFIC_REPORT from node [{}]",
            nodeId);
        if (drivers.containsKey(nodeId))
        {
          int manufacturer = (payload[5] << 8) | payload[6];
          int productType = (payload[7] << 8) | payload[8];
          int productId = (payload[9] << 8) | payload[10];
          for (Object device : drivers.get(nodeId))
            if (device instanceof CommandClassManufacturerSpecific)
              ((CommandClassManufacturerSpecific) device)
                  .commandClassManufacturerSpecificReport(manufacturer,
                      productType, productId);
        }
      }
    } else if (payload[3] == CommandClass.COMMAND_CLASS_SENSOR_BINARY.get())
    {
      if (payload[4] == CommandSensorBinary.SENSOR_BINARY_REPORT.get())
      {
        logger
            .debug(
                "Received SENSOR_BINARY_REPORT from node [{}] with a value of [{}]",
                nodeId, payload[5]);
        if (drivers.get(nodeId) != null)
        {
          for (Object device : drivers.get(nodeId))
            if (device instanceof CommandClassSensorBinary)
              ((CommandClassSensorBinary) device)
                  .commandClassSensorBinaryReport(payload[5]);
        }
      }
    } else if (payload[3] == CommandClass.COMMAND_CLASS_HAIL.get())
    {
      logger.debug("Received HAIL from node [{}]", nodeId);
      if (drivers.get(nodeId) != null)
      {
        for (Object device : drivers.get(nodeId))
          if (device instanceof CommandClassHail)
            ((CommandClassHail) device).hail();
      }
    }

    logger.exit();
  }

  public void dataPacketReceived(final CommandType cmd, final DataPacket packet)
  {
    logger.entry(cmd, packet);

    logger.debug("Creating packet worker thread");
    Thread thread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        logger.entry();

        final int[] payload = packet.getPayload();
        logger.trace("Payload = [{}]", payload);

        if (cmd == DataFrame.CommandType.CmdApplicationCommandHandler)
        {
          cmdApplicationCommandHandler(payload);
        }

        logger.exit();
      }
    });

    thread.setDaemon(true);
    logger.debug("Starting packet worker thread");
    thread.start();

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  @Override
  @PreDestroy
  public void destroy()
  {
    logger.entry();
    logger.info("Destroying Z-Wave interface");
    interfaceReadyExecutor.shutdownNow();
    keepaliveExecutor.shutdownNow();
    appLayer.destroy();
    logger.exit();
  }

  /**
   * Get node's neighbor table
   * 
   * @param nodeId
   * @return
   */
  public String getNodeNeighbors(int nodeId)
  {
    logger.entry(nodeId);
    String nodes = null;

    try
    {
      nodes = appLayer.getRoutingTableLine(nodeId, false, false).toString();
    } catch (FrameLayerException e)
    {
      logger
          .warn("Error sending routing table request to node [{}]", nodeId, e);
    } catch (ApplicationLayerException e)
    {
      logger
          .warn("Error sending routing table request to node [{}]", nodeId, e);
    }
    logger.exit(nodes);
    return nodes;
  }

  @Override
  public HashMap<String, HashMap<String, Object>> getNodes()
  {
    logger.entry();
    HashMap<String, HashMap<String, Object>> nodes = new HashMap<String, HashMap<String, Object>>();

    HashMap<String, Object> node;

    Node[] nodeList = appLayer.getAllNodes();

    for (Node n : nodeList)
    {
      node = new HashMap<String, Object>();
      node.put("basic", String.valueOf(n.getBasic()));
      node.put("capabilities", String.valueOf(n.getCapability()));
      node.put("generic", String.valueOf(n.getGeneric()));
      node.put("reserved", String.valueOf(n.getReserved()));
      node.put("security", String.valueOf(n.getSecurity()));
      node.put("specific", String.valueOf(n.getSpecific()));
      node.put("commandClasses", n.getSupportedCmdClasses());
      node.put("manufacturer", String.valueOf(n.getManufacturer()));
      node.put("productType", String.valueOf(n.getProductType()));
      node.put("productId", String.valueOf(n.getProductId()));
      nodes.put(String.valueOf(n.getId()), node);
    }

    logger.exit(nodes);
    return nodes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  public void init()
  {
    logger.entry();

    // Kill the executor if it's already running
    if (interfaceReadyExecutor != null)
      interfaceReadyExecutor.shutdownNow();

    interfaceReadyExecutor = Executors.newSingleThreadScheduledExecutor();

    if (this.properties != null && this.properties.containsKey(PROPERTY_HOST)
        && this.properties.containsKey(PROPERTY_PORT))
    {
      String host = this.properties.get(PROPERTY_HOST);
      logger.debug("Read property [{}] with value [{}]", PROPERTY_HOST, host);

      int port;

      try
      {
        port = Integer.valueOf(this.properties.get(PROPERTY_PORT));
        logger.debug("Read property [{}] with value [{}]", PROPERTY_PORT, port);
      } catch (NumberFormatException e)
      {
        logger.error(
            "Property [{}] is invalid - [{}] is not a valid TCP port number",
            PROPERTY_PORT, this.properties.get(PROPERTY_PORT));

        logger.exit(false);
        return;
      }

      FrameLayer frameLayer = new FrameLayerImpl(host, port);

      sessionLayer = new SessionLayerImpl(frameLayer);
      appLayer = new ApplicationLayerImpl(sessionLayer);
      appLayer.setCallbackHandler(this);

      this.zwaveInit();

      this.startKeepalives();
    }

    logger.exit();
  }

  /**
   * Request node command classes
   * 
   * @param nodeId
   *          Z-Wave node ID
   */
  public void requestNodeInfo(int nodeId)
  {
    logger.entry(nodeId);

    try
    {
      appLayer.zwaveRequestNodeInfo(nodeId);
    } catch (FrameLayerException e)
    {
      logger.error("Error requesting node info from node [{}]", nodeId, e);
    }

    logger.exit();
  }

  /**
   * Send a keep alive frame
   */
  private void sendKeepalive()
  {
    logger.entry();
    try
    {
      VersionInfoType zwaveVersion = appLayer.zwaveGetVersion();
      logger.info("Controller library: {}, version: {}",
          zwaveVersion.library.toString(), zwaveVersion.version.trim());
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
      logger.exit();
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
      logger.exit();
      return;
    }
    logger.exit();
  }

  /**
   * Tells attached drivers that this interface is ready for traffic
   */
  private void setInterfaceReady(boolean ready)
  {
    logger.entry(ready);
    this.interfaceReady = ready;

    if (ready && this.drivers != null)
    {
      for (ArrayList<ZwaveDeviceDriver> zwaveDrivers : this.drivers.values())
      {
        for (ZwaveDeviceDriver driver : zwaveDrivers)
        {
          if (driver instanceof InterfaceCallback)
          {
            ((InterfaceCallback) driver).interfaceReady();
          }
        }
      }
    }
    logger.exit();
  }

  /**
   * Start keep alive executor
   */
  private void startKeepalives()
  {
    logger.entry();
    logger.debug("Starting keepalive executor");

    keepaliveExecutor = Executors.newSingleThreadScheduledExecutor();
    keepaliveExecutor.scheduleWithFixedDelay(new Runnable()
    {
      public void run()
      {
        if ((System.currentTimeMillis() - sessionLayer
            .getLastFrameReceivedTime()) > (keepaliveHoldtimeSeconds * 1000))
        {
          logger.error("Keep alive holdtime expired, resetting transport");

          appLayer.destroy();

          init();

          try
          {
            // Give the interface time to reconnect before resuming keepalives
            Thread.sleep(20000);
          } catch (InterruptedException e)
          {
          }
        } else
        {
          if ((System.currentTimeMillis() - sessionLayer
              .getLastFrameReceivedTime()) > (keepaliveIntervalSeconds * 1000))
          {
            logger.debug("Sending keep alive request");
            sendKeepalive();
          }
        }

      }
    }, 0, keepaliveIntervalSeconds, TimeUnit.SECONDS);

    logger.exit();
  }

  /**
   * Request node to update its return route to this node
   * 
   * @param nodeId
   * @return
   */
  public boolean zwaveAssignReturnRoute(int nodeId)
  {
    logger.entry(nodeId);
    TXStatus status = null;
    int controllerNodeId = appLayer.getControllerNodeId();

    try
    {
      status = appLayer.zwaveAssignReturnRoute(nodeId, controllerNodeId);
    } catch (FrameLayerException e)
    {
      logger.warn("Error sending assign return route request to node [{}]",
          nodeId, e);
    }

    if (status == TXStatus.CompleteOk)
    {
      logger.exit(true);
      return true;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /**
   * Request node to delete its return route to this node
   * 
   * @param nodeId
   * @return
   */
  public boolean zwaveDeleteReturnRoute(int nodeId)
  {
    logger.entry(nodeId);
    TXStatus status = null;

    try
    {
      status = appLayer.zwaveDeleteReturnRoute(nodeId);
    } catch (FrameLayerException e)
    {
      logger.warn("Error sending delete return route request to node [{}]",
          nodeId, e);
    }

    if (status == TXStatus.CompleteOk)
    {
      logger.exit(true);
      return true;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /**
   * Initialize the Z-Wave interface
   */
  public void zwaveInit()
  {
    logger.entry();
    try
    {
      VersionInfoType zwaveVersion = appLayer.zwaveGetVersion();
      logger.info("Controller library: {}, version: {}",
          zwaveVersion.library.toString(), zwaveVersion.version.trim());
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
      logger.exit();
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
      logger.exit();
      return;
    }

    try
    {
      MemoryGetId zwaveId = appLayer.zwaveMemoryGetId();
      logger.info("Controller home ID: {}, node ID: {}",
          String.format("%#06x", zwaveId.getHomeId()), zwaveId.getNodeId());
    } catch (FrameLayerException e)
    {
      logger.error(
          "Error retrieving home ID and node ID from ZWave controller", e);
      logger.exit();
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error(
          "Error retrieving home ID and node ID from ZWave controller", e);
      logger.exit();
      return;
    }

    try
    {
      appLayer.zwaveGetControllerCapabilities();
      if (appLayer.isControllerIsRealPrimary())
        logger
            .info("Controller is the original owner of the current Z-Wave network Home ID");
      if (appLayer.isSlaveController())
        logger.info("Controller is a slave controller");
      if (appLayer.isControllerIsSuc())
        logger.info("Controller is the SUC");
      if (appLayer.isControllerOnOtherNetwork())
        logger.info("Controller is on other network");
      if (appLayer.isNodeIdServerPresent())
        logger.info("A SIS server is present on this network");
    } catch (ApplicationLayerException e)
    {
      logger.error("Error retrieving ZWave controller capabilities", e);
      logger.exit();
      return;
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving ZWave controller capabilities", e);
      logger.exit();
      return;
    }

    try
    {
      SerialApiCapabilities serialApiCapabilities = appLayer
          .zwaveSerialApiGetCapabilities();
      logger
          .info(
              "Serial API capabilities version: {}, manufacturer ID: {} ({}), manufacturer product type: {}, manufacturer product: {}",
              new Object[] {
                  serialApiCapabilities.getVersion(),
                  String.format("%#02x",
                      serialApiCapabilities.getManufacturer()),
                  Manufacturer.getByVal(serialApiCapabilities.getManufacturer()),
                  String.format("%#02x", serialApiCapabilities.getProductType()),
                  String.format("%#02x", serialApiCapabilities.getProductId()) });
      logger.info("Supported serial commands: {}",
          appLayer.getSupportedSerialCmds());
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving ZWave serial API capabilities", e);
      logger.exit();
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error("Error retrieving ZWave serial API capabilities", e);
      logger.exit();
      return;
    }

    try
    {
      appLayer.zwaveEnumerateNodes();
      logger.debug("Z-Wave chip information: {}{}", appLayer.getChipType(),
          appLayer.getChipRev());

    } catch (FrameLayerException e)
    {
      logger.error("Error enumerating ZWave nodes", e);
    } catch (ApplicationLayerException e)
    {
      logger.error("Error enumerating ZWave nodes", e);
    }

    setInterfaceReady(true);
    logger.exit();
  }

  /**
   * Request node to update it's neighbor table
   * 
   * @param nodeId
   * @return
   */
  public boolean zwaveRequestNodeNeighborUpdate(int nodeId)
  {
    logger.entry(nodeId);
    RequestNeighbor status = null;

    try
    {
      status = appLayer.zwaveRequestNodeNeighborUpdate(nodeId);
    } catch (FrameLayerException e)
    {
      logger.warn("Error sending delete return route request to node [{}]",
          nodeId, e);
    }

    if (status == RequestNeighbor.UpdateDone)
    {
      logger.exit(true);
      return true;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /**
   * Send data out Z-Wave interface
   * 
   * @param nodeId
   * @param data
   * @return
   */
  public boolean zwaveSendData(int nodeId, int... data)
  {
    logger.entry(nodeId, data);
    try
    {
      TXStatus txStatus = appLayer.zwaveSendData(nodeId, data,
          new TXOption[] { TXOption.TransmitOptionAcknowledge,
              TXOption.TransmitOptionAutoRoute });
      if (txStatus == TXStatus.CompleteOk)
      {
        logger.exit(true);
        return true;
      }
    } catch (FrameLayerException e)
    {
      logger.exit(false);
      return false;
    }

    logger.exit(false);
    return false;
  }
}
