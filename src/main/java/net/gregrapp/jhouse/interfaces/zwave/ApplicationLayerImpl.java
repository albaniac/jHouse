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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.interfaces.zwave.Constants.ChipType;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandBasic;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CommandClass;
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
import net.gregrapp.jhouse.transports.Transport;
import net.gregrapp.jhouse.utils.ArrayUtils;
import net.gregrapp.jhouse.utils.CollectionUtils;

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
  
  public class ControllerCapabilities
  {
    private int[] capabilities;

    private boolean slave;

    public ControllerCapabilities(int[] capabilities, boolean slave)
    {
      this.capabilities = capabilities;
      this.slave = slave;
    }

    public int[] getCapabilities()
    {
      return capabilities;
    }

    public boolean isSlave()
    {
      return slave;
    }
  }

  private enum CtrlCapabilities
  {
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is the original owner of the current Z-Wave network HomeID
    // </summary>
    IS_REAL_PRIMARY(0x08),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is secondary on current Z-Wave network
    // </summary>
    IS_SECONDARY(0x01),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is the SUC in current Z-WAve network
    // </summary>
    IS_SUC(0x10),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is a member of a Z-Wave network with a NodeID Server present
    // </summary>
    NODEID_SERVER_PRESENT(0x04),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // </summary>
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

  // <summary>
  // Nodemask handling class
  // </summary>
  public class NodeBitmask
  {
    private int[] mask;

    // private int maxNodeID;

    // <summary>
    // Initializes a new instance of the <see cref="T:NodeBitmask"/> class.
    // </summary>
    public NodeBitmask()
    {
      mask = null;
    }

    // <summary>
    // Creates a nodemask object
    // </summary>
    // <param name="length">length of nodemask (number of ids/8)</param>
    public NodeBitmask(int length)
    {
      if (length > 0)
      {
        mask = new int[length];
        // maxNodeID = length*8;
      }
    }

    // <summary>
    // Gets the nodemask as an bit array
    // </summary>
    // <returns></returns>
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

    // <summary>
    // Gets the length.
    // </summary>
    // <value>The length.</value>
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

    // <summary>
    // Stores an entire array
    // </summary>
    // <param name="array">The array.</param>
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

    // <summary>
    // Returns true if any bits in mask
    // </summary>
    // <returns>true if bits set, false if not</returns>
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

    // <summary>
    // Clears the nodemask
    // </summary>
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

    // <summary>
    // Clears the bit corresponding to then nodeID supplied
    // </summary>
    // <param name="nodeId"></param>
    public void zwaveNodeMaskClearBit(int nodeId)
    {
      logger.debug("Clearing nodemask bit for node {}", nodeId);
      if (nodeId < 1)
      {
        return;
      }
      nodeId--;
      mask[nodeId >> 3] &= ~(0x1 << (nodeId & 7));
    }

    // <summary>
    // Checks if the bit corresponding to the node ID is set
    // </summary>
    // <param name="nodeId"></param>
    // <returns>true if bit is set</returns>
    public boolean zwaveNodeMaskNodeIn(int nodeId)
    {
      logger.debug("Getting nodemask bit for node {}", nodeId);

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

    // <summary>
    // Set the bit corresponding to the nodeID supplied
    // </summary>
    // <param name="nodeId"></param>
    public void zwaveNodeMaskSetBit(int nodeId)
    {
      logger.debug("Setting nodemask bit for node {}", nodeId);

      if (nodeId < 1)
      {
        return;
      }
      nodeId--;
      mask[nodeId >> 3] |= (0x1 << (nodeId & 7));
    }

  }// Class BitMask
   // <summary>
   // NodeMask
   // </summary>

  public class NodeMask
  {
    private int[] nodeMask;

    // <summary>
    // NodeMask
    // </summary>
    public NodeMask()
    {
      nodeMask = new int[29];
    }

    // <summary>
    // NodeMask(byte[] _nodeMask)
    // </summary>
    // <param name="_nodeMask"></param>
    public NodeMask(int[] _nodeMask)
    {
      if (_nodeMask == null)
      {
        throw new NullPointerException("_nodeMask");
      }
      if (nodeMask == null)
      {
        nodeMask = new int[29];
      }
      if (_nodeMask.length == 29)
      {
        for (int i = 0; i < 29; i++)
        {
          nodeMask[i] = _nodeMask[i];
        }
      }
    }

    // <summary>
    // clear
    // </summary>
    public void clear()
    {
      for (int i = 0; i < 29; i++)
      {
        nodeMask[i] = 0;
      }
    }

    // <summary>
    //
    // </summary>
    // <param name="nodeId"></param>
    // <returns></returns>
    public boolean get(int nodeId)
    {
      if ((nodeId > 0) && (nodeId <= 232))
      {
        return ((nodeMask[(nodeId - 1) >> 3] & (1 << ((nodeId - 1) & 0x07))) != 0);
      }
      return false;
    }

    // <summary>
    //
    // </summary>
    // <param name="nodeId"></param>
    // <param name="value"></param>
    // <returns></returns>
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

  private static final int DEFAULT_TIMEOUT = 10000; // How long in ms to wait

  private static final int GET_INIT_DATA_FLAG_IS_SUC = 0x08;

  private static final int GET_INIT_DATA_FLAG_SECONDARY_CTRL = 0x04;

  private static final int GET_INIT_DATA_FLAG_SLAVE_API = 0x01;

  private static final int GET_INIT_DATA_FLAG_TIMER_SUPPORT = 0x02;

  private static final Logger logger = LoggerFactory
      .getLogger(ApplicationLayerImpl.class);

  // for an response
  private static final int TIMEOUT = 180000; // wait 3 minuttes before timing

  private static final int ZWavePROTECT_TIME = 30000;

  private boolean _addNode;

  private boolean _controllerChange;

  private int _controllerHomeId;

  private int _controllerNodeId;

  private boolean _newPrimary;

  private boolean _removeNode;

  private boolean _requestNodeInfo = false;

  // out
  private Node addedNode;

  private ApplicationLayerAsyncCallback asyncCallback;

  int chipRev = 0; // if chipType still is undefined after the

  int chipType = 0; // Initially we do not know which Chip is used and

  // Controller capabilities
  private int[] ctrlCapabilities = null;

  private boolean disposed;

  private FrameLayer frameLayer;

  private VersionInfoType libraryType;

  private Node[] nodeList;

  private NodeTable nodeTable = new NodeTable(100);

  private Node removedNode;

  int serialAPIver = 0;

  // <summary>
  // Contains a mask of which SerialAPI commands supported by connected module
  // </summary>
  NodeBitmask serialCapabilityMask = new NodeBitmask();

  private SessionLayer sessionLayer;

  private boolean slaveApi;

  private boolean slaveController;

  private Transport transport;

  private ScheduledExecutorService waitForNodeInfoExecutor;
  
  public ApplicationLayerImpl(SessionLayer sessionLayer)
  {
    this.sessionLayer = sessionLayer;
    sessionLayer.setCallbackHandler(this);
  }

  private DataPacket addPayloadToSUCNodeId(int nodeId, boolean sucState,
      TXOption[] txOptions, int capabilities)
  {
    int txOptInt = 0;
    for (TXOption txOpt : txOptions)
      txOptInt |= txOpt.get();

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(sucState ? 1 : 0);
    req.addPayload(txOptInt);
    req.addPayload(capabilities);
    return req;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipRev()
   */
  public int chipRev()
  {
    logger.info("Getting chip revision");

    return chipRev;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipType()
   */
  public ChipType chipType()
  {
    logger.info("Getting chip type");

    return ChipType.getByVal(chipType);
  }

  public boolean clockCompare(Time time) throws FrameLayerException,
      ApplicationLayerException
  {
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
    return payload[0] == 0 ? false : true;
  }

  public Time clockGet() throws FrameLayerException, ApplicationLayerException
  {
    logger.info("Getting interface clock time");

    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdClockGet, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_CLOCK_GET");
    int[] payload = rc.getResponse().getPayload();
    return new Time(payload[0], payload[1], payload[2]);
  }

  public void clockSet(Time time) throws FrameLayerException,
      ApplicationLayerException
  {
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
    logger.debug("Data packet received for command [{}]", cmd);
    
    
    if (packet == null)
    {
      logger.warn("Null data packet received");
      throw new NullPointerException("packet");
    }

    int[] payload = packet.getPayload();
    /*if (cmd == DataFrame.CommandType.CmdApplicationCommandHandler)
    {
      if (payload[3] == CommandClass.COMMAND_CLASS_BASIC.get())
      {
        logger.info("COMMAND_CLASS_BASIC");
        if (payload[4] == CommandBasic.BASIC_REPORT.get())
        {
          logger.info("Received BASIC_REPORT from node {} with a value of {}", payload[1], payload[5]);
        }
      }
    } else*/ if (cmd == DataFrame.CommandType.CmdApplicationSlaveCommandHandler)
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
              for (byte i = 0; i < addedNode.getSupportedCmdClasses().length; i++)
              {
                supportedCmdClasses[i] = payload[i + 7];
              }
              logger.debug("Supported command classes: {}", ArrayUtils.toHexStringArray(supportedCmdClasses));
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
          //Node node = nodeTable.get(nid);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (ApplicationLayerException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else
        {
          try
          {
            node = zwaveGetNodeProtocolInfo(nid);
          } catch (FrameLayerException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (ApplicationLayerException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        if (_requestNodeInfo)
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
          enumerateNodes();
        } catch (FrameLayerException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ApplicationLayerException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      // UpdateEventArgs e = new UpdateEventArgs(payload[1], payload[2]);
      // if (UpdateEvent != null) UpdateEvent(this, e);

    }

    else if (cmd == DataFrame.CommandType.CmdZWaveSetSlaveLearnMode)
    {
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
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ApplicationLayerException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
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
        int st = payload[1]; // Byte 1: funcID, Byte 2: status
        // NodeFailedEventArgs nodeEvent = new NodeFailedEventArgs(st,false);
        // if (NodeFailedEvent != null) NodeFailedEvent(this, nodeEvent);
      } else
      { // Return value
        int st = payload[0]; // Byte 1: status
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#enumerateNodes()
   */
  public Node[] enumerateNodes() throws FrameLayerException,
      ApplicationLayerException
  {
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

      slaveController = (capab & GET_INIT_DATA_FLAG_SECONDARY_CTRL) > 0;
      slaveApi = (capab & GET_INIT_DATA_FLAG_SLAVE_API) != 0;

      if (payload.length > len)
      {
        chipType = payload[3 + len];
        chipRev = payload[4 + len];
      }

      nodeTable.clear();

      MemoryGetId ret = zwaveMemoryGetId();
      _controllerHomeId = ret.getHomeId();
      _controllerNodeId = ret.getNodeId();

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
      return nodeTable.getList();
    }
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
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getNode(int)
   */
  public Node getNode(int nodeId)
  {
    if (!nodeTable.contains(nodeId))
      return null;
    else
      return nodeTable.get(nodeId);
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
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getNodeList()
   */
  public Node[] getNodeList()
  {
    return nodeTable.getList();
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
    ZWStatistics stats = new ZWStatistics();

    stats.bytesReceived = transport.getReceivedBytes();
    stats.bytesTransmitted = transport.getTransmittedBytes();

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

    return stats;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getTransport()
   */
  public Transport getTransport()
  {
    return transport;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveVersion()
   */
  public VersionInfoType getZwaveVersion() throws FrameLayerException
  {
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetVersion, req);
    if (rc != TXStatus.CompleteOk)
      return libraryType;// throw new
                         // ApplicationLayerException("CmdZWaveGetVersion");

    libraryType = new VersionInfoType();

    int[] payload = rc.getResponse().getPayload();
    libraryType.version = new String(payload, 6, 6);
    if (libraryType.version.endsWith("\0"))
    {
      libraryType.version = libraryType.version.substring(0,
          libraryType.version.length() - 1);
    }
    libraryType.library = Library.getByVal(payload[12]);

    return libraryType;
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
    if (ctrlCapabilities != null)
    {
      return ((ctrlCapabilities[0] & CtrlCapabilities.IS_REAL_PRIMARY.get()) != 0);
    } else
    {
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
    if ((isControllerIsSuc()) && (isNodeIdServerPresent()))
    {
      return true;
    } else
    {
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
    if (ctrlCapabilities != null)
    {
      return ((ctrlCapabilities[0] & CtrlCapabilities.IS_SUC.get()) != 0);
    } else
    {
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
    if (ctrlCapabilities != null)
    {
      return ((ctrlCapabilities[0] & CtrlCapabilities.ON_OTHER_NETWORK.get()) != 0);
    } else
    {
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
    if (ctrlCapabilities != null)
    {
      return ((ctrlCapabilities[0] & CtrlCapabilities.NODEID_SERVER_PRESENT
          .get()) != 0);
    } else
    {
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
    return slaveController;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isSupportedSerialCmd
   * (int)
   */
  public boolean isSupportedSerialCmd(int CommandId)
  {
    return serialCapabilityMask.zwaveNodeMaskNodeIn(CommandId);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#open(java.lang.String
   * , java.lang.String)
   */
  /*
  public void open(String transportLayerLibrary, String connectionString)
  {
    // transportLayer = (ITransportLayer)FindInterface(a, "ITransportLayer");
    frameLayer = new FrameLayerImpl();
    sessionLayer = new SessionLayerImpl();
    sessionLayer.setCallbackHandler(this);
    sessionLayer.open(frameLayer, transport);
  }
  */
  
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
    DataPacket req = addPayloadToSUCNodeId(nodeId, sucState, txOptions,
        capabilities);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveSetSucNodeId, req, 2, true);
    if ((rc == TXStatus.CompleteOk))
    {
      if (((rc.getResponses()[1].getLength() >= 1) && (rc.getResponses()[1]
          .getPayload()[0] == SetSucReturnValue.SucSetSucceeded.get())))
      {
        return true;
      }
    }
    return false;
  }

  private void requestNodeInfo(int[] payload, int nid)
  {
    // FuncID|status|nodeId|len|basic|generic|specific|data[0]|data[1],data[2]..data[len-7]....
    Node node = nodeTable.get(nid);
    node.setGeneric(payload[4]);
    node.setSpecific(payload[5]);
    if (payload.length > 6)
    {
      int[] supportedCmdClasses = new int[payload.length - 6];
      for (int i = 0; i < node.getSupportedCmdClasses().length; i++)
      {
        supportedCmdClasses[i] = payload[i + 6];
      }
      node.setSupportedCmdClasses(supportedCmdClasses);
    } else
    {
      node.setSupportedCmdClasses(null);
    }
    nodeTable.add(node);
    _requestNodeInfo = false;
    waitForNodeInfoCallbackHandler();
  }

  public void setCallbackHandler(ApplicationLayerAsyncCallback handler)
  {
    this.asyncCallback = handler;
  }

  private boolean thisSUCNodeId(int nodeId, boolean sucState,
      TXOption[] txOptions, int capabilities) throws FrameLayerException
  {
    DataPacket req = addPayloadToSUCNodeId(nodeId, sucState, txOptions,
        capabilities);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSetSucNodeId, req);
    DataPacket res = rc.getResponse();
    if ((rc == TXStatus.CompleteOk)
        && ((nodeId == _controllerNodeId) || ((res.getLength() >= 1) && (res
            .getPayload()[0] == SetSucReturnValue.SucSetSucceeded.get()))))
    {
      return true;
    } else
    {
      return false;
    }
  }

  private void waitForNodeInfoCallbackHandler()
  {
    if (waitForNodeInfoExecutor != null)
    {
      waitForNodeInfoExecutor.shutdownNow();
    }
    _requestNodeInfo = false;
    // RequestNodeInfoEventArgs e = new RequestNodeInfoEventArgs((Node)handler);
    // if (RequestNodeInfoEvent != null) RequestNodeInfoEvent((Node)handler);
    // if (RequestNodeInfoEvent != null) RequestNodeInfoEvent(this, e);
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
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveAddNodeToNetwork, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveADD_NODE_TO_NETWORK:"
          + mode.toString());

    return NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
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
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    req.addPayload(destinationNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveAssignReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(destinationNodeId);
    req.addPayload(0);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveAssignSucReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveControllerChange, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveCONTROLLER_CHANGE");
    NodeStatus status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
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
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveCreateNewPrimary, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveCREATE_NEW_PRIMARY");
    NodeStatus status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
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
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveDeleteReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveDeleteSucReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(enable ? 1 : 0);
    req.addPayload(capabilities);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveEnableSuc, req);
    if (rc == TXStatus.CompleteOk)
    {
      if (rc.getResponse().getPayload()[0] != 0)
      {
        return true;
      }
    }

    return false;
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
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetControllerCapabilities, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException(
          "CMD_ZWaveGET_CONTROLLER_CAPABILITIES");
    int[] payload = rc.getResponse().getPayload();
    ctrlCapabilities = payload;
    slaveController = ((payload[0] & CtrlCapabilities.IS_SECONDARY.get()) != 0);
    ControllerCapabilities caps = new ControllerCapabilities(payload,
        slaveController);
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
    return zwaveGetNodeProtocolInfo(nodeId, false);
  }

  public Node zwaveGetNodeProtocolInfo(int nodeId, boolean checkIfVirtual)
      throws FrameLayerException, ApplicationLayerException
  {
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetNodeProtocolInfo, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException(String.format("CMD_ZWaveGET_NODE_PROTOCOL_INFO - status: {}", rc.toString()));
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
    return new Node(nodeId, payload[0], payload[1], payload[2], payload[3],
        payload[4], payload[5], null, isVirtual);
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
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveGetSucNodeId, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveGET_SUC_NODE_ID");
    return rc.getResponse().getPayload()[0];
  }

  public NodeMask zwaveGetVirtualNodes() throws FrameLayerException,
      ApplicationLayerException
  {
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveIsFailedNode, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveIS_FAILED_NODE");
    return rc.getResponse().getPayload()[0] == 0 ? false : true;
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
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      DataPacket req = new DataPacket();
      req.addPayload(nodeId);

      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdZWaveIsVirtualNode, req);
      if (rc != TXStatus.CompleteOk)
        throw new ApplicationLayerException("CMD_ZWaveIS_VIRTUAL_NODE");

      return (rc.getResponse().getPayload()[0] != 0);
    }
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdLockRouteResponse, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_LOCK_ROUTE_RESPONSE");
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
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));
    req.addPayload(len);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryGetBuffer, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_GET_BUFFER");
    return rc.getResponse().getPayload();
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
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryGetByte, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_GET_BYTE");
    return rc.getResponse().getPayload()[0];
  }

  public MemoryGetId zwaveMemoryGetId() throws FrameLayerException,
      ApplicationLayerException
  {
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryGetId, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_MEMORY_GET_ID");
    int[] payload = rc.getResponse().getPayload();
    int homeId = payload[0] << 24 | payload[1] << 16 | payload[2] << 8
        | payload[3];
    int controllerNode = payload[4];

    return new MemoryGetId(homeId, controllerNode, rc);
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
    DataPacket req = new DataPacket();
    req.addPayload((int) (offset >> 8));
    req.addPayload((int) (offset & 0xFF));
    req.addPayload(value);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdMemoryPutByte, req);
    return rc;
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
        return ZWaveRediscoveryNeededReturnValue.getByVal(rc.getResponses()[2]
            .getPayload()[0]);
      } else
      {
        return st;
      }

    } else
    {
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
        return rc.getResponses();
      }
    }
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveReplaceFailedNode, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveReplaceFailedNode");
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
    DataPacket req = new DataPacket();
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveReplicationCommandComplete, req);
    if (!rc)
      throw new ApplicationLayerException(
          "CMD_ZWaveREPLICATION_COMMAND_COMPLETE");
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
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveRequestNetworkUpdate, req, 2, true,
        TIMEOUT);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveRequestNodeInfo, req, false);
    if (rc == TXStatus.CompleteOk)
    {
      _requestNodeInfo = true;
      waitForNodeInfoExecutor = Executors.newSingleThreadScheduledExecutor();
      waitForNodeInfoExecutor.schedule(new Runnable()
      {
        public void run()
        {
          waitForNodeInfoCallbackHandler();
        }
      }, TIMEOUT, TimeUnit.MILLISECONDS);

    }
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(
        DataFrame.CommandType.CmdZWaveRequestNodeNeighborUpdate, req, 2, true);

    if (rc == TXStatus.CompleteOk)
    {
      return RequestNeighbor.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(powerLevel);
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveRFPowerLevelSet, req);
    if (rc != TXStatus.CompleteOk)
    {
      throw new ApplicationLayerException("CMD_ZWaveRF_POWER_LEVEL_SET");
    }
    return rc.getResponse().getPayload()[0];
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
    if (data == null)
    {
      throw new NullPointerException("data");
    }
    return zwaveSendData(nodeId, data, txOptions, TIMEOUT);
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
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSendDataAbort, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveSEND_DATA_ABORT");
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
    return zwaveSendDataMeta(nodeId, data, txOptions, TIMEOUT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMeta
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption[] txOptions,
      int timeout) throws FrameLayerException
  {
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
      return rc;
    }
    return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
  }

  public TXStatus zwaveSendDataMulti(List<Integer> nodeIdList, int[] data,
      TXOption[] txOptions) throws FrameLayerException
  {
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
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
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
      return TXStatus.getByVal(rc.getResponse().getPayload()[0]);
    } else
    {
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendSlaveData
   * (int, int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption[])
   */
  public TXStatus zwaveSendSlaveData(int sourceId, int destinationId,
      int[] data, TXOption[] txOptions) throws FrameLayerException
  {
    return zwaveSendSlaveData(sourceId, destinationId, data, txOptions, TIMEOUT);
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
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      }
    } else
    {
      // rc = new TXStatus();
      rc = null;
    }
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
    return zwaveSendSlaveNodeInformation(sourceId, destinationId, txOptions,
        10000);
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
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
      }
    } else
    {
      // rc = new TXStatus();
      rc = null;
    }
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
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSerialApiGetCapabilities()
   */
  public int[] zwaveSerialApiGetCapabilities() throws FrameLayerException,
      ApplicationLayerException
  {
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdSerialApiGetCapabilities, req);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_SERIAL_API_GET_CAPABILITIES");
    int[] payload = rc.getResponse().getPayload();
    ctrlCapabilities = payload;
    if (ctrlCapabilities.length > 8)
    {
      serialCapabilityMask = new NodeBitmask();
      int[] temp = new int[ctrlCapabilities.length - 8];
      for (int n = 0; n < temp.length; n++)
      {
        temp[n] = ctrlCapabilities[n + 8];
      }
      serialCapabilityMask.store(temp);
    }
    return payload;
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
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdSerialApiSoftReset, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_SERIAL_API_SOFT_RESET");
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
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(
        DataFrame.CommandType.CmdZWaveSetDefault, req, true);
    if (rc != TXStatus.CompleteOk)
      throw new ApplicationLayerException("CMD_ZWaveSET_DEFAULT");
    enumerateNodes();
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
    DataPacket req = new DataPacket();
    req.addPayload(learnMode ? 1 : 0);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveSetLearnMode, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveSET_LEARN_MODE");
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
      return true;
    } else
    {
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
    DataPacket req = new DataPacket();
    req.addPayload(mode);
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveSetRFReceiveMode, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveSET_RF_RECEIVE_MODE");
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
    int suc = 0;
    if (slaveController)
      return -1;

    MemoryGetId ret = zwaveMemoryGetId();
    _controllerHomeId = ret.getHomeId();
    _controllerNodeId = ret.getNodeId();

    if (_controllerNodeId == 0)
      return -1;

    if (zwaveSetSucNodeId(_controllerNodeId, sucState,
        new TXOption[] {TXOption.TransmitOptionNone}, capabilities))
    {
      suc = _controllerNodeId;
      return suc;
    }
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
    if (libraryType.library == Library.ControllerBridgeLib)
    {
      DataPacket req = new DataPacket();
      req.addPayload(node);
      req.addPayload(mode);

      TXStatus rc = sessionLayer.requestWithResponse(
          DataFrame.CommandType.CmdZWaveSetSlaveLearnMode, req, true);
      if (rc == TXStatus.CompleteOk)
      {
        return (rc.getResponse().getPayload()[0] != 0);
      }
    }
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
    if (nodeId == _controllerNodeId)
    {
      return thisSUCNodeId(nodeId, sucState, txOptions, capabilities);
    } else
    {
      return otherSUCNodeId(nodeId, sucState, txOptions, capabilities);
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
    DataPacket req = new DataPacket();
    req.addPayload(Mode.NodeStop.get());
    boolean rc = sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdZWaveAddNodeToNetwork, req);
    if (!rc)
      throw new ApplicationLayerException("CMD_ZWaveADD_NODE_TO_NETWORK");
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
    DataPacket req = new DataPacket();
    return sessionLayer.requestWithNoResponse(
        DataFrame.CommandType.CmdStoreHomeId, req);
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
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    } else
    {
      return rc;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSupportedSerialCmds
   * ()
   */
  public String zwaveSupportedSerialCmds()
  {
    String retString = null;
    /*
     * ctrlCapabilities format:0 APPL_VERSION,1 APPL_REVISION,2 MAN_ID1,
     * 3MAN_ID2,4 PRODUCT_TYPE1, 5 PRODUCT_TYPE2, 6 PRODUCT_ID1,7 PRODUCT_ID2, 8
     * FUNCID_SUPPORTED...
     */
    if (serialCapabilityMask != null)
    {
      int i = 1;
      while (i < serialCapabilityMask.getLength() * 8)
      {
        if (serialCapabilityMask.zwaveNodeMaskNodeIn(i))
        {
          DataFrame.CommandType ct = DataFrame.CommandType.getByVal(i);
          if (ct != null)
            retString += ct.toString() + ",";
          else
            retString += i + ",";
        }
        i++;
      }
    }
    return retString;
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
      return TXStatus.CompleteFail.get();
    } else
    {
      return rc.getResponse().getPayload()[0];
    }
  }

}
