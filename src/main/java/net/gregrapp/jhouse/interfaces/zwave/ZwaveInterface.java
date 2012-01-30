/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.device.drivers.types.ZwaveDeviceDriver;
import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.NodeInterface;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.MemoryGetId;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.SerialApiCapabilities;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandSensorBinary;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassBasic;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.command.CommandClassSensorBinary;
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Z-Wave network interface
 * 
 * @author Greg Rapp
 * 
 */
public class ZwaveInterface extends AbstractInterface implements
    ApplicationLayerAsyncCallback, NodeInterface
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveInterface.class);

  private ApplicationLayer appLayer;

  private Map<Integer, ArrayList<ZwaveDeviceDriver>> devices = new HashMap<Integer, ArrayList<ZwaveDeviceDriver>>();

  public ZwaveInterface(Transport transport)
  {
    super(transport);
  }

  public void attachDeviceDriver(DeviceDriver device)
  {
    if (!(device instanceof ZwaveDeviceDriver))
    {
      throw new ClassCastException(
          "Cannot attach non ZWave device to this interface");
    }
    else
    {
      logger.info("Attaching device driver: {}", device.getClass().getName());
      ZwaveDeviceDriver zwaveDevice = (ZwaveDeviceDriver) device;

      if (devices.containsKey(zwaveDevice.getNodeId()))
      {
        if (devices.get(zwaveDevice.getNodeId()) instanceof ArrayList)
        {
          devices.get(zwaveDevice.getNodeId()).add(zwaveDevice);
        } else
        {
          ArrayList<ZwaveDeviceDriver> tmp = new ArrayList<ZwaveDeviceDriver>();
          tmp.add(zwaveDevice);
          devices.put(zwaveDevice.getNodeId(), tmp);
        }
      } else
      {
        ArrayList<ZwaveDeviceDriver> tmp = new ArrayList<ZwaveDeviceDriver>();
        tmp.add(zwaveDevice);
        devices.put(zwaveDevice.getNodeId(), tmp);
      }

      // if (this.interfaceReady)
      // device.interfaceReady();
    }
  }

  private void cmdApplicationCommandHandler(int[] payload)
  {
    int nodeId = payload[1];

    if (payload[3] == CommandClass.COMMAND_CLASS_BASIC.get())
    {
      logger.info("COMMAND_CLASS_BASIC from node {}", nodeId);
      if (payload[4] == CommandBasic.BASIC_SET.get())
      {
        int value = payload[5];
        logger.info("Received BASIC_SET from node {} with a value of {}",
            nodeId, value);

        if (devices.containsKey(nodeId))
        {
          for (Object device : devices.get(nodeId))
            if (device instanceof CommandClassBasic)
              ((CommandClassBasic) device).commandClassBasicSet(value);
        }
      }
      else if (payload[4] == CommandBasic.BASIC_REPORT.get())
      {
        int value = payload[5];
        logger.info("Received BASIC_REPORT from node {} with a value of {}",
            nodeId, value);

        if (devices.containsKey(nodeId))
        {
          for (Object device : devices.get(nodeId))
            if (device instanceof CommandClassBasic)
              ((CommandClassBasic) device).commandClassBasicReport(value);
        }
      }
    } else if (payload[3] == CommandClass.COMMAND_CLASS_MANUFACTURER_SPECIFIC
        .get())
    {
      if (payload[4] == CommandManufacturerSpecific.MANUFACTURER_SPECIFIC_REPORT
          .get())
      {
        if (devices.containsKey(nodeId))
        {
          int manufacturer = (payload[5] << 8) | payload[6];
          int productType = (payload[7] << 8) | payload[8];
          int productId = (payload[9] << 8) | payload[10];
          for (Object device : devices.get(nodeId))
            if (device instanceof CommandClassManufacturerSpecific)
              ((CommandClassManufacturerSpecific) device)
                  .commandClassManufacturerSpecificReport(manufacturer,
                      productType, productId);
        }
      }
    }
    else if (payload[3] == CommandClass.COMMAND_CLASS_SENSOR_BINARY.get())
    {
      if (payload[4] == CommandSensorBinary.SENSOR_BINARY_REPORT.get())
      {
        for (Object device : devices.get(nodeId))
          if (device instanceof CommandClassSensorBinary)
            ((CommandClassSensorBinary) device)
                .commandClassSensorBinaryReport(payload[5]);
      }
    }

  }

  public void dataPacketReceived(CommandType cmd, DataPacket packet)
  {
    int[] payload = packet.getPayload();
    if (cmd == DataFrame.CommandType.CmdApplicationCommandHandler)
    {
      cmdApplicationCommandHandler(payload);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public HashMap<String, HashMap<String, Object>> getNodes()
  {
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

    return nodes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  public void init()
  {
    try
    {
      this.transport.init();
    } catch (TransportException e)
    {
      logger
          .error("Error opening transport, aborting Z-Wave initialization [{}]", e.getMessage());
      return;
    }

    FrameLayer frameLayer = new FrameLayerImpl(this.transport);
    SessionLayer sessionLayer = new SessionLayerImpl(frameLayer);
    appLayer = new ApplicationLayerImpl(sessionLayer);
    appLayer.setCallbackHandler(this);

    zwaveInit();
  }

  /**
   * Request node command classes
   * 
   * @param nodeId
   *          Z-Wave node ID
   */
  public void requestNodeInfo(int nodeId)
  {
    try
    {
      appLayer.zwaveRequestNodeInfo(nodeId);
    } catch (FrameLayerException e)
    {
      logger.error("Error requesting node info from node {}", nodeId, e);
    }
  }

  /**
   * Tells attached devices that this interface is ready for traffic
   */
  private void setInterfaceReady(boolean ready)
  {
    this.interfaceReady = ready;

    if (ready && this.devices != null)
    {
      for (ArrayList<ZwaveDeviceDriver> zwaveDevices : this.devices.values())
      {
        for (ZwaveDeviceDriver dev : zwaveDevices)
        {
          dev.interfaceReady();
        }
      }
    }
  }

  /**
   * Initialize the Z-Wave interface
   */
  public void zwaveInit()
  {
    try
    {
      VersionInfoType zwaveVersion = appLayer.zwaveGetVersion();
      logger.info("Controller library: {}, version: {}",
          zwaveVersion.library.toString(), zwaveVersion.version.trim());
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error("Error retrieving version info from ZWave controller", e);
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
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error(
          "Error retrieving home ID and node ID from ZWave controller", e);
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
      return;
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving ZWave controller capabilities", e);
      return;
    }

    try
    {
      SerialApiCapabilities serialApiCapabilities = appLayer
          .zwaveSerialApiGetCapabilities();
      logger
          .info(
              "Serial API capabilities version: {}, manufacturer ID: {}, manufacturer product type: {}, manufacturer product: {}",
              new Object[] {
                  serialApiCapabilities.getVersion(),
                  String.format("%#02x",
                      serialApiCapabilities.getManufacturer()),
                  String.format("%#02x", serialApiCapabilities.getProductType()),
                  String.format("%#02x", serialApiCapabilities.getProductId()) });
      logger.info("Supported serial commands: {}",
          appLayer.getSupportedSerialCmds());
    } catch (FrameLayerException e)
    {
      logger.error("Error retrieving ZWave serial API capabilities", e);
      return;
    } catch (ApplicationLayerException e)
    {
      logger.error("Error retrieving ZWave serial API capabilities", e);
      return;
    }

    try
    {
      appLayer.zwaveEnumerateNodes();
      logger.debug("Z-Wave chip informtion: {}{}", appLayer.getChipType(),
          appLayer.getChipRev());

    } catch (FrameLayerException e)
    {
      logger.error("Error enumerating ZWave nodes", e);
    } catch (ApplicationLayerException e)
    {
      logger.error("Error enumerating ZWave nodes", e);
    }

    setInterfaceReady(true);
  }

  public boolean zwaveSendData(int nodeId, int... data)
  {
    try
    {
      TXStatus txStatus = appLayer.zwaveSendData(nodeId, data,
          new TXOption[] { TXOption.TransmitOptionAcknowledge,
              TXOption.TransmitOptionAutoRoute });
      if (txStatus == TXStatus.CompleteOk)
        return true;
    } catch (FrameLayerException e)
    {
      return false;
    }

    return false;
  }
}
