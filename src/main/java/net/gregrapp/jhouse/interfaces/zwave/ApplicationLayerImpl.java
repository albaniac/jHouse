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

import java.util.ArrayList;
import java.util.Hashtable;

import net.gregrapp.jhouse.interfaces.zwave.Constants.ChipType;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ControllerChangeMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CreateNewPrimaryControllerMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.LearnMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Library;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Mode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.NodeStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.RequestNeighbor;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ZWaveRediscoveryNeededReturnValue;
import net.gregrapp.jhouse.interfaces.zwave.DataFrame.CommandType;
import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 * 
 */
public class ApplicationLayerImpl implements ApplicationLayer,
    SessionLayerAsyncCallback
{
  private static final int DEFAULT_TIMEOUT = 10000; // How long in ms to wait
                                                    // for an response
  private static final int TIMEOUT = 180000; // wait 3 minuttes before timing
                                             // out
  private Node addedNode;
  private boolean disposed;
  private FrameLayer frameLayer;
  private Node[] nodeList;
  private NodeTable nodeTable = new NodeTable(100);
  private Node removedNode;
  // / <summary>
  // / Contains a mask of which SerialAPI commands supported by connected module
  // / </summary>
  NodeBitmask serialCapabilityMask = new NodeBitmask();
  private SessionLayer sessionLayer;

  private Transport transport;

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipRev()
   */
  public int chipRev()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipType()
   */
  public ChipType chipType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#close()
   */
  public void close()
  {
    // TODO Auto-generated method stub

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
    if (packet == null)
    {
      throw new NullPointerException("packet");
    }

    int[] payload = packet.getPayload();
    if (cmd == DataFrame.CommandType.CmdApplicationCommandHandler)
    {
      // ApplicationCommandEventArgs e = new
      // ApplicationCommandEventArgs(packet);
      // if (ApplicationCommandEvent != null) ApplicationCommandEvent(this, e);
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
            if ((payload.length - 7) > 0)
            {
              int[] supportedCmdClasses = new int[payload.length - 7];
              for (byte i = 0; i < addedNode.getSupportedCmdClasses().length; i++)
              {
                supportedCmdClasses[i] = payload[i + 7];
              }
              addedNode.setSupportedCmdClasses(supportedCmdClasses);
            }
            nodeTable.add(addedNode);
          }
        }
      } else if (nodeStatus == NodeStatus.Done)
      {
        addedNode = zwaveGetNodeProtocolInfo(nid);
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
      } else if (nodeStatus == NodeStatus.Done)
      {
        int nid = payload[2];
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
          Node node = nodeTable.get(nid);
          nodeTable.remove(nid);
          // RemoveNodeEventArgs e = new RemoveNodeEventArgs(node, cmd);
          // if (RemoveNodeEvent != null) RemoveNodeEvent(this, e);
        }
      }

      else if (appCtrlUpdateStatus == AppCtrlUpdateStatus.ADD_DONE)
      {
        Node node;
        if (libraryType.lib == Library.ControllerBridgeLib)
        {
          node = zwaveGetNodeProtocolInfo(nid, true);
        } else
        {
          node = zwaveGetNodeProtocolInfo(nid);
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
        waitForNodeInfocallbackHandler(null);
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
        enumerateNodes();
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
        Node node = zwaveGetNodeProtocolInfo(newId, true);
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
    // // testcmd, state, nodeid, status, runnr.
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
    // // command is not an unknown commando.
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#enumerateNodes()
   */
  public Node[] enumerateNodes()
  {
    synchronized (this)
    {
        DataPacket res;
        DataPacket req = new DataPacket();

        TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdSerialApiGetInitData, req);
        if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_SERIAL_API_GET_INIT_DATA");

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

        zwaveMemoryGetId(out _controllerHomeId, out _controllerNodeId);

        NodeMask virtualNodeMask = zwaveGetVirtualNodes();
        int nodeIdx = 0;
        for (int i = 0; i < len; i++)
        {
            int availabilityMask = payload[3 + i];
            for (byte bit = 0; bit < 8; bit++)
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
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#getLibraryVersion()
   */
  public String getLibraryVersion()
  {
    // TODO Auto-generated method stub
    return null;
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
    nodeTable.getList();
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
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isControllerIsRealPrimary
   * ()
   */
  public boolean isControllerIsRealPrimary()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isControllerIsSis()
   */
  public boolean isControllerIsSis()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#isControllerIsSuc()
   */
  public boolean isControllerIsSuc()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * isControllerOnOtherNetwork()
   */
  public boolean isControllerOnOtherNetwork()
  {
    // TODO Auto-generated method stub
    return false;
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
    // TODO Auto-generated method stub
    return false;
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
    // TODO Auto-generated method stub
    return false;
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
  public void open(String transportLayerLibrary, String connectionString)
  {
    // transportLayer = (ITransportLayer)FindInterface(a, "ITransportLayer");
    frameLayer = new FrameLayerImpl();
    sessionLayer = new SessionLayerImpl();
    sessionLayer.setCallbackHandler(this);
    sessionLayer.open(frameLayer, transport);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#serialApiVersion()
   */
  public int serialApiVersion()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveAddNodeToNetwork
   * (net.gregrapp.jhouse.interfaces.zwave.Constants.Mode)
   */
  public NodeStatus zwaveAddNodeToNetwork(Mode mode)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveAssignReturnRoute
   * (int, int)
   */
  public TXStatus zwaveAssignReturnRoute(int sourceNodeId, int destinationNodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveAssignSucReturnRoute
   * (int)
   */
  public TXStatus zwaveAssignSucReturnRoute(int destinationNodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveControllerChange
   * (net.gregrapp.jhouse.interfaces.zwave.Constants.ControllerChangeMode)
   */
  public NodeStatus zwaveControllerChange(ControllerChangeMode mode)
  {
    // TODO Auto-generated method stub
    return null;
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
      CreateNewPrimaryControllerMode mode)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveDeleteReturnRoute
   * (int)
   */
  public TXStatus zwaveDeleteReturnRoute(int sourceNodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveDeleteSucReturnRoute
   * (int)
   */
  public TXStatus zwaveDeleteSucReturnRoute(int sourceNodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveEnableSuc(boolean
   * , int)
   */
  public boolean zwaveEnableSuc(boolean enable, int capabilities)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveGetControllerCapabilities()
   */
  public int[] zwaveGetControllerCapabilities(out boolean slaveController) throws ApplicationLayerException
  {
    DataPacket res;
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveGetControllerCapabilities, req);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveGET_CONTROLLER_CAPABILITIES");
    int[] payload = rc.getResponse().getPayload();
    ctrlCapabilities = payload;
    slaveController = ((payload[0] & (int)CtrlCapabilities.IS_SECONDARY.get()) != 0);
    return payload;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveGetNodeProtocolInfo
   * (int)
   */
  public Node zwaveGetNodeProtocolInfo(int nodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveGetSucNodeId()
   */
  public int zwaveGetSucNodeId()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveIsFailedNode
   * (int)
   */
  public boolean zwaveIsFailedNode(int nodeId)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveIsVirtualNode
   * (int)
   */
  public boolean zwaveIsVirtualNode(int nodeId)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveLockRoutes(int)
   */
  public void zwaveLockRoutes(int nodeId)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryGetBuffer
   * (long, int)
   */
  public int[] zwaveMemoryGetBuffer(long offset, int len)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryGetByte
   * (long)
   */
  public int zwaveMemoryGetByte(long offset)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryPutBuffer
   * (long, int[], long)
   */
  public void zwaveMemoryPutBuffer(long offset, int[] buffer, long length)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveMemoryPutByte
   * (long, int)
   */
  public TXStatus zwaveMemoryPutByte(long offset, int value)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRediscoveryNeeded
   * (int)
   */
  public ZWaveRediscoveryNeededReturnValue zwaveRediscoveryNeeded(int nodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRemoveFailedNodeId
   * (int)
   */
  public DataPacket[] zwaveRemoveFailedNodeId(int nodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveRemoveNodeFromNetwork
   * (net.gregrapp.jhouse.interfaces.zwave.Constants.Mode)
   */
  public NodeStatus zwaveRemoveNodeFromNetwork(Mode mode)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveReplaceFailedNode
   * (int)
   */
  public boolean zwaveReplaceFailedNode(int nodeId)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveReplicationReceiveComplete()
   */
  public void zwaveReplicationReceiveComplete()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveReplicationSend
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveReplicationSend(int nodeId, int[] data,
      TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRequestNetworkUpdate
   * ()
   */
  public TXStatus zwaveRequestNetworkUpdate()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRequestNodeInfo
   * (int)
   */
  public TXStatus zwaveRequestNodeInfo(int nodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveRequestNodeNeighborUpdate(int)
   */
  public RequestNeighbor zwaveRequestNodeNeighborUpdate(int nodeId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveRFPowerLevelSet
   * (int)
   */
  public int zwaveRFPowerLevelSet(int powerLevel)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendData(int,
   * int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendData(int nodeId, int[] data, TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendData(int,
   * int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendData(int nodeId, int[] data, TXOption txOptions,
      int timeout)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataAbort()
   */
  public void zwaveSendDataAbort()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMeta
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMeta
   * (int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption txOptions,
      int timeout)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMulti
   * (java.util.ArrayList, int[],
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendDataMulti(ArrayList nodeIdList, int[] data,
      TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendNodeInformation
   * (int, net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendNodeInformation(int destination, TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendSlaveData
   * (int, int, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendSlaveData(int sourceId, int destinationId,
      int[] data, TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
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
      int[] data, TXOption txOptions, int timeout)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSendSlaveNodeInformation(int, int,
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendSlaveNodeInformation(int sourceId,
      int destinationId, TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSendSlaveNodeInformation(int, int,
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendSlaveNodeInformation(int sourceId,
      int destinationId, TXOption txOptions, int timeout)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendSucId(int,
   * net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendSucId(int nodeId, TXOption txOptions)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveSerialApiGetCapabilities()
   */
  public int[] zwaveSerialApiGetCapabilities()
  {
    DataPacket res;
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdSerialApiGetCapabilities, req);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_SERIAL_API_GET_CAPABILITIES");
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
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSerialApiSoftReset
   * ()
   */
  public void zwaveSerialApiSoftReset()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetDefault()
   */
  public void zwaveSetDefault()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetLearnMode
   * (boolean)
   */
  public boolean zwaveSetLearnMode(boolean learnMode)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetNodeInformation
   * (int, int, int, int[])
   */
  public void zwaveSetNodeInformation(int listening, int generic, int specific,
      int[] nodeParameter)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetPromiscuousMode
   * (boolean)
   */
  public boolean zwaveSetPromiscuousMode(boolean enable)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetRFReceiveMode
   * (int)
   */
  public void zwaveSetRFReceiveMode(int mode)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSelfAsSuc
   * (boolean, int)
   */
  public boolean zwaveSetSelfAsSuc(boolean sucState, int capabilities)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSlaveLearnMode
   * (int, int)
   */
  public boolean zwaveSetSlaveLearnMode(int node, int mode)
  {
    // TODO Auto-generated method stub
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
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSucNodeId
   * (int, boolean, net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption,
   * int)
   */
  public boolean zwaveSetSucNodeId(int nodeId, boolean sucState,
      TXOption txOptions, int capabilities)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveStopLearnMode()
   */
  public void zwaveStopLearnMode()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveStoreHomeId(int,
   * int)
   */
  public boolean zwaveStoreHomeId(int homeId, int nodeId)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveStoreNodeInfo
   * (int, net.gregrapp.jhouse.interfaces.zwave.Node)
   */
  public TXStatus zwaveStoreNodeInfo(int nodeId, Node nodeInfo)
  {
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveTest(int,
   * int, int, int, net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption,
   * int, int[])
   */
  public int zwaveTest(int testCmd, int testDelay, int testPayloadLength,
      int testCount, TXOption testTXOptions, int maxLength, int[] testNodeMask)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveVersion()
   */
  public VersionInfoType zwaveVersion()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  private static final int GET_INIT_DATA_FLAG_SLAVE_API = 0x01;
  private static final int GET_INIT_DATA_FLAG_TIMER_SUPPORT = 0x02;
  private static final int GET_INIT_DATA_FLAG_SECONDARY_CTRL = 0x04;
  private static final int GET_INIT_DATA_FLAG_IS_SUC = 0x08;

  private static final int ZWavePROTECT_TIME = 30000;

  private enum CtrlCapabilities
  {
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is secondary on current Z-Wave network
    // </summary>
    IS_SECONDARY(0x01),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // </summary>
    ON_OTHER_NETWORK(0x02),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is a member of a Z-Wave network with a NodeID Server present
    // </summary>
    NODEID_SERVER_PRESENT(0x04),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is the original owner of the current Z-Wave network HomeID
    // </summary>
    IS_REAL_PRIMARY(0x08),
    // <summary>
    // Return value mask for ZWaveGetControllerCapabilities
    // Controller is the SUC in current Z-WAve network
    // </summary>
    IS_SUC(0x10);

    private int value;

    CtrlCapabilities(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

    public static CtrlCapabilities getByVal(int value)
    {
      for (CtrlCapabilities c : CtrlCapabilities.class.getEnumConstants())
        if (c.get() == value)
          return c;
      return null;
    }
  }

  // ApplicationControllerUpdate status
  private enum AppCtrlUpdateStatus
  {
    SUC_ID(0x10), DELETE_DONE(0x20), ADD_DONE(0x40), ROUTING_PENDING(0x80), NODE_INFO_REQ_FAILED(
        0x81), NODE_INFO_REQ_DONE(0x82), NODE_INFO_RECEIVED(0x84);

    private int value;

    AppCtrlUpdateStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

    public static AppCtrlUpdateStatus getByVal(int value)
    {
      for (AppCtrlUpdateStatus s : AppCtrlUpdateStatus.class.getEnumConstants())
        if (s.get() == value)
          return s;
      return null;
    }
  }

  // <summary>
  // Controller capabilities
  // </summary>
  private int[] ctrlCapabilities = null;

  private VersionInfoType libraryType;
  private int _controllerNodeId;
  private int _controllerHomeId;
  private boolean _controllerChange;
  private boolean _newPrimary;
  private boolean _removeNode;
  private boolean _addNode;
  private boolean _requestNodeInfo = false;
  private boolean slaveApi;
  private boolean slaveController;
  // private System.Threading.Timer waitForNodeInfoTimer;
  int chipType = 0; // Initialy we do not know which Chip is used and
  int chipRev = 0; // if chipType still is undefined after the
  int serialAPIver = 0;

  // / <summary>
  // / Nodemask handling class
  // / </summary>
  public class NodeBitmask
  {
    // / <summary>
    // / Initializes a new instance of the <see cref="T:NodeBitmask"/> class.
    // / </summary>
    public NodeBitmask()
    {
      mask = null;
    }

    // / <summary>
    // / Creates a nodemask object
    // / </summary>
    // / <param name="length">length of nodemask (number of ids/8)</param>
    public NodeBitmask(int length)
    {
      if (length > 0)
      {
        mask = new int[length];
        // maxNodeID = length*8;
      }
    }

    // / <summary>
    // / Gets the length.
    // / </summary>
    // / <value>The length.</value>
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

    // / <summary>
    // / Gets the nodemask as an bit array
    // / </summary>
    // / <returns></returns>
    public int[] get()
    {
      return mask;
    }

    // / <summary>
    // / Returns the bitMask for the given NodeId
    // / </summary>
    // / <param name="nodeId"></param>
    // / <returns></returns>
    public int get(int nodeId)
    {
      return mask[nodeId >> 3];
    }

    // / <summary>
    // / Stores an entire array
    // / </summary>
    // / <param name="array">The array.</param>
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

    // / <summary>
    // / Set the bit corresponding to the nodeID supplied
    // / </summary>
    // / <param name="nodeId"></param>
    public void zwaveNodeMaskSetBit(int nodeId)
    {
      if (nodeId < 1)
      {
        return;
      }
      nodeId--;
      mask[nodeId >> 3] |= (int) (0x1 << (nodeId & 7));
    }

    // / <summary>
    // / Clears the bit corresponding to then nodeID supplied
    // / </summary>
    // / <param name="nodeId"></param>
    public void zwaveNodeMaskClearBit(int nodeId)
    {
      if (nodeId < 1)
      {
        return;
      }
      nodeId--;
      mask[nodeId >> 3] &= (int) ~(0x1 << (nodeId & 7));
    }

    // / <summary>
    // / Checks if the bit corresponding to the node ID is set
    // / </summary>
    // / <param name="nodeId"></param>
    // / <returns>true if bit is set</returns>
    public boolean zwaveNodeMaskNodeIn(int nodeId)
    {
      if (nodeId < 1)
      {
        return false;
      }
      nodeId--;
      if ((((mask[(nodeId >> 3)]) >> (int) (nodeId & 7)) & (int) 0x01) != 0)
      {
        return true;
      } else
      {
        return false;
      }
    }

    // / <summary>
    // / Returns true if any bits in mask
    // / </summary>
    // / <returns>true if bits set, false if not</returns>
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

    // / <summary>
    // / Clears the nodemask
    // / </summary>
    public void zwaveNodeMaskClear()
    {
      if (mask != null)
      {
        for (int i = 0; i < mask.length; i++)
        {
          mask[i] = 0;
        }
      }
    }

    private int[] mask;
    // private int maxNodeID;

  }// Class BitMask

  // / <summary>
  // / NodeMask
  // / </summary>
  public class NodeMask
  {
    // / <summary>
    // / NodeMask
    // / </summary>
    public NodeMask()
    {
      nodeMask = new int[29];
    }

    // / <summary>
    // / NodeMask(byte[] _nodeMask)
    // / </summary>
    // / <param name="_nodeMask"></param>
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

    // / <summary>
    // / clear
    // / </summary>
    public void clear()
    {
      for (int i = 0; i < 29; i++)
      {
        nodeMask[i] = 0;
      }
    }

    // / <summary>
    // /
    // / </summary>
    // / <param name="nodeId"></param>
    // / <returns></returns>
    public boolean get(int nodeId)
    {
      if ((nodeId > 0) && (nodeId <= 232))
      {
        return ((nodeMask[(nodeId - 1) >> 3] & (1 << ((nodeId - 1) & 0x07))) != 0);
      }
      return false;
    }

    // / <summary>
    // /
    // / </summary>
    // / <param name="nodeId"></param>
    // / <param name="value"></param>
    // / <returns></returns>
    public boolean set(int nodeId, boolean value)
    {
      if ((nodeId > 0) && (nodeId <= 232))
      {
        if (value)
        {
          /* Set nodeMask bit */
          nodeMask[((int) nodeId - 1) >> 3] |= (int) (1 << (((int) nodeId - 1) & 0x07));
        } else
        {
          /* Clear nodeMask bit */
          nodeMask[((int) nodeId - 1) >> 3] &= (int) ~(1 << (((int) nodeId - 1) & 0x07));
        }
        return true;
      }
      return false;
    }

    private int[] nodeMask;
  }

  private class NodeTable
  {
    public NodeTable(int initialSize)
    {
      nodeTable = new Hashtable<Integer, Node>(initialSize);
    }

    public void clear()
    {
      nodeTable.clear();
    }

    public Node get(int nid)
    {
      return (Node) nodeTable.get(nid);
    }

    public void remove(int nid)
    {
      nodeTable.remove(nid);
    }

    public void add(Node node)
    {
      if (node == null)
        return;
      if (nodeTable.contains(node.getId()))
        nodeTable.remove(node.getId());
      nodeTable.put(node.getId(), node);
    }

    public boolean contains(int nid)
    {
      return nodeTable.contains(nid);
    }

    public int size()
    {
      return nodeTable.size();
    }

    public Node[] getList()
    {
      Node[] nodes = new Node[nodeTable.size()];
      nodes = (Node[]) nodeTable.values().toArray(new Node[0]);
      return nodes;
    }

    private Hashtable<Integer, Node> nodeTable;
  }

  private void requestNodeInfo(int[] payload, int nid)
  {
      //  FuncID|status|nodeId|len|basic|generic|specific|data[0]|data[1],data[2]..data[len-7]....
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
      }
      else
      {
          node.setSupportedCmdClasses(null);
      }
      nodeTable.add(node);
      _requestNodeInfo = false;
      waitForNodeInfocallbackHandler(node);
  }
  
  /// <summary>
  /// Remove Node from the nodeTable
  /// </summary>
  /// <param name="id"></param>
  public void nodeRemove(int id)
  {
      nodeTable.remove(id);
  }
}
