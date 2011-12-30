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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.gregrapp.jhosue.utils.CollectionUtils;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ChipType;
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
    return chipRev;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#chipType()
   */
  public ChipType chipType()
  {
    return ChipType.getByVal(chipType);
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
  
  public NodeMask zwaveGetVirtualNodes()
  {
      NodeMask nodeMask;
      if (libraryType.lib == Library.ControllerBridgeLib)
      {
          DataPacket req = new DataPacket();

          TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveGetVirtualNodes, req);
          if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveGET_VIRTUAL_NODES");

          nodeMask = new NodeMask(rc.getResponse().getPayload());
      }
      else
      {
          nodeMask = new NodeMask();
      }
      return nodeMask;
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
  public int getSerialApiVersion()
  {
    return serialAPIver;
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
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveAddNodeToNetwork, req, true);
    if (rc != TXStatus.CompleteOk)
        throw new ApplicationLayerException("CMD_ZWaveADD_NODE_TO_NETWORK:" + mode.toString());

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
  {
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    req.addPayload(destinationNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveAssignReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(destinationNodeId);
    req.addPayload(0);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveAssignSucReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveControllerChange, req, true);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveCONTROLLER_CHANGE");
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
      CreateNewPrimaryControllerMode mode)
  {
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveCreateNewPrimary, req, true);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveCREATE_NEW_PRIMARY");
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveDeleteReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(sourceNodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveDeleteSucReturnRoute, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  {
    DataPacket res;
    DataPacket req = new DataPacket();
    req.addPayload(enable?1:0);
    req.addPayload(capabilities);
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveEnableSuc, req);
    if (rc == TXStatus.CompleteOk)
    {
        if (rc.getResponse().getPayload()[0] != 0)
        {
            return true;
        }
    }

    return false;
  }

  private boolean thisSUCNodeId(int nodeId, boolean sucState, TXOption txOptions, int capabilities)
  {
      DataPacket req = addPayloadToSUCNodeId(nodeId, sucState, txOptions, capabilities);
      TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveSetSucNodeId, req);
      DataPacket res = rc.getResponse();
      if ((rc == TXStatus.CompleteOk) && ((nodeId == _controllerNodeId) || ((res.getLength() >= 1) && (res.getPayload()[0] == SetSucReturnValue.SucSetSucceeded.get()))))
      {
          return true;
      }
      else
      {
          return false;
      }
  }

  private boolean otherSUCNodeId(int nodeId, boolean sucState, TXOption txOptions, int capabilities)
  {
      DataPacket[] res = new DataPacket[2];
      DataPacket req = addPayloadToSUCNodeId(nodeId, sucState, txOptions, capabilities);
      TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSetSucNodeId, req, 2, true);
      if ((rc == TXStatus.CompleteOk))
      {
          if (((rc.getResponses()[1].getLength() >= 1) && (rc.getResponses()[1].getPayload()[0] == SetSucReturnValue.SucSetSucceeded.get())))
          {
              return true;
          }
      }
      return false;
  }

  private DataPacket addPayloadToSUCNodeId(int nodeId, boolean sucState, TXOption txOptions, int capabilities)
  {
      DataPacket req = new DataPacket();
      req.addPayload(nodeId);
      req.addPayload(sucState?1:0);
      req.addPayload(txOptions.get());
      req.addPayload(capabilities);
      return req;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveGetControllerCapabilities()
   */
  public int[] zwaveGetControllerCapabilities(out boolean slaveController) throws ApplicationLayerException
  {
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
    return zwaveGetNodeProtocolInfo(nodeId, false);
  }

  public Node zwaveGetNodeProtocolInfo(int nodeId, boolean checkIfVirtual)
  {
      DataPacket req = new DataPacket();
      req.addPayload(nodeId);

      TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveGetNodeProtocolInfo, req);
      if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveGET_NODE_PROTOCOL_INFO");
      int[] payload = rc.getResponse().getPayload();

      boolean isVirtual = false;
      if (checkIfVirtual)
      {
          isVirtual = zwaveIsVirtualNode(nodeId);
      }
      //capability    : payload[0];
      //security      : payload[1];
      //reserved      : payload[2];
      //type basic    : payload[3];
      //type generic  : payload[4];
      //type specific : payload[5];
      return new Node(nodeId, payload[0], payload[1], payload[2], payload[3], payload[4], payload[5], null, isVirtual);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveGetSucNodeId()
   */
  public int zwaveGetSucNodeId()
  {
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveGetSucNodeId, req);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveGET_SUC_NODE_ID");
    return rc.getResponse().getPayload()[0];
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
    DataPacket res;
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);

    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveIsFailedNode, req);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveIS_FAILED_NODE");
    return rc.getResponse().getPayload()[0]==0?false:true;
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
    if (libraryType.lib == Library.ControllerBridgeLib)
    {
        DataPacket req = new DataPacket();
        req.addPayload(nodeId);

        TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveIsVirtualNode, req);
        if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveIS_VIRTUAL_NODE");

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
  public void zwaveLockRoutes(int nodeId)
  {
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdLockRouteResponse, req);
    if (!rc) throw new ApplicationLayerException("CMD_LOCK_ROUTE_RESPONSE");
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
    DataPacket req = new DataPacket();
    req.addPayload((int)(offset >> 8));
    req.addPayload((int)(offset & 0xFF));
    req.addPayload(len);
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdMemoryGetBuffer, req);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_MEMORY_GET_BUFFER");
    return rc.getResponse().getPayload();
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
    DataPacket req = new DataPacket();
    req.addPayload((int)(offset >> 8));
    req.addPayload((int)(offset & 0xFF));
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdMemoryGetByte, req);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_MEMORY_GET_BYTE");
    return rc.getResponse().getPayload()[0];
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
    if (length > 66) length = 66;
    DataPacket req = new DataPacket();
    req.addPayload((int)(offset >> 8));
    req.addPayload((int)(offset & 0xFF));
    req.addPayload((int)(length >> 8));
    req.addPayload((int)(length & 0xFF));
    req.addPayload(buffer);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdMemoryPutBuffer, req, 2, true);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_MEMORY_PUT_BUFFER");
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
    DataPacket req = new DataPacket();
    req.addPayload((int)(offset >> 8));
    req.addPayload((int)(offset & 0xFF));
    req.addPayload(value);
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdMemoryPutByte, req);
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
  {
    //DataPacket[] res = new DataPacket[3]; // We are receiving 2 responses...
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithVariableResponses(DataFrame.CommandType.CmdZWaveRediscoveryNeeded, req, 3, new int[] { 1, 4 }, true, 60000);
    ZWaveRediscoveryNeededReturnValue st = ZWaveRediscoveryNeededReturnValue.getByVal(rc.getResponses()[1].getPayload()[0]);
    if (rc == TXStatus.CompleteOk)
    {
        if (st == ZWaveRediscoveryNeededReturnValue.LostAccepted)
        {
            return ZWaveRediscoveryNeededReturnValue.getByVal(rc.getResponses()[2].getPayload()[0]);
        }
        else
        {
            return st;
        }

    }
    else
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithVariableReturnsAndResponses(DataFrame.CommandType.CmdZWaveRemoveFailedNodeId, req, 2, new int[] { 1, 2, 4, 5, 8, 16 }, true, TIMEOUT);
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
        }
        catch (NullPointerException e)
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(mode.get());
    NodeStatus status = NodeStatus.Done;
    if (mode == Mode.NodeStop)
    {
        boolean sent = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdZWaveRemoveNodeFromNetwork, req);
        if (!sent)
        {
            status = NodeStatus.Failed;
        }
    }
    else
    {
        TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveRemoveNodeFromNetwork, req, true);
        if (rc != TXStatus.CompleteOk)
        {
            throw new ApplicationLayerException("CMD_ZWaveREMOVE_NODE_FROM_NETWORK");
        }
        status = NodeStatus.getByVal(rc.getResponse().getPayload()[0]);
    }
    return status;
  }

  public TXStatus zwaveMemoryGetId(out int homeId, out int controllerNode)
  {
      DataPacket req = new DataPacket();

      TXStatus rc = sessionLayer.RequestWithResponse(DataFrame.CommandType.CmdMemoryGetId, req);
      if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_MEMORY_GET_ID");
      int[] payload = rc.getResponse().getPayload();
      homeId = (int)payload[0] << 24 | (int)payload[1] << 16 | (int)payload[2] << 8 | (int)payload[3];
      controllerNode = payload[4];
      return rc;
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdZWaveReplaceFailedNode, req);
    if (!rc) throw new ApplicationLayerException("CMD_ZWaveReplaceFailedNode");
    return rc;
  }

  public void clockSet(Time time)
  {
      if (time == null)
      {
          throw new NullPointerException("time");
      }
      DataPacket res;
      DataPacket req = new DataPacket();
      req.addPayload(time.weekday);
      req.addPayload(time.hour);
      req.addPayload(time.minute);
      TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdClockSet, req);
      if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_CLOCK_SET");
  }

  public Time clockGet()
  {
      DataPacket req = new DataPacket();
      TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdClockGet, req);
      if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_CLOCK_GET");
      int[] payload = rc.getResponse().getPayload();
      return new Time(payload[0], payload[1], payload[2]);
  }

  public boolean clockCompare(Time time)
  {
      if (time == null)
      {
          throw new NullPointerException("time");
      }
      DataPacket res;
      DataPacket req = new DataPacket();
      req.addPayload(time.weekday);
      req.addPayload(time.hour);
      req.addPayload(time.minute);

      TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdClockCompare, req);
      if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_CLOCK_CMP");
      int[] payload = rc.getResponse().getPayload();
      return payload[0]==0?false:true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveReplicationReceiveComplete()
   */
  public void zwaveReplicationReceiveComplete()
  {
    DataPacket req = new DataPacket();
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdZWaveReplicationCommandComplete, req);
    if (!rc) throw new ApplicationLayerException("CMD_ZWaveREPLICATION_COMMAND_COMPLETE");
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
    if (data == null)
    {
        throw new NullPointerException("data");
    }
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptions.get());
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveReplicationSendData, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
      return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  public TXStatus zwaveRequestNetworkUpdate()
  {
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveRequestNetworkUpdate, req, 2, true, TIMEOUT);
    if (rc == TXStatus.CompleteOk)
    {
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  public TXStatus zwaveRequestNodeInfo(int nodeId)
  {
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveRequestNodeInfo, req, false);
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

  private void waitForNodeInfoCallbackHandler()
  {
      if (waitForNodeInfoExecutor != null)
      {
        waitForNodeInfoExecutor.shutdownNow();
      }
      _requestNodeInfo = false;
      //RequestNodeInfoEventArgs e = new RequestNodeInfoEventArgs((Node)handler);
      // if (RequestNodeInfoEvent != null) RequestNodeInfoEvent((Node)handler);
      //if (RequestNodeInfoEvent != null) RequestNodeInfoEvent(this, e);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#
   * zwaveRequestNodeNeighborUpdate(int)
   */
  public RequestNeighbor zwaveRequestNodeNeighborUpdate(int nodeId)
  {
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveRequestNodeNeighborUpdate, req, 2, true);

    if (rc == TXStatus.CompleteOk)
    {
        return RequestNeighbor.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  public int zwaveRFPowerLevelSet(int powerLevel)
  {
    DataPacket req = new DataPacket();
    req.addPayload(powerLevel);
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveRFPowerLevelSet, req);
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
   * int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendData(int nodeId, int[] data, TXOption txOptions)
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
   * int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption, int)
   */
  public TXStatus zwaveSendData(int nodeId, int[] data, TXOption txOptions,
      int timeout)
  {
    if (data == null)
    {
        throw new NullPointerException("data");
    }

    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptions.get());
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSendData, req, 2, true, timeout);
    if (rc == TXStatus.CompleteOk)
    {
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  public void zwaveSendDataAbort()
  {
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveSendDataAbort, req, true);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_ZWaveSEND_DATA_ABORT");
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
    return zwaveSendDataMeta(nodeId, data, txOptions, TIMEOUT);
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
    if (data == null)
    {
        throw new NullPointerException("data");
    }
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptions.get());
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSendDataMeta, req, 2, true, timeout);
    if (rc != TXStatus.CompleteOk)
    {
        return rc;
    }
    return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
  }

 
  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSendDataMulti(java.util.List, int[], net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption)
   */
  public TXStatus zwaveSendDataMulti(List<Integer> nodeIdList, int[] data, TXOption txOptions)
  {
    if (nodeIdList == null || data == null)
    {
        throw new NullPointerException("nodeIdList");
    }
    DataPacket[] res = new DataPacket[2];
    DataPacket req = new DataPacket();

    req.addPayload(nodeIdList.size());
    req.addPayload(CollectionUtils.toIntArray(nodeIdList));
    req.addPayload(data.length);
    req.addPayload(data);
    req.addPayload(txOptions.get());
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSendDataMulti, req, 2, true);
    if (rc == TXStatus.CompleteOk)
    {
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
    {
        return rc;
    }
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
    DataPacket req = new DataPacket();
    req.addPayload(destination);
    req.addPayload(txOptions.get());
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveSendNodeInformation, req, true);
    if (rc != TXStatus.CompleteOk)
    {
        return TXStatus.getByVal(rc.getResponse().getPayload()[0]);
    }
    else
    {
        return rc;
    }  
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
      int[] data, TXOption txOptions, int timeout)
  {
    if (data == null)
    {
        throw new NullPointerException("data");
    }
    TXStatus rc;
    if (libraryType.lib == Library.ControllerBridgeLib)
    {
        DataPacket req = new DataPacket();
        req.addPayload(sourceId);
        req.addPayload(destinationId);
        req.addPayload(data.length);
        req.addPayload(data);
        req.addPayload(txOptions.get());
        rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSendSlaveData, req, 2, true, timeout);
        if (rc == TXStatus.CompleteOk)
        {
            return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
        }
    }
    else
    {
      //rc = new TXStatus();
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
      int destinationId, TXOption txOptions)
  {
    return zwaveSendSlaveNodeInformation(sourceId, destinationId, txOptions, 10000);
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
    TXStatus rc;
    if (libraryType.lib == Library.ControllerBridgeLib)
    {
        DataPacket req = new DataPacket();
        req.addPayload(sourceId);
        req.addPayload(destinationId);
        req.addPayload(txOptions.get());

        rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSendSlaveNodeInfo, req, 2, true, timeout);
        if (rc == TXStatus.CompleteOk)
        {
            return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
        }
    }
    else
    {
      //rc = new TXStatus();
      rc = null;
    }
    return rc;
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
    DataPacket req = new DataPacket();
    req.addPayload(nodeId);
    req.addPayload(txOptions.get());
    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdZWaveSendSucId, req, 2, true, DEFAULT_TIMEOUT);

    if ((rc == TXStatus.CompleteOk) && (rc.getResponses()[1].getLength() == 1))
    {
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
  public int[] zwaveSerialApiGetCapabilities()
  {
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
    DataPacket req = new DataPacket();

    req.addPayload(acknowledgeTimeout);
    req.addPayload(timeout);
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdSerialApiSetTimeouts, req);
    if (rc != TXStatus.CompleteOk)
        throw new ApplicationLayerException("CMD_SERIAL_API_SET_TIMEOUTS");
    // Get the payload as it contains the old ackTimeout and byteTimeout which previously was present in the ZW module
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
  public void zwaveSerialApiSoftReset()
  {
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdSerialApiSoftReset, req, true);
    if (rc != TXStatus.CompleteOk) throw new ApplicationLayerException("CMD_SERIAL_API_SOFT_RESET");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetDefault()
   */
  public void zwaveSetDefault()
  {
    DataPacket req = new DataPacket();
    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveSetDefault, req, true);
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
  {
    DataPacket req = new DataPacket();
    req.addPayload(learnMode?1:0);
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdZWaveSetLearnMode, req);
    if (!rc) throw new ApplicationLayerException("CMD_ZWaveSET_LEARN_MODE");
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
      int[] nodeParameter)
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
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdSerialApiApplNodeInformation, req);
    if (!rc) throw new ApplicationLayerException("CMD_SERIAL_API_APPL_NODE_INFORMATION");
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
    DataPacket req = new DataPacket();
    req.addPayload(mode);
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdZWaveSetRFReceiveMode, req);
    if (!rc) throw new ApplicationLayerException("CMD_ZWaveSET_RF_RECEIVE_MODE");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.zwave.ApplicationLayer#zwaveSetSelfAsSuc
   * (boolean, int)
   */
  public boolean zwaveSetSelfAsSuc(boolean sucState, int capabilities, out int suc)
  {
    suc = 0;
    if (slaveController) return false;

    zwaveMemoryGetId(out _controllerHomeId, out _controllerNodeId);
    if (_controllerNodeId == 0) return false;

    if (zwaveSetSucNodeId(_controllerNodeId, sucState, 0, capabilities))
    {
        suc = _controllerNodeId;
        return true;
    }
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
    if (libraryType.lib == Library.ControllerBridgeLib)
    {
        DataPacket req = new DataPacket();
        req.addPayload(node);
        req.addPayload(mode);

        TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveSetSlaveLearnMode, req, true);
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
  {
    if (nodeParameter == null)
    {
        throw new NullPointerException("nodeParameter");
    }

    if (libraryType.lib == Library.ControllerBridgeLib)
    {
        DataPacket req = new DataPacket();
        req.addPayload(nodeId);
        req.addPayload(listening);
        req.addPayload(generic);
        req.addPayload(specific);
        req.addPayload(nodeParameter.length);
        req.addPayload(nodeParameter);
        boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdSerialApiSlaveNodeInfo, req);
        if (!rc) throw new ApplicationLayerException("CMD_SERIAL_API_APPL_SLAVE_NODE_INFO");
    }
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
    if (nodeId == _controllerNodeId)
    {
        return thisSUCNodeId(nodeId, sucState, txOptions, capabilities);
    }
    else
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
  public void zwaveStopLearnMode()
  {
    DataPacket req = new DataPacket();
    req.addPayload(Mode.NodeStop.get());
    boolean rc = sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdZWaveAddNodeToNetwork, req);
    if (!rc) throw new ApplicationLayerException("CMD_ZWaveADD_NODE_TO_NETWORK");
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
    DataPacket req = new DataPacket();
    return sessionLayer.requestWithNoResponse(DataFrame.CommandType.CmdStoreHomeId, req);
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

    TXStatus rc = sessionLayer.requestWithMultipleResponses(DataFrame.CommandType.CmdStoreNodeInfo, req, 2);

    if (rc == TXStatus.CompleteOk)
    {
        return TXStatus.getByVal(rc.getResponses()[1].getPayload()[0]);
    }
    else
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
    /* ctrlCapabilities format:
     *0 APPL_VERSION,1 APPL_REVISION,2 MAN_ID1, 3MAN_ID2, 
     *4 PRODUCT_TYPE1, 5 PRODUCT_TYPE2, 6 PRODUCT_ID1,7 PRODUCT_ID2, 8 FUNCID_SUPPORTED...*/
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
    DataPacket req = new DataPacket();

    TXStatus rc = sessionLayer.requestWithResponse(DataFrame.CommandType.CmdZWaveGetVersion, req);
    if (rc != TXStatus.CompleteOk) return libraryType;// throw new ApplicationLayerException("CmdZWaveGetVersion");  

    libraryType = new VersionInfoType();

    int[] payload = rc.getResponse().getPayload();
    libraryType.ver = new String(payload, 6, 6);
    if (libraryType.ver.endsWith("\0"))
    {
        libraryType.ver = libraryType.ver.substring(0, libraryType.ver.length() - 1);
    }
    libraryType.lib = Library.getByVal(payload[12]);

    return libraryType;
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
  private ScheduledExecutorService waitForNodeInfoExecutor;
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
