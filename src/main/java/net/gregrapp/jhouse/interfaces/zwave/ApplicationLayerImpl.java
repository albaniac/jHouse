//////////////////////////////////////////////////////////////////////////////////////////////// 
//
//          #######
//          #   ##    ####   #####    #####  ##  ##   #####
//             ##    ##  ##  ##  ##  ##      ##  ##  ##
//            ##  #  ######  ##  ##   ####   ##  ##   ####
//           ##  ##  ##      ##  ##      ##   #####      ##
//          #######   ####   ##  ##  #####       ##  #####
//                                           #####
//          Z-Wave, the wireless language.
//
//          Copyright Zensys A/S, 2005
//
//          All Rights Reserved
//
//          Description:   
//
//          Author:      Jette Christensen
//
//          Last Changed By:  $Author: jrm $
//          Revision:         $Revision: 1.21 $
//          Last Changed:     $Date: 2007/03/15 15:03:23 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.gregrapp.jhouse.interfaces.zwave.Constants.ChipType;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandManufacturerSpecific;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ControllerChangeMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CreateNewPrimaryControllerMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.LearnMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Library;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Mode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.NodeFailedReturnValue;
import net.gregrapp.jhouse.interfaces.zwave.Constants.NodeFailedStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.NodeStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.RequestNeighbor;
import net.gregrapp.jhouse.interfaces.zwave.Constants.SetSucReturnValue;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ZWaveRediscoveryNeededReturnValue;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;
import net.gregrapp.jhouse.utils.ArrayUtils;
import net.gregrapp.jhouse.utils.CollectionUtils;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class ApplicationLayerImpl implements ApplicationLayer,
    SessionLayerAsyncCallback
{
  // ApplicationControllerUpdate status
  private enum AppCtrlUpdateStatus
  {
    ADD_DONE(0x40), DELETE_DONE(0x20), NODE_INFO_RECEIVED(0x84), NODE_INFO_REQ_DONE(
        0x82), NODE_INFO_REQ_FAILED(0x81), ROUTING_PENDING(0x80), SUC_ID(0x10);

    public static AppCtrlUpdateStatus getByVal(int value)
    {
      for (AppCtrlUpdateStatus s : AppCtrlUpdateStatus.class.getEnumConstants())
        if (s.get() == value)
          return s;
      return null;
    }

    private int value;

    AppCtrlUpdateStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * Controller capabilities
   * 
   * @author Greg Rapp
   */
  public class ControllerCapabilities
  {
    private int[] capabilities;

    private boolean slave;

    public ControllerCapabilities(int[] capabilities, boolean slave)
    {
      this.capabilities = capabilities;
      this.slave = slave;
    }

    /**
     * @return Capabilities flag:<br>
     *         Bit 0 : 0 = Controller API; 1 = Slave API<br>
     *         Bit 1 : 0 = Timer functions not supported; 1 = Timer functions
     *         supported.<br>
     *         Bit 2 : 0 = Primary Controller; 1 = Secondary Controller <br>
     *         Bit 3-7: reserved <br>
     */
    public int[] getCapabilities()
    {
      return capabilities;
    }

    /**
     * @return <code>true</code> if this is a slave controller
     */
    public boolean isSlave()
    {
      return slave;
    }

    /**
     * @param slave
     *          the slave to set
     */
    public void setSlave(boolean slave)
    {
      this.slave = slave;
    }
  }

  private enum CtrlCapabilities
  {
    /**
     * Controller is the original owner of the current Z-Wave network Home ID
     */
    IS_REAL_PRIMARY(0x08),

    /**
     * Controller is secondary on current Z-Wave network
     */
    IS_SECONDARY(0x01),

    /**
     * Controller is the SUC in current Z-Wave network
     */
    IS_SUC(0x10),

    /**
     * Controller is a member of a Z-Wave network with a Node ID Server present
     */
    NODEID_SERVER_PRESENT(0x04),

    /**
     *
     */
    ON_OTHER_NETWORK(0x02);

    private int value;

    CtrlCapabilities(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  public class MemoryGetId
  {
    private int homeId;
    private int nodeId;
    private TXStatus txStatus;

    public MemoryGetId(int homeId, int nodeId, TXStatus txStatus)
    {
      this.homeId = homeId;
      this.nodeId = nodeId;
    }

    public int getHomeId()
    {
      return homeId;
    }

    public int getNodeId()
    {
      return nodeId;
    }

    public TXStatus getTxStatus()
    {
      return txStatus;
    }
  }

  /**
   * Nodemask handling class
   * 
   * @author Greg Rapp
   * 
   */
  public class NodeBitmask
  {
    private int[] mask;

    // private int maxNodeID;

    /**
     * Initializes a new instance of a {@link NodeBitMask}
     */
    public NodeBitmask()
    {
      mask = null;
    }

    /**
     * Creates a nodemask object
     * 
     * @param length
     *          length of node mask (number of node IDs divided by 8)
     */
    public NodeBitmask(int length)
    {
      if (length > 0)
      {
        mask = new int[length];
        // maxNodeID = length*8;
      }
    }

    /**
     * Gets the node mask as an bit array
     * 
     * @return node mask bit array
     */
    public int[] get()
    {
      return mask;
    }

    // <summary>
    // Returns the bitMask for the given NodeId
    // </summary>
    // <param name="nodeId"></param>
    // <returns></returns>
    public int get(int nodeId)
    {
      return mask[nodeId >> 3];
    }

    /**
     * @return node mask array length
     */
    public int getLength()
    {
      if (mask != null)
      {
        return mask.length;
      } else
      {
        return 0;
      }
    }

    /**
     * Store an array as the node mask
     * 
     * @param array
     *          array of node masks
     */
    public void store(int[] array)
    {
      if (array == null)
      {
        throw new NullPointerException("array");
      }
      if (mask == null)
      {
        mask = new int[array.length];
      }
      for (int i = 0; i < mask.length; i++)
      {
        mask[i] = array[i];
      }

    }

    /**
     * Returns true if any bits in mask are set
     * 
     * @return <code>true</code> if any bits are set, <code>false</code> if not
     */
    public boolean zwaveNodeMaskBitsIn()
    {
      if (mask != null)
      {
        for (int i = 0; i < mask.length; i++)
        {
          if (mask[i] != 0)
          {
            return true;
          }
        }
      }
      return false;
    }

    /**
     * Clear the node mask
     */
    public void zwaveNodeMaskClear()
    {
      logger.debug("Clearning node mask");
      if (mask != null)
      {
        for (int i = 0; i < mask.length; i++)
        {
          mask[i] = 0;
        }
      }
    }

    /**
     * Clears the bit corresponding to the node ID supplied
     * 
     * @param nodeId
     *          node ID to clear
     */
    public void zwaveNodeMaskClearBit(int nodeId)
    {
      if (nodeId < 1)
      {
        return;
      }
      nodeId--;
      mask[nodeId >> 3] &= ~(0x1 << (nodeId & 7));
    }

    /**
     * Checks if the bit corresponding to the node ID is set
     * 
     * @param nodeId
     *          node ID to check
     * @return <code>true</code> if bit is set
     */
    public boolean zwaveNodeMaskNodeIn(int nodeId)
    {
      if (nodeId < 1)
      {
        return false;
      }
      nodeId--;
      if ((((mask[(nodeId >> 3)]) >> (nodeId & 7)) & 0x01) != 0)
      {
        return true;
      } else
      {
        return false;
      }
    }

    /**
     * Set the bit corresponding to the node ID supplied
     * 
     * @param nodeId
     *          node ID of bit to set
     */
    public void zwaveNodeMaskSetBit(int nodeId)
    {
      if (nodeId < 1)
      {
        return;
      }
      nodeId--;
      mask[nodeId >> 3] |= (0x1 << (nodeId & 7));
    }

  }

  public class NodeMask
  {
    private int[] nodeMask;

    /**
     * Constructor
     */
    public NodeMask()
    {
      nodeMask = new int[29];
    }

    /**
     * @param nodeMask
     */
    public NodeMask(int[] nodeMask)
    {
      if (nodeMask == null)
      {
        throw new NullPointerException("nodeMask");
      }

      if (this.nodeMask == null)
      {
        this.nodeMask = new int[29];
      }

      if (nodeMask.length == 29)
      {
        for (int i = 0; i < 29; i++)
        {
          this.nodeMask[i] = nodeMask[i];
        }
      }
    }

    /**
     * Clear nodemask
     */
    public void clear()
    {
      for (int i = 0; i < 29; i++)
      {
        nodeMask[i] = 0;
      }
    }

    /**
     * Get node state
     * 
     * @param nodeId
     * @return
     */
    public boolean get(int nodeId)
    {
      if ((nodeId > 0) && (nodeId <= 232))
      {
        return ((nodeMask[(nodeId - 1) >> 3] & (1 << ((nodeId - 1) & 0x07))) != 0);
      }
      return false;
    }

    /**
     * Set node state
     * 
     * @param nodeId
     * @param value
     * @return
     */
    public boolean set(int nodeId, boolean value)
    {
      if ((nodeId > 0) && (nodeId <= 232))
      {
        if (value)
        {
          /* Set nodeMask bit */
          nodeMask[(nodeId - 1) >> 3] |= (1 << ((nodeId - 1) & 0x07));
        } else
        {
          /* Clear nodeMask bit */
          nodeMask[(nodeId - 1) >> 3] &= ~(1 << ((nodeId - 1) & 0x07));
        }
        return true;
      }
      return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      if (this.nodeMask == null)
      {
        return "";
      } else
      {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (int i = 0; i < 232; i++)
        {
          if (this.get(i))
          {
            if (buffer.length() > 1)
              buffer.append(",");
            buffer.append(i);
          }
        }
        buffer.append("]");
        return buffer.toString();
      }
    }
  }

  private class NodeTable
  {
    private Hashtable<Integer, Node> nodeTable;

    public NodeTable(int initialSize)
    {
      nodeTable = new Hashtable<Integer, Node>(initialSize);
    }

    public void add(Node node)
    {
      if (node == null)
        return;
      if (nodeTable.contains(node.getId()))
        nodeTable.remove(node.getId());
      nodeTable.put(node.getId(), node);
    }

    public void clear()
    {
      nodeTable.clear();
    }

    public boolean contains(int nid)
    {
      return nodeTable.contains(nid);
    }

    public Node get(int nid)
    {
      return nodeTable.get(nid);
    }

    public Node[] getList()
    {
      Node[] nodes = new Node[nodeTable.size()];
      nodes = nodeTable.values().toArray(new Node[0]);
      return nodes;
    }

    public void remove(int nid)
    {
      nodeTable.remove(nid);
    }

    public int size()
    {
      return nodeTable.size();
    }
  }

  // <summary>
  // NodeMask
  // </summary>

  /**
   * @author Greg Rapp
   * 
   */
  public class SerialApiCapabilities
  {
    NodeBitmask capabilityMask;
    private int manufacturer;
    private int productId;
    private int productType;
    private String version;

    /**
     * @param version
     * @param manufacturer
     * @param productType
     * @param productId
     */
    public SerialApiCapabilities(String version, int manufacturer,
        int productType, int productId)
    {
      this.version = version;
      this.manufacturer = manufacturer;
      this.productType = productType;
      this.productId = productId;
      capabilityMask = new NodeBitmask();
    }

    /**
     * @return the serialCapabilityMask
     */
    public NodeBitmask getCapabilityMask()
    {
      return capabilityMask;
    }

    /**
     * @return the manufacturer
     */
    public int getManufacturer()
    {
      return manufacturer;
    }

    /**
     * @return the productId
     */
    public int getProductId()
    {
      return productId;
    }

    /**
     * @return the productType
     */
    public int getProductType()
    {
      return productType;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
      return version;
    }

    /**
     * @param serialCapabilityMask
     *          the serialCapabilityMask to set
     */
    public void setCapabilityMask(NodeBitmask capabilityMask)
    {
      this.capabilityMask = capabilityMask;
    }
  }

  private static final int DEFAULT_TIMEOUT = 10000; // How long in ms to wait

  @SuppressWarnings("unused")
  private static final int GET_INIT_DATA_FLAG_IS_SUC = 0x08;

  private static final int GET_INIT_DATA_FLAG_SECONDARY_CTRL = 0x04;

  private static final int GET_INIT_DATA_FLAG_SLAVE_API = 0x01;

  @SuppressWarnings("unused")
  private static final int GET_INIT_DATA_FLAG_TIMER_SUPPORT = 0x02;

  private static final XLogger logger = XLoggerFactory
      .getXLogger(ApplicationLayerImpl.class);

  // for an response
  private static final int TIMEOUT = 180000; // wait 3 minutes before timing out

  @SuppressWarnings("unused")
  private static final int ZWAVE_PROTECT_TIME = 30000;

  // private boolean _addNode;

  // out
  private Node addedNode;

  private ApplicationLayerAsyncCallback asyncCallback;

  int chipRev = 0; // if chipType still is undefined after the

  int chipType = 0; // Initially we do not know which Chip is used and

  @SuppressWarnings("unused")
  private boolean controllerChange;

  @SuppressWarnings("unused")
  private int controllerHomeId;

  private int controllerNodeId;

  // Controller capabilities
  private int[] ctrlCapabilities = null;

  private FrameLayer frameLayer;

  private VersionInfoType libraryType;

  private MemoryGetId memoryGetId;

  // private boolean disposed;

  @SuppressWarnings("unused")
  private boolean newPrimary;

  @SuppressWarnings("unused")
  private Node[] nodeList;

  private NodeTable nodeTable = new NodeTable(100);

  @SuppressWarnings("unused")
  private Node removedNode;

  @SuppressWarnings("unused")
  private boolean removeNode;

  private boolean requestNodeInfo = false;

  private boolean secondaryController;

  private SerialApiCapabilities serialApiCapabilities;

  // <summary>
  // Contains a mask of which SerialAPI commands supported by connected module
  // </summary>
  // NodeBitmask serialCapabilityMask = new NodeBitmask();

  int serialAPIver = 0;

  private SessionLayer sessionLayer;

  private boolean slaveApi;

  private ScheduledExecutorService waitForNodeInfoExecutor;

  public ApplicationLayerImpl(SessionLayer sessionLayer)
  {
    logger.entry(sessionLayer);
    this.sessionLayer = sessionLayer;
    sessionLayer.setCallbackHandler(this);
    logger.exit();
  }

  private DataPacket addPayloadToSUCNodeId(int nodeId, boolean sucState,
      TXOption[] txOptions, int capabilities)
  {
    logger.entry(nodeId, sucState, txOptions, capabilities);
    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(sucState ? 1 : 0);
    req.addPayload(txOptInt);
    req.addPayload(capabilities);
    logger.exit(req);
    return req;
  }

  public boolean clockCompare(Time time) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(time);
    logger.info("Comparing interface clock time with time [{}]", time);

    if (time == null)
    {
      throw new NullPointerException("time");
    }
    DataPacket req = new DataPacket();
    req.addPayload(time.weekday);
    req.addPayload(time.hour);
    req.addPayload(time.minute);

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdClockCompare, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_CLOCK_CMP");
    int[] payload = rc.getResponse().getPayload();
    boolean success = payload[0] == 0 ? false : true;
    logger.exit(success);
    return success;
  }

  public Time clockGet() throws FrameLayerException, ApplicationLayerException
  {
    logger.entry();
    logger.info("Getting interface clock time");

    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdClockGet, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_CLOCK_GET");
    int[] payload = rc.getResponse().getPayload();
    Time time = new Time(payload[0], payload[1], payload[2]);
    logger.exit(time);
    return time;
  }

  public void clockSet(Time time) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(time);
    logger.info("Setting interface clock to [{}]", time);

    if (time == null)
    {
      throw new NullPointerException("time");
    }
    DataPacket req = new DataPacket();
    req.addPayload(time.weekday);
    req.addPayload(time.hour);
    req.addPayload(time.minute);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdClockSet, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_CLOCK_SET");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.SessionLayerAsyncCallback#
   * dataPacketReceived
   * (net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType,
   * net.gregrapp.jhouse.interfaces.zwave.DataPacket)
   */
  public void dataPacketReceived(CommandType cmd, DataPacket packet)
  {
    logger.entry(cmd, packet);
    logger.debug("Data packet received for command [{}]", cmd);

    if (packet == null)
    {
      logger.warn("Null data packet received");
      throw new NullPointerException("packet");
    }

    int[] payload = packet.getPayload();

    if (cmd == DataFrame.CommandType.CmdApplicationCommandHandler)
    {
      if (payload[3] == CommandClass.COMMAND_CLASS_BASIC.get())
      {
        logger.info("COMMAND_CLASS_BASIC");
        if (payload[4] == CommandBasic.BASIC_REPORT.get())
        {
          logger.info("Received BASIC_REPORT from node {} with a value of {}",
              payload[1], payload[5]);
        }
      } else if (payload[3] == CommandClass.COMMAND_CLASS_MANUFACTURER_SPECIFIC
          .get())
      {
        if (payload[4] == CommandManufacturerSpecific.MANUFACTURER_SPECIFIC_REPORT
            .get())
        {
          int nodeId = payload[1];
          int manufacturer = (payload[5] << 8) | payload[6];
          int productType = (payload[7] << 8) | payload[8];
          int productId = (payload[9] << 8) | payload[10];
          logger
              .debug(
                  "Received MANUFACTURER_SPECIFIC_REPORT for node {}, manufacturer: {}, product type: {}, product id: {}",
                  new Object[] { nodeId, String.format("%#02x", manufacturer),
                      String.format("%#x", productType),
                      String.format("%#x", productId) });
          Node node = nodeTable.get(payload[1]);
          node.setManufacturer(manufacturer);
          node.setProductType(productType);
          node.setProductId(productId);
        }
      }
    } else if (cmd == DataFrame.CommandType.CmdApplicationSlaveCommandHandler)
    {
      // ApplicationSlaveCommandEventArgs e = new
      // ApplicationSlaveCommandEventArgs(packet);
      // if (ApplicationSlaveCommandEvent != null)
      // ApplicationSlaveCommandEvent(this, e);
    } else if (cmd == DataFrame.CommandType.CmdZWaveControllerChange
        || cmd == DataFrame.CommandType.CmdZWaveCreateNewPrimary
        || cmd == DataFrame.CommandType.CmdZWaveAddNodeToNetwork)
    {
      // 0 1 2 3 4 5 6 7
      // FuncID|status|nodeId|len|basic|generic|specific|data[0]|data[1],data[2]..data[len-7]....
      int nid = payload[2];
      NodeStatus nodeStatus = NodeStatus.getByVal(payload[1]);
      logger.debug("Node [{}] status is [{}]", nid, nodeStatus);
      if (nodeStatus == NodeStatus.AddingRemovingSlave
          || nodeStatus == NodeStatus.AddingRemovingController)
      { // ZWNode(byte id , byte capability, byte security, byte reserved, byte
        // basic, byte generic, byte specific)
        if (payload.length >= 4)
        {
          if (payload[3] >= 3)
          {
            addedNode = new Node(nid, 0, 0, 0, payload[4], payload[5],
                payload[6]);
            logger.info("Node added: {}", addedNode);
            if ((payload.length - 7) > 0)
            {
              int[] supportedCmdClasses = new int[payload.length - 7];
              for (int i = 0; i < addedNode.getSupportedCmdClasses().length; i++)
              {
                supportedCmdClasses[i] = payload[i + 7];
              }
              logger.debug("Supported command classes: {}",
                  ArrayUtils.toHexStringArray(supportedCmdClasses));
              addedNode.setSupportedCmdClasses(supportedCmdClasses);
            }
            nodeTable.add(addedNode);
          }
        }
      } else if (nodeStatus == NodeStatus.Done)
      {
        try
        {
          addedNode = zwaveGetNodeProtocolInfo(nid);
        } catch (FrameLayerException e)
        {
          logger.warn("Error getting node protocol info", e);
        } catch (ApplicationLayerException e)
        {
          logger.warn("Error getting node protocol info", e);
        }
        // AddNodeEventArgs e = new AddNodeEventArgs(addedNode, cmd,
        // nodeStatus);
        // if (AddNodeEvent != null) AddNodeEvent(this, e);
      }

      else if (nodeStatus == NodeStatus.Failed)
      {
        nodeTable.remove(nid);
        // AddNodeEventArgs e = new AddNodeEventArgs(addedNode, cmd,
        // nodeStatus);
        // if (AddNodeEvent != null) AddNodeEvent(this, e);
      }

      else if (nodeStatus == NodeStatus.ProtocolDone)
      {
        // AddNodeEventArgs e = new AddNodeEventArgs(addedNode, cmd,
        // nodeStatus);
        // if (AddNodeEvent != null) AddNodeEvent(this, e);
      }
    }

    else if (cmd == DataFrame.CommandType.CmdZWaveRemoveNodeFromNetwork)
    {
      NodeStatus nodeStatus = NodeStatus.getByVal(payload[1]);

      if (nodeStatus == NodeStatus.AddingRemovingController
          || nodeStatus == NodeStatus.AddingRemovingSlave
          || nodeStatus == NodeStatus.LearnReady
          || nodeStatus == NodeStatus.NodeFound)
      {
        logger.debug("Node status is [{}]", nodeStatus);
      } else if (nodeStatus == NodeStatus.Done)
      {
        int nid = payload[2];
        logger.debug("Node [{}] status is [{}]", nid, nodeStatus);
        if (nodeTable.contains(nid))
        {
          removedNode = nodeTable.get(nid);
          nodeTable.remove(nid);
        }
        // RemoveNodeEventArgs e = new RemoveNodeEventArgs(removedNode, cmd);
        // if (RemoveNodeEvent != null) RemoveNodeEvent(this, e);
      }

      else if (nodeStatus == NodeStatus.Failed)
      {
        logger.warn("Node status is [{}]", nodeStatus);
        // RemoveNodeEventArgs e = new RemoveNodeEventArgs(removedNode, cmd);
        // if (RemoveNodeEvent != null) RemoveNodeEvent(this, e);
      }

    }

    else if (cmd == DataFrame.CommandType.CmdApplicationControllerUpdate)
    {
      int nid = payload[1];
      AppCtrlUpdateStatus appCtrlUpdateStatus = AppCtrlUpdateStatus
          .getByVal(payload[0]);

      // Node requested removed from remote control...
      if (appCtrlUpdateStatus == AppCtrlUpdateStatus.DELETE_DONE)
      {
        if (nodeTable.contains(nid))
        {
          // Node node = nodeTable.get(nid);
          nodeTable.remove(nid);
          // RemoveNodeEventArgs e = new RemoveNodeEventArgs(node, cmd);
          // if (RemoveNodeEvent != null) RemoveNodeEvent(this, e);
        }
      }

      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.ADD_DONE)
      {
        Node node = null;
        if (libraryType.library == Library.ControllerBridgeLib)
        {
          try
          {
            node = zwaveGetNodeProtocolInfo(nid, true);
          } catch (FrameLayerException e)
          {
            logger
                .error("Error getting node protocol info: {}", e.getMessage());
          } catch (ApplicationLayerException e)
          {
            logger
                .error("Error getting node protocol info: {}", e.getMessage());
          }
        } else
        {
          try
          {
            node = zwaveGetNodeProtocolInfo(nid);
          } catch (FrameLayerException e)
          {
            logger
                .error("Error getting node protocol info: {}", e.getMessage());
          } catch (ApplicationLayerException e)
          {
            logger
                .error("Error getting node protocol info: {}", e.getMessage());
          }
        }
        if (payload.length - 6 > 0)
        {
          int[] supportedCmdClasses = new int[payload.length - 6];
          for (int i = 0; i < node.getSupportedCmdClasses().length; i++)
          {
            supportedCmdClasses[i] = payload[i + 6];
          }
          node.setSupportedCmdClasses(supportedCmdClasses);
        }
        nodeTable.add(node);
        // AddNodeEventArgs e = new AddNodeEventArgs(node, cmd,
        // NodeStatus.Done);
        // if (AddNodeEvent != null) AddNodeEvent(this, e);
      }

      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.SUC_ID)
      {
        // UpdateEventArgs e = new UpdateEventArgs(payload[0], payload[1]);
        // if (UpdateEvent != null) UpdateEvent(this, e);
      }

      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.NODE_INFO_REQ_FAILED)
      {
        waitForNodeInfoCallbackHandler();
      }

      // NODE_INFO_REQ_DONE only used with Serial API 2.16 and below.
      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.NODE_INFO_REQ_DONE)
      {
        requestNodeInfo(payload, nid);
      }

      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.NODE_INFO_RECEIVED)
      {
        if (requestNodeInfo)
        {
          requestNodeInfo(payload, nid);
        }
      }

      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.ROUTING_PENDING)
      {
      } else
      {
        // UnknownCommandEventArgs e = new UnknownCommandEventArgs(packet);
        // if (UnknownCommandEvent != null) UnknownCommandEvent(this, e);
      }

    }

    else if (cmd == DataFrame.CommandType.CmdZWaveRequestNodeInfo)
    {
    }

    else if (cmd == DataFrame.CommandType.CmdZWaveSetLearnMode)
    {
      int nid = payload[2];
      LearnMode learnMode = LearnMode.getByVal(payload[1]);
      if (learnMode == LearnMode.Failed)
        nodeTable.remove(nid);

      else if (learnMode == LearnMode.Done)
        try
        {
          zwaveEnumerateNodes();
        } catch (FrameLayerException e)
        {
          logger.error("Error enumerating Z-Wave nodes: {}", e.getMessage());
        } catch (ApplicationLayerException e)
        {
          logger.error("Error enumerating Z-Wave nodes: {}", e.getMessage());
        }
      // UpdateEventArgs e = new UpdateEventArgs(payload[1], payload[2]);
      // if (UpdateEvent != null) UpdateEvent(this, e);
    }

    else if (cmd == DataFrame.CommandType.CmdZWaveSetSlaveLearnMode)
    {
      @SuppressWarnings("unused")
      int status = payload[1];
      int orgId = payload[2];
      int newId = payload[3];
      if (newId == 0)
      {
        nodeRemove(orgId);
      } else
      {
        Node node = null;
        try
        {
          node = zwaveGetNodeProtocolInfo(newId, true);
        } catch (FrameLayerException e)
        {
          logger.error("Error getting node protocol info: {}", e.getMessage());
        } catch (ApplicationLayerException e)
        {
          logger.error("Error getting node protocol info: {}", e.getMessage());
        }
        /* We know the node is virtual... */
        node.setVirtual(true);
        if (payload.length - 6 > 0)
        {
          int[] supportedCmdClasses = new int[payload.length - 6];
          for (int i = 0; i < node.getSupportedCmdClasses().length; i++)
          {
            supportedCmdClasses[i] = payload[i + 6];
          }
          node.setSupportedCmdClasses(supportedCmdClasses);
        }
        nodeTable.add(node);
      }
      // SlaveEventArgs e = new SlaveEventArgs(status, orgId, newId);
      // if (SlaveEvent != null) SlaveEvent(this, e);
    }

    else if (cmd == DataFrame.CommandType.CmdZWaveReplaceFailedNode)
      if (payload.length > 1)
      {
        // int st = payload[1]; // Byte 1: funcID, Byte 2: status
        // NodeFailedEventArgs nodeEvent = new NodeFailedEventArgs(st,false);
        // if (NodeFailedEvent != null) NodeFailedEvent(this, nodeEvent);
      } else
      { // Return value
        // int st = payload[0]; // Byte 1: status
        // NodeFailedEventArgs nodeEvent = new NodeFailedEventArgs(st,true);
        // if (NodeFailedEvent != null) NodeFailedEvent(this, nodeEvent);
      }

    // else if (cmd == DataFrame.CommandType.CmdSerialApiTest)
    // / testcmd, state, nodeid, status, runnr.
    // if (TestEvent != null)
    // {
    // if (payload.Length >= 6)
    // {
    // TestEventArgs e = new TestEventArgs(payload[1], payload[2], payload[3],
    // payload[4], payload[5]);
    // TestEvent(this, e);
    // }
    // else
    // {
    // TestEvent(this, null);
    // }
    // }

    // else
    // {
    // boolean found = false;
    // foreach (DataFrame.CommandType _cmd in
    // GetValues(typeof(DataFrame.CommandType)))
    // {
    // / command is not an unknown commando.
    // if (cmd == _cmd)
    // {
    // found = true;
    // break;
    // }
    // }
    // if (!found)
    // {
    // Debug.WriteLine("Unknown CMD: " + cmd + " Data: " + packet);
    // if (UnknownCommandEvent != null)
    // {
    // UnknownCommandEventArgs e = new UnknownCommandEventArgs(packet);
    // UnknownCommandEvent(this, e);
    // }
    // }
    // }
    asyncCallback.dataPacketReceived(cmd, packet);
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#close()
   */
  @Override
  public void destroy()
  {
    logger.entry();
    logger.debug("Destroying application layer");
    sessionLayer.destroy();
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getAllNodes()
   */
  public Node[] getAllNodes()
  {
    return nodeTable.getList();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipRev()
   */
  public int getChipRev()
  {
    logger.info("Getting chip revision");
    return chipRev;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipType()
   */
  public ChipType getChipType()
  {
    logger.info("Getting chip type");
    return ChipType.getByVal(chipType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getControllerNodeId()
   */
  public int getControllerNodeId()
  {
    return this.controllerNodeId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getFrameLayer()
   */
  public FrameLayer getFrameLayer()
  {
    return frameLayer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getLibraryType()
   */
  public Library getLibraryType()
  {
    return libraryType.library;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getLibraryVersion()
   */
  public String getLibraryVersion()
  {
    return libraryType.version;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getMemoryGetId()
   */
  public MemoryGetId getMemoryGetId()
  {
    return memoryGetId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getNode(int)
   */
  public Node getNode(int nodeId)
  {
    logger.entry(nodeId);
    if (!nodeTable.contains(nodeId))
    {
      logger.exit(null);
      return null;
    } else
    {
      Node node = nodeTable.get(nodeId);
      logger.exit(node);
      return node;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getNodeCount()
   */
  public int getNodeCount()
  {
    return nodeTable.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getRoutingTableLine
   * (int, boolean, boolean)
   */
  public NodeMask getRoutingTableLine(int nodeId, boolean removeBadRepeaters,
      boolean removeNonRepeatingDevices) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(nodeId, removeBadRepeaters, removeNonRepeatingDevices);
    NodeMask nodeMask;
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(removeBadRepeaters ? 1 : 0);
    req.addPayload(removeNonRepeatingDevices ? 1 : 0);

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdGetRoutingTableLine, req);

    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_GET_ROUTING_TABLE_LINE");

    nodeMask = new NodeMask(rc.getResponse().getPayload());

    logger.exit(nodeMask);
    return nodeMask;
  }

  /**
   * @return an instance of {@link SerialApiCapabilities}
   */
  public SerialApiCapabilities getSerialApiCapabilities()
  {
    return serialApiCapabilities;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#serialApiVersion()
   */
  public int getSerialApiVersion()
  {
    return serialAPIver;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getSessionLayer()
   */
  public SessionLayer getSessionLayer()
  {
    return sessionLayer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getStatistics()
   */
  public ZWStatistics getStatistics()
  {
    logger.entry();
    ZWStatistics stats = new ZWStatistics();

    //stats.bytesReceived = transport.getReceivedBytes();
    //stats.bytesTransmitted = transport.getTransmittedBytes();

    FrameStatistics fstats = frameLayer.getStatistics();
    stats.receivedAcks = fstats.receivedAcks;
    stats.receivedNaks = fstats.receivedNaks;

    stats.transmittedAcks = fstats.transmittedAcks;
    stats.transmittedNaks = fstats.transmittedNaks;

    stats.droppedFrames = fstats.droppedFrames;
    stats.receivedFrames = fstats.receivedFrames;
    stats.transmittedFrames = fstats.transmittedFrames;
    stats.retransmittedFrames = fstats.retransmittedFrames;

    SessionStatistics sstats = sessionLayer.getStatistics();
    stats.asyncPackets = sstats.asyncPackets;
    stats.duplicatePackets = sstats.duplicatePackets;
    stats.transmittedPackets = sstats.transmittedPackets;
    stats.receivedPackets = sstats.receivedPackets;
    stats.receiveTimeouts = sstats.receiveTimeouts;

    logger.exit(stats);
    return stats;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSupportedSerialCmds
   * ()
   */
  public String getSupportedSerialCmds()
  {
    logger.entry();
    StringBuffer retString = new StringBuffer();
    /*
     * ctrlCapabilities format:0 APPL_VERSION,1 APPL_REVISION,2 MAN_ID1,
     * 3MAN_ID2,4 PRODUCT_TYPE1, 5 PRODUCT_TYPE2, 6 PRODUCT_ID1,7 PRODUCT_ID2, 8
     * FUNCID_SUPPORTED...
     */
    if (serialApiCapabilities.getCapabilityMask() != null)
    {
      int i = 1;
      while (i < serialApiCapabilities.getCapabilityMask().getLength() * 8)
      {
        if (serialApiCapabilities.getCapabilityMask().zwaveNodeMaskNodeIn(i))
        {
          if (retString.length() > 0)
            retString.append(",");
          DataFrame.CommandType ct = DataFrame.CommandType.getByVal(i);
          if (ct != null)
            retString.append(ct.toString());
          else
            retString.append(String.format("%#02x", i));
        }
        i++;
      }
    }

    logger.exit(retString.toString());
    return retString.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isControllerIsRealPrimary
   * ()
   */
  public boolean isControllerIsRealPrimary()
  {
    logger.entry();
    if (ctrlCapabilities != null)
    {
      boolean result = ((ctrlCapabilities[0] & CtrlCapabilities.IS_REAL_PRIMARY
          .get()) != 0);
      logger.exit(result);
      return result;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isControllerIsSis()
   */
  public boolean isControllerIsSis()
  {
    logger.entry();
    if ((isControllerIsSuc()) && (isNodeIdServerPresent()))
    {
      logger.exit(true);
      return true;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isControllerIsSuc()
   */
  public boolean isControllerIsSuc()
  {
    logger.entry();
    if (ctrlCapabilities != null)
    {
      boolean result = ((ctrlCapabilities[0] & CtrlCapabilities.IS_SUC.get()) != 0);
      logger.exit(result);
      return result;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * isControllerOnOtherNetwork()
   */
  public boolean isControllerOnOtherNetwork()
  {
    logger.entry();
    if (ctrlCapabilities != null)
    {
      boolean result = ((ctrlCapabilities[0] & CtrlCapabilities.ON_OTHER_NETWORK
          .get()) != 0);
      logger.exit(result);
      return result;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isNodeIdServerPresent
   * ()
   */
  public boolean isNodeIdServerPresent()
  {
    logger.entry();
    if (ctrlCapabilities != null)
    {
      boolean result = ((ctrlCapabilities[0] & CtrlCapabilities.NODEID_SERVER_PRESENT
          .get()) != 0);
      logger.exit(result);
      return result;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isNodePresent(int)
   */
  public boolean isNodePresent(int id)
  {
    return nodeTable.contains(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isRealTimeSystem()
   */
  public boolean isRealTimeSystem()
  {
    return sessionLayer.isReady();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#open(java.lang.String
   * , java.lang.String)
   */
  /*
   * public void open(String transportLayerLibrary, String connectionString) {
   * // transportLayer = (ITransportLayer)FindInterface(a, "ITransportLayer");
   * frameLayer = new FrameLayerImpl(); sessionLayer = new SessionLayerImpl();
   * sessionLayer.setCallbackHandler(this); sessionLayer.open(frameLayer,
   * transport); }
   */

  /**
   * @return the slaveApi
   */
  public boolean isSlaveApi()
  {
    return slaveApi;
  }

  /**
   * @return the slaveController
   */
  public boolean isSlaveController()
  {
    return secondaryController;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isSupportedSerialCmd
   * (int)
   */
  public boolean isSupportedSerialCmd(int commandId)
  {
    return serialApiCapabilities.getCapabilityMask().zwaveNodeMaskNodeIn(
        commandId);
  }

  // <summary>
  // Remove Node from the nodeTable
  // </summary>
  // <param name="id"></param>
  public void nodeRemove(int id)
  {
    nodeTable.remove(id);
  }

  private boolean otherSUCNodeId(int nodeId, boolean sucState,
      TXOption[] txOptions, int capabilities) throws FrameLayerException
  {
    logger.entry(nodeId, sucState, txOptions, capabilities);
    DataPacket req = addPayloadToSUCNodeId(nodeId, sucState, txOptions,
        capabilities);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveSetSucNodeId, req, 2, true);
    if ((rc == TXStatus.CompleteOk))
    {
      if (((rc.getResponses()[1].getLength() >= 1) && (rc.getResponses()[1]
          .getPayload()[0] == SetSucReturnValue.SucSetSucceeded.get())))
      {
        logger.exit(true);
        return true;
      }
    }
    logger.exit(false);
    return false;
  }

  private void requestNodeInfo(int[] payload, int nid)
  {
    logger.entry(payload, nid);
    logger.debug("Processing node info for node [{}]", nid);
    // FuncID|status|nodeId|len|basic|generic|specific|data[0]|data[1],data[2]..data[len-7]....
    Node node = nodeTable.get(nid);
    node.setGeneric(payload[4]);
    node.setSpecific(payload[5]);
    if (payload.length > 6)
    {
      int[] supportedCmdClasses = new int[payload.length - 6];
      for (int i = 0; i < supportedCmdClasses.length; i++)
      {
        logger.debug("Discovered supported command class for node [{}]: [{}]", nid,
            CommandClass.getByVal(payload[i + 6]));
        supportedCmdClasses[i] = payload[i + 6];
      }
      node.setSupportedCmdClasses(supportedCmdClasses);
    } else
    {
      node.setSupportedCmdClasses(null);
    }
    nodeTable.add(node);
    requestNodeInfo = false;
    waitForNodeInfoCallbackHandler();
    logger.exit();
  }

  public void setCallbackHandler(ApplicationLayerAsyncCallback handler)
  {
    logger.entry(handler);
    this.asyncCallback = handler;
    logger.exit();
  }

  private boolean thisSUCNodeId(int nodeId, boolean sucState,
      TXOption[] txOptions, int capabilities) throws FrameLayerException
  {
    logger.entry(nodeId, sucState, txOptions, capabilities);
    DataPacket req = addPayloadToSUCNodeId(nodeId, sucState, txOptions,
        capabilities);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSetSucNodeId, req);
    DataPacket res = rc.getResponse();
    if ((rc == TXStatus.CompleteOk)
        && ((nodeId == controllerNodeId) || ((res.getLength() >= 1) && (res
            .getPayload()[0] == SetSucReturnValue.SucSetSucceeded.get()))))
    {
      logger.exit(true);
      return true;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  private void waitForNodeInfoCallbackHandler()
  {
    logger.entry();
    if (waitForNodeInfoExecutor != null)
    {
      waitForNodeInfoExecutor.shutdownNow();
    }
    requestNodeInfo = false;
    // RequestNodeInfoEventArgs e = new RequestNodeInfoEventArgs((Node)handler);
    // if (RequestNodeInfoEvent != null) RequestNodeInfoEvent((Node)handler);
    // if (RequestNodeInfoEvent != null) RequestNodeInfoEvent(this, e);
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveAddNodeToNetwork
   * (net.gregrapp.jhouse.interfaces.zwave.Constants.Mode)
   */
  public NodeStatus zwaveAddNodeToNetwork(Mode mode)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(mode);
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveAddNodeToNetwork, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveADD_NODE_TO_NETWORK:"
          + mode.toString());

    NodeStatus status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveAssignReturnRoute
   * (int, int)
   */
  public TXStatus zwaveAssignReturnRoute(int sourceNodeId, int destinationNodeId)
      throws FrameLayerException
  {
    logger.entry(sourceNodeId, destinationNodeId);
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    req.addPayload(destinationNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveAssignReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveAssignSucReturnRoute
   * (int)
   */
  public TXStatus zwaveAssignSucReturnRoute(int destinationNodeId)
      throws FrameLayerException
  {
    logger.entry(destinationNodeId);
    DataPacket req = new DataPacket();
    req.addPayload(destinationNodeId);
    req.addPayload(0);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveAssignSucReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveControllerChange
   * (net.gregrapp.jhouse.interfaces.zwave.Constants.ControllerChangeMode)
   */
  public NodeStatus zwaveControllerChange(ControllerChangeMode mode)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(mode);
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveControllerChange, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveCONTROLLER_CHANGE");
    NodeStatus status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveCreateNewPrimaryCtrl
   * (
   * net.gregrapp.jhouse.interfaces.zwave.Constants.CreateNewPrimaryControllerMode
   * )
   */
  public NodeStatus zwaveCreateNewPrimaryCtrl(
      CreateNewPrimaryControllerMode mode) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(mode);
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveCreateNewPrimary, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveCREATE_NEW_PRIMARY");
    NodeStatus status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveDeleteReturnRoute
   * (int)
   */
  public TXStatus zwaveDeleteReturnRoute(int sourceNodeId)
      throws FrameLayerException
  {
    logger.entry(sourceNodeId);
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveDeleteReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveDeleteSucReturnRoute
   * (int)
   */
  public TXStatus zwaveDeleteSucReturnRoute(int sourceNodeId)
      throws FrameLayerException
  {
    logger.entry(sourceNodeId);
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveDeleteSucReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveEnableSuc(boolean
   * , int)
   */
  public boolean zwaveEnableSuc(boolean enable, int capabilities)
      throws FrameLayerException
  {
    logger.entry(enable, capabilities);
    DataPacket req = new DataPacket();
    req.addPayload(enable ? 1 : 0);
    req.addPayload(capabilities);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveEnableSuc, req);
    if (rc == TXStatus.CompleteOk)
    {
      if (rc.getResponse().getPayload()[0] != 0)
      {
        logger.exit(true);
        return true;
      }
    }

    logger.exit(false);
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#enumerateNodes()
   */
  public Node[] zwaveEnumerateNodes() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    logger.debug("Enumerating ZWave nodes");
    synchronized (this)
    {
      DataPacket req = new DataPacket();

      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdSerialApiGetInitData, req);
      if (rc != TXStatus.CompleteOk)
        throw new ApplicationLayerException("CMD_SERIAL_API_GET_INIT_DATA");

      int[] payload = rc.getResponse().getPayload();
      serialAPIver = payload[0];
      int capab = payload[1];
      int len = payload[2];

      secondaryController = (capab & GET_INIT_DATA_FLAG_SECONDARY_CTRL) > 0;
      slaveApi = (capab & GET_INIT_DATA_FLAG_SLAVE_API) != 0;

      if (payload.length > len)
      {
        chipType = payload[3 + len];
        chipRev = payload[4 + len];
      }

      nodeTable.clear();

      MemoryGetId ret = zwaveMemoryGetId();
      controllerHomeId = ret.getHomeId();
      controllerNodeId = ret.getNodeId();

      NodeMask virtualNodeMask = zwaveGetVirtualNodes();
      int nodeIdx = 0;
      for (int i = 0; i < len; i++)
      {
        int availabilityMask = payload[3 + i];
        for (int bit = 0; bit < 8; bit++)
        {
          nodeIdx++;
          if ((availabilityMask & (1 << bit)) > 0)
          {
            Node node = zwaveGetNodeProtocolInfo(nodeIdx, false);
            node.setVirtual(virtualNodeMask.get(nodeIdx));
            nodeTable.add(node);
          }
        }
      }

      /*
       * for (Node n : nodeTable.getList()) { if (n.isNodeListening()) {
       * zwaveRequestNodeInfo(n.getId()); } }
       * 
       * for (Node n : nodeTable.getList()) { if (n.isNodeListening()) {
       * zwaveNodeManufacturerSpecific(n.getId()); } }
       */
      Node[] nodes = nodeTable.getList();
      logger.exit(nodes);
      return nodes;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveGetControllerCapabilities()
   */
  public ControllerCapabilities zwaveGetControllerCapabilities()
      throws ApplicationLayerException, FrameLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetControllerCapabilities, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException(
          "CMD_ZWaveGET_CONTROLLER_CAPABILITIES");
    int[] payload = rc.getResponse().getPayload();
    ctrlCapabilities = payload;
    secondaryController = ((payload[0] & CtrlCapabilities.IS_SECONDARY.get()) != 0);
    ControllerCapabilities caps = new ControllerCapabilities(payload,
        secondaryController);

    logger.exit(caps);
    return caps;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveGetNodeProtocolInfo
   * (int)
   */
  public Node zwaveGetNodeProtocolInfo(int nodeId) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(nodeId);
    Node nodeInfo = zwaveGetNodeProtocolInfo(nodeId, false);
    logger.exit(nodeInfo);
    return nodeInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveGetNodeProtocolInfo
   * (int, boolean)
   */
  public Node zwaveGetNodeProtocolInfo(int nodeId, boolean checkIfVirtual)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(nodeId, checkIfVirtual);
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetNodeProtocolInfo, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException(String.format(
          "CMD_ZWaveGET_NODE_PROTOCOL_INFO - status: {}", rc.toString()));
    int[] payload = rc.getResponse().getPayload();

    boolean isVirtual = false;
    if (checkIfVirtual)
    {
      isVirtual = zwaveIsVirtualNode(nodeId);
    }
    // capability : payload[0];
    // security : payload[1];
    // reserved : payload[2];
    // type basic : payload[3];
    // type generic : payload[4];
    // type specific : payload[5];
    Node node = new Node(nodeId, payload[0], payload[1], payload[2],
        payload[3], payload[4], payload[5], null, isVirtual);
    logger.exit(node);
    return node;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveGetSucNodeId()
   */
  public int zwaveGetSucNodeId() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetSucNodeId, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveGET_SUC_NODE_ID");
    int nodeId = rc.getResponse().getPayload()[0];
    logger.exit(nodeId);
    return nodeId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveVersion()
   */
  public VersionInfoType zwaveGetVersion() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    logger.debug("Getting version from ZWave interface");
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetVersion, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException(String.format(
          "CmdZWaveGetVersion - TXStatus.%s", rc.toString()));
    // return libraryType;

    libraryType = new VersionInfoType();

    int[] payload = rc.getResponse().getPayload();
    libraryType.version = new String(payload, 6, 6);
    if (libraryType.version.endsWith("\0"))
    {
      libraryType.version = libraryType.version.substring(0,
          libraryType.version.length() - 1);
    }
    libraryType.library = Library.getByVal(payload[12]);

    logger.exit(libraryType);
    return libraryType;
  }

  public NodeMask zwaveGetVirtualNodes() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    NodeMask nodeMask;
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      DataPacket req = new DataPacket();

      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdZWaveGetVirtualNodes, req);
      if (rc != TXStatus.CompleteOk)
        throw new ApplicationLayerException("CMD_ZWaveGET_VIRTUAL_NODES");

      nodeMask = new NodeMask(rc.getResponse().getPayload());
    } else
    {
      nodeMask = new NodeMask();
    }

    logger.exit(nodeMask);
    return nodeMask;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveIsFailedNode
   * (int)
   */
  public boolean zwaveIsFailedNode(int nodeId) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(nodeId);
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveIsFailedNode, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveIS_FAILED_NODE");

    boolean status = rc.getResponse().getPayload()[0] == 0 ? false : true;
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveIsVirtualNode
   * (int)
   */
  public boolean zwaveIsVirtualNode(int nodeId) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(nodeId);
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      DataPacket req = new DataPacket();
      req.addPayload(nodeId);

      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdZWaveIsVirtualNode, req);
      if (rc != TXStatus.CompleteOk)
        throw new ApplicationLayerException("CMD_ZWaveIS_VIRTUAL_NODE");

      boolean virtual = (rc.getResponse().getPayload()[0] != 0);
      logger.exit(virtual);
      return virtual;
    }

    logger.exit(false);
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveLockRoutes(int)
   */
  public void zwaveLockRoutes(int nodeId) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(nodeId);
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdLockRouteResponse, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_LOCK_ROUTE_RESPONSE");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryGetBuffer
   * (long, int)
   */
  public int[] zwaveMemoryGetBuffer(long offset, int len)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(offset, len);
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));
    req.addPayload(len);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryGetBuffer, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_GET_BUFFER");

    int[] buffer = rc.getResponse().getPayload();
    logger.exit(buffer);
    return buffer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryGetByte
   * (long)
   */
  public int zwaveMemoryGetByte(long offset) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(offset);
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryGetByte, req);

    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_GET_BYTE");

    int data = rc.getResponse().getPayload()[0];
    logger.exit(data);
    return data;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryGetId()
   */
  public MemoryGetId zwaveMemoryGetId() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryGetId, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_GET_ID");
    int[] payload = rc.getResponse().getPayload();
    int homeId = payload[0] << 24 | payload[1] << 16 | payload[2] << 8
        | payload[3];
    int controllerNode = payload[4];

    this.memoryGetId = new MemoryGetId(homeId, controllerNode, rc);
    logger.exit(this.memoryGetId);
    return this.memoryGetId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryPutBuffer
   * (long, int[], long)
   */
  public void zwaveMemoryPutBuffer(long offset, int[] buffer, long length)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(offset, buffer, length);
    if (length > 66)
      length = 66;
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));
    req.addPayload((int) (length >> 8));
    req.addPayload((int) (length & 0xFF));
    req.addPayload(buffer);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdMemoryPutBuffer, req, 2, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_PUT_BUFFER");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryPutByte
   * (long, int)
   */
  public TXStatus zwaveMemoryPutByte(long offset, int value)
      throws FrameLayerException
  {
    logger.entry(offset, value);
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));
    req.addPayload(value);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryPutByte, req);
    logger.exit(rc);
    return rc;
  }

  public void zwaveNodeManufacturerSpecific(int nodeId)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(nodeId);
    logger
        .debug("Requesting manufacturer specific report from node {}", nodeId);
    int[] data = new int[] {
        CommandClass.COMMAND_CLASS_MANUFACTURER_SPECIFIC.get(),
        CommandManufacturerSpecific.MANUFACTURER_SPECIFIC_GET.get() };
    zwaveSendData(nodeId, data, new TXOption[] {
        TXOption.TransmitOptionAcknowledge, TXOption.TransmitOptionAutoRoute });
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRediscoveryNeeded
   * (int)
   */
  public ZWaveRediscoveryNeededReturnValue zwaveRediscoveryNeeded(int nodeId)
      throws FrameLayerException
  {
    logger.entry(nodeId);
    // DataPacket[] res = new DataPacket[3]; // We are receiving 2 responses...
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithVariableResponses(
        DataFrame.CommandType.CmdZWaveRediscoveryNeeded, req, 3, new int[] { 1,
            4 }, true, 60000);
    ZWaveRediscoveryNeededReturnValue st = ZWaveRediscoveryNeededReturnValue
        .getByVal(rc.getResponses()[1].getPayload()[0]);
    if (rc == TXStatus.CompleteOk)
    {
      if (st == ZWaveRediscoveryNeededReturnValue.LostAccepted)
      {
        ZWaveRediscoveryNeededReturnValue val = ZWaveRediscoveryNeededReturnValue
            .getByVal(rc.getResponses()[2].getPayload()[0]);
        logger.exit(val);
        return val;
      } else
      {
        logger.exit(st);
        return st;
      }

    } else
    {
      logger.exit(ZWaveRediscoveryNeededReturnValue.LostFailed);
      return ZWaveRediscoveryNeededReturnValue.LostFailed;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRemoveFailedNodeId
   * (int)
   */
  public DataPacket[] zwaveRemoveFailedNodeId(int nodeId)
      throws FrameLayerException
  {
    logger.entry(nodeId);
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithVariableReturnsAndResponses(
        DataFrame.CommandType.CmdZWaveRemoveFailedNodeId, req, 2, new int[] {
            1, 2, 4, 5, 8, 16 }, true, TIMEOUT);
    if (NodeFailedReturnValue.getByVal(rc.getResponses()[0].getPayload()[0]) == NodeFailedReturnValue.FailedNodeRemoveStarted)
    {
      try
      {
        if (NodeFailedStatus.getByVal(rc.getResponses()[1].getPayload()[0]) == NodeFailedStatus.NodeRemoved)
        {
          if (nodeTable.contains(nodeId))
          {
            nodeTable.remove(nodeId);
          }
        }
      } catch (NullPointerException e)
      {
        logger.exit(rc.getResponses());
        return rc.getResponses();
      }
    }
    logger.exit(rc.getResponses());
    return rc.getResponses();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveRemoveNodeFromNetwork
   * (net.gregrapp.jhouse.interfaces.zwave.Constants.Mode)
   */
  public NodeStatus zwaveRemoveNodeFromNetwork(Mode mode)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(mode);
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    NodeStatus status = NodeStatus.Done;
    if (mode == Mode.NodeStop)
    {
      boolean sent = sessionLayer.requestWithNoResponse(
          DataFrame.CommandType.CmdZWaveRemoveNodeFromNetwork, req);
      if (!sent)
      {
        status = NodeStatus.Failed;
      }
    } else
    {
      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdZWaveRemoveNodeFromNetwork, req, true);
      if (rc != TXStatus.CompleteOk)
      {
        throw new ApplicationLayerException("CMD_ZWaveREMOVE_NODE_FROM_NETWORK");
      }
      status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
    }
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveReplaceFailedNode
   * (int)
   */
  public boolean zwaveReplaceFailedNode(int nodeId) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(nodeId);
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveReplaceFailedNode, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveReplaceFailedNode");
    logger.exit(rc);
    return rc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveReplicationReceiveComplete()
   */
  public void zwaveReplicationReceiveComplete() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveReplicationCommandComplete, req);
    if (!rc)
      throw new ApplicationLayerException(
          "CMD_ZWaveREPLICATION_COMMAND_COMPLETE");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveReplicationSend
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveReplicationSend(int nodeId, int[] data,
      TXOption[] txOptions) throws FrameLayerException
  {
    logger.entry(nodeId, data, txOptions);
    if (data == null)
    {
      throw new NullPointerException("data");
    }

    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptInt);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveReplicationSendData, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRequestNetworkUpdate
   * ()
   */
  public TXStatus zwaveRequestNetworkUpdate() throws FrameLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveRequestNetworkUpdate, req, 2, true,
        TIMEOUT);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRequestNodeInfo
   * (int)
   */
  public TXStatus zwaveRequestNodeInfo(int nodeId) throws FrameLayerException
  {
    logger.entry();
    logger.debug("Requesting node info from node {}", nodeId);

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveRequestNodeInfo, req, false);
    if (rc == TXStatus.CompleteOk)
    {
      requestNodeInfo = true;
      waitForNodeInfoExecutor = Executors.newSingleThreadScheduledExecutor();
      waitForNodeInfoExecutor.schedule(new Runnable()
      {
        public void run()
        {
          waitForNodeInfoCallbackHandler();
        }
      }, TIMEOUT, TimeUnit.MILLISECONDS);
    }
    logger.exit(rc);
    return rc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveRequestNodeNeighborUpdate(int)
   */
  public RequestNeighbor zwaveRequestNodeNeighborUpdate(int nodeId)
      throws FrameLayerException
  {
    logger.entry(nodeId);
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveRequestNodeNeighborUpdate, req, 2, true);

    if (rc == TXStatus.CompleteOk)
    {
      RequestNeighbor neighbor = RequestNeighbor.getByVal(rc.getResponses()[1]
          .getPayload()[0]);
      logger.exit(neighbor);
      return neighbor;
    } else
    {
      logger.exit(RequestNeighbor.UpdateFailed);
      return RequestNeighbor.UpdateFailed;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRFPowerLevelSet
   * (int)
   */
  public int zwaveRFPowerLevelSet(int powerLevel) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(powerLevel);
    DataPacket req = new DataPacket();
    req.addPayload(powerLevel);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveRFPowerLevelSet, req);
    if (rc != TXStatus.CompleteOk)
    {
      throw new ApplicationLayerException("CMD_ZWaveRF_POWER_LEVEL_SET");
    }

    int response = rc.getResponse().getPayload()[0];
    logger.exit(response);
    return response;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendData(int,
   * int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveSendData(int nodeId, int[] data, TXOption[] txOptions)
      throws FrameLayerException
  {
    logger.entry(nodeId, data, txOptions);
    if (data == null)
    {
      throw new NullPointerException("data");
    }

    TXStatus status = zwaveSendData(nodeId, data, txOptions, TIMEOUT);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendData(int,
   * int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[], int)
   */
  public TXStatus zwaveSendData(int nodeId, int[] data, TXOption[] txOptions,
      int timeout) throws FrameLayerException
  {
    logger.entry(nodeId, data, txOptions, timeout);
    if (data == null)
    {
      throw new NullPointerException("data");
    }

    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptInt);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveSendData, req, 2, true, timeout);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataAbort()
   */
  public void zwaveSendDataAbort() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSendDataAbort, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveSEND_DATA_ABORT");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMeta
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption[] txOptions)
      throws FrameLayerException
  {
    logger.entry(nodeId, data, txOptions);
    TXStatus status = zwaveSendDataMeta(nodeId, data, txOptions, TIMEOUT);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMeta
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendDataMeta(int nodeId, int[] data,
      TXOption[] txOptions, int timeout) throws FrameLayerException
  {
    logger.entry(nodeId, data, txOptions, timeout);
    if (data == null)
    {
      throw new NullPointerException("data");
    }

    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptInt);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveSendDataMeta, req, 2, true, timeout);
    if (rc != TXStatus.CompleteOk)
    {
      logger.exit(rc);
      return rc;
    }

    TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    logger.exit(status);
    return status;
  }

  public TXStatus zwaveSendDataMulti(List<Integer> nodeIdList, int[] data,
      TXOption[] txOptions) throws FrameLayerException
  {
    logger.entry(nodeIdList, data, txOptions);
    if (nodeIdList == null || data == null)
    {
      throw new NullPointerException("nodeIdList");
    }

    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeIdList.size());
    req.addPayload(CollectionUtils.toIntArray(nodeIdList));
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptInt);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveSendDataMulti, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendNodeInformation
   * (int, net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveSendNodeInformation(int destination, TXOption[] txOptions)
      throws FrameLayerException
  {
    logger.entry(destination, txOptions);
    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(destination);
    req.addPayload(txOptInt);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSendNodeInformation, req, true);
    if (rc != TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponse().getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendSlaveData
   * (int, int, int[],
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveSendSlaveData(int sourceId, int destinationId,
      int[] data, TXOption[] txOptions) throws FrameLayerException
  {
    logger.entry();
    TXStatus status = zwaveSendSlaveData(sourceId, destinationId, data,
        txOptions, TIMEOUT);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendSlaveData
   * (int, int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption,
   * int)
   */
  public TXStatus zwaveSendSlaveData(int sourceId, int destinationId,
      int[] data, TXOption[] txOptions, int timeout) throws FrameLayerException
  {
    logger.entry(sourceId, destinationId, data, txOptions, timeout);
    if (data == null)
    {
      throw new NullPointerException("data");
    }

    TXStatus rc;
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      int txOptInt = 0;
      for (TXOption txOpt : txOptions)
        txOptInt |= txOpt.get();

      DataPacket req = new DataPacket();
      req.addPayload(sourceId);
      req.addPayload(destinationId);
      req.addPayload(data.length);
      req.addPayload(data);
      req.addPayload(txOptInt);
      rc = sessionLayer.requestWithMultipleResponses(
          DataFrame.CommandType.CmdZWaveSendSlaveData, req, 2, true, timeout);
      if (rc == TXStatus.CompleteOk)
      {
        TXStatus status = TXStatus
            .getByVal(rc.getResponses()[1].getPayload()[0]);
        logger.exit(status);
        return status;
      }
    } else
    {
      // rc = new TXStatus();
      rc = null;
    }
    logger.exit(rc);
    return rc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSendSlaveNodeInformation(int, int,
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendSlaveNodeInformation(int sourceId,
      int destinationId, TXOption[] txOptions) throws FrameLayerException
  {
    logger.entry(sourceId, destinationId, txOptions);
    TXStatus status = zwaveSendSlaveNodeInformation(sourceId, destinationId,
        txOptions, 10000);
    logger.exit(status);
    return status;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSendSlaveNodeInformation(int, int,
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendSlaveNodeInformation(int sourceId,
      int destinationId, TXOption[] txOptions, int timeout)
      throws FrameLayerException
  {
    logger.entry(sourceId, destinationId, txOptions, timeout);
    TXStatus rc;
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      int txOptInt = 0;
      for (TXOption txOpt : txOptions)
        txOptInt |= txOpt.get();

      DataPacket req = new DataPacket();
      req.addPayload(sourceId);
      req.addPayload(destinationId);
      req.addPayload(txOptInt);

      rc = sessionLayer.requestWithMultipleResponses(
          DataFrame.CommandType.CmdZWaveSendSlaveNodeInfo, req, 2, true,
          timeout);
      if (rc == TXStatus.CompleteOk)
      {
        TXStatus status = TXStatus
            .getByVal(rc.getResponses()[1].getPayload()[0]);
        logger.exit(status);
        return status;
      }
    } else
    {
      // rc = new TXStatus();
      rc = null;
    }
    logger.exit(rc);
    return rc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendSucId(int,
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveSendSucId(int nodeId, TXOption[] txOptions)
      throws FrameLayerException
  {
    logger.entry(nodeId, txOptions);
    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(txOptInt);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveSendSucId, req, 2, true, DEFAULT_TIMEOUT);

    if ((rc == TXStatus.CompleteOk) && (rc.getResponses()[1].getLength() == 1))
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSerialApiGetCapabilities()
   */
  public SerialApiCapabilities zwaveSerialApiGetCapabilities()
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdSerialApiGetCapabilities, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_SERIAL_API_GET_CAPABILITIES");
    int[] payload = rc.getResponse().getPayload();
    // ctrlCapabilities = payload;
    String version = String.format("%d.%d", payload[0], payload[1]);
    int manufacturer = (payload[2] << 8) | payload[3];
    int productType = (payload[4] << 8) | payload[5];
    int productId = (payload[6] << 8) | payload[7];

    serialApiCapabilities = new SerialApiCapabilities(version, manufacturer,
        productType, productId);

    if (payload.length > 8)
    {
      // serialCapabilityMask = new NodeBitmask();
      int[] temp = new int[payload.length - 8];
      for (int n = 0; n < temp.length; n++)
      {
        temp[n] = payload[n + 8];
      }
      serialApiCapabilities.getCapabilityMask().store(temp);
    }

    logger.exit(serialApiCapabilities);
    return serialApiCapabilities;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSerialApiSetTimeout
   * (int, int)
   */
  public int[] zwaveSerialApiSetTimeout(int acknowledgeTimeout, int timeout)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(acknowledgeTimeout, timeout);
    DataPacket req = new DataPacket();

    req.addPayload(acknowledgeTimeout);
    req.addPayload(timeout);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdSerialApiSetTimeouts, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_SERIAL_API_SET_TIMEOUTS");
    // Get the payload as it contains the old ackTimeout and byteTimeout which
    // previously was present in the ZW module
    int[] payload = rc.getResponse().getPayload();
    logger.exit(payload);
    return payload;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSerialApiSoftReset
   * ()
   */
  public void zwaveSerialApiSoftReset() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdSerialApiSoftReset, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_SERIAL_API_SOFT_RESET");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetDefault()
   */
  public void zwaveSetDefault() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSetDefault, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveSET_DEFAULT");
    zwaveEnumerateNodes();
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetLearnMode
   * (boolean)
   */
  public boolean zwaveSetLearnMode(boolean learnMode)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(learnMode);
    DataPacket req = new DataPacket();
    req.addPayload(learnMode ? 1 : 0);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveSetLearnMode, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveSET_LEARN_MODE");
    logger.exit(rc);
    return rc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetNodeInformation
   * (int, int, int, int[])
   */
  public void zwaveSetNodeInformation(int listening, int generic, int specific,
      int[] nodeParameter) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(listening, generic, specific, nodeParameter);
    if (nodeParameter == null)
    {
      throw new NullPointerException("nodeParm");
    }
    DataPacket req = new DataPacket();
    req.addPayload(listening);
    req.addPayload(generic);
    req.addPayload(specific);
    req.addPayload(nodeParameter.length);
    req.addPayload(nodeParameter);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdSerialApiApplNodeInformation, req);
    if (!rc)
      throw new ApplicationLayerException(
          "CMD_SERIAL_API_APPL_NODE_INFORMATION");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetPromiscuousMode
   * (boolean)
   */
  public boolean zwaveSetPromiscuousMode(boolean enable)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(enable);
    if (libraryType.library == Library.InstallerLib)
    {
      DataPacket req = new DataPacket();
      if (enable)
      {
        req.addPayload(0xff);
      } else
      {
        req.addPayload(0x00);
      }
      boolean rc = sessionLayer.requestWithNoResponse(
          DataFrame.CommandType.CmdZWaveSetPromiscuousMode, req);
      if (!rc)
        throw new ApplicationLayerException("CMD_ZWAVE_SET_PROMISCUOUS_MODE");
      logger.exit(true);
      return true;
    } else
    {
      logger.exit(false);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetRFReceiveMode
   * (int)
   */
  public void zwaveSetRFReceiveMode(int mode) throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry(mode);
    DataPacket req = new DataPacket();
    req.addPayload(mode);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveSetRFReceiveMode, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveSET_RF_RECEIVE_MODE");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSelfAsSuc
   * (boolean, int)
   */
  public int zwaveSetSelfAsSuc(boolean sucState, int capabilities)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(sucState, capabilities);
    int suc = 0;
    if (secondaryController)
      return -1;

    MemoryGetId ret = zwaveMemoryGetId();
    controllerHomeId = ret.getHomeId();
    controllerNodeId = ret.getNodeId();

    if (controllerNodeId == 0)
    {
      logger.exit(-1);
      return -1;
    }

    if (zwaveSetSucNodeId(controllerNodeId, sucState,
        new TXOption[] { TXOption.TransmitOptionNone }, capabilities))
    {
      suc = controllerNodeId;
      logger.exit(suc);
      return suc;
    }

    logger.exit(-1);
    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSlaveLearnMode
   * (int, int)
   */
  public boolean zwaveSetSlaveLearnMode(int node, int mode)
      throws FrameLayerException
  {
    logger.entry(node, mode);
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      DataPacket req = new DataPacket();
      req.addPayload(node);
      req.addPayload(mode);

      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdZWaveSetSlaveLearnMode, req, true);
      if (rc == TXStatus.CompleteOk)
      {
        boolean response = (rc.getResponse().getPayload()[0] != 0);
        logger.exit(response);
        return response;
      }
    }

    logger.exit(false);
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSetSlaveNodeInformation(int, int, int, int, int[])
   */
  public void zwaveSetSlaveNodeInformation(int nodeId, int listening,
      int generic, int specific, int[] nodeParameter)
      throws FrameLayerException, ApplicationLayerException
  {
    logger.entry(nodeId, listening, generic, specific, nodeParameter);
    if (nodeParameter == null)
    {
      throw new NullPointerException("nodeParameter");
    }

    if (libraryType.library == Library.ControllerBridgeLib)
    {
      DataPacket req = new DataPacket();
      req.addPayload(nodeId);
      req.addPayload(listening);
      req.addPayload(generic);
      req.addPayload(specific);
      req.addPayload(nodeParameter.length);
      req.addPayload(nodeParameter);
      boolean rc = sessionLayer.requestWithNoResponse(
          DataFrame.CommandType.CmdSerialApiSlaveNodeInfo, req);
      if (!rc)
        throw new ApplicationLayerException(
            "CMD_SERIAL_API_APPL_SLAVE_NODE_INFO");
      logger.exit();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSucNodeId
   * (int, boolean, net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[],
   * int)
   */
  public boolean zwaveSetSucNodeId(int nodeId, boolean sucState,
      TXOption[] txOptions, int capabilities) throws FrameLayerException
  {
    logger.entry(nodeId, sucState, txOptions, capabilities);
    if (nodeId == controllerNodeId)
    {
      boolean value = thisSUCNodeId(nodeId, sucState, txOptions, capabilities);
      logger.exit(value);
      return value;
    } else
    {
      boolean value = otherSUCNodeId(nodeId, sucState, txOptions, capabilities);
      logger.exit(value);
      return value;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveStopLearnMode()
   */
  public void zwaveStopLearnMode() throws FrameLayerException,
      ApplicationLayerException
  {
    logger.entry();
    DataPacket req = new DataPacket();
    req.addPayload(Mode.NodeStop.get());
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveAddNodeToNetwork, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveADD_NODE_TO_NETWORK");
    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveStoreHomeId(int,
   * int)
   */
  public boolean zwaveStoreHomeId(int homeId, int nodeId)
      throws FrameLayerException
  {
    logger.entry(homeId, nodeId);
    DataPacket req = new DataPacket();
    boolean value = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdStoreHomeId, req);
    logger.exit(value);
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveStoreNodeInfo
   * (int, net.gregrapp.jhouse.interfaces.zwave.Node)
   */
  public TXStatus zwaveStoreNodeInfo(int nodeId, Node nodeInfo)
      throws FrameLayerException
  {
    logger.entry(nodeId, nodeInfo);
    if (nodeInfo == null)
    {
      throw new NullPointerException("nodeInfo");
    }
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(nodeInfo.getCapability());
    req.addPayload(nodeInfo.getSecurity());
    req.addPayload(nodeInfo.getReserved());
    req.addPayload(nodeInfo.getBasic());
    req.addPayload(nodeInfo.getGeneric());
    req.addPayload(nodeInfo.getSpecific());

    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdStoreNodeInfo, req, 2);

    if (rc == TXStatus.CompleteOk)
    {
      TXStatus status = TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      logger.exit(status);
      return status;
    } else
    {
      logger.exit(rc);
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveTest(int,
   * int, int, int, net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption,
   * int, int[])
   */
  public int zwaveTest(int testCmd, int testDelay, int testPayloadLength,
      int testCount, TXOption[] testTXOptions, int maxLength, int[] testNodeMask)
      throws FrameLayerException
  {
    logger.entry(testCmd, testDelay, testPayloadLength, testCount,
        testTXOptions, maxLength, testNodeMask);
    if (testNodeMask == null)
    {
      throw new NullPointerException("testNodeMask");
    }
    DataPacket req = new DataPacket();
    req.addPayload(testCmd);
    if (testCmd > 0)
    {
      int testTXOptInt = 0;
      for (TXOption txOpt : testTXOptions)
        testTXOptInt |= txOpt.get();

      req.addPayload((testDelay >> 8) & 0xff);
      req.addPayload(testDelay & 0xff);
      req.addPayload(testPayloadLength);
      req.addPayload((testCount >> 8) & 0xff);
      req.addPayload((testCount & 0xff));
      req.addPayload(testTXOptInt);
      req.addPayload(testNodeMask.length);
      req.addPayload(testNodeMask);
    }
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdSerialApiTest, req, true);
    if (rc != TXStatus.CompleteOk)
    {
      logger.exit(TXStatus.CompleteFail.get());
      return TXStatus.CompleteFail.get();
    } else
    {
      int response = rc.getResponse().getPayload()[0];
      logger.exit(response);
      return response;
    }
  }

}
