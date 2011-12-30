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
//          Author:   Jette Christensen
//
//          Last Changed By:  $Author: jrm $
//          Revision:         $Revision: 1.14 $
//          Last Changed:     $Date: 2007/03/02 12:12:21 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.ArrayList;
import java.util.List;

import net.gregrapp.jhouse.interfaces.zwave.Constants.ChipType;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ControllerChangeMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.CreateNewPrimaryControllerMode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Library;
import net.gregrapp.jhouse.interfaces.zwave.Constants.Mode;
import net.gregrapp.jhouse.interfaces.zwave.Constants.NodeStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.RequestNeighbor;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXOption;
import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;
import net.gregrapp.jhouse.interfaces.zwave.Constants.ZWaveRediscoveryNeededReturnValue;
import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 *
 */
public interface ApplicationLayer
{
  // <summary>
  // Configure and connect the Applicationlayer to a ZWave module
  // </summary>
  // <param name="transportLayerLibrary">Name of transportLayer</param>
  // <param name="connectionString">String used to pass connection information to transportLayer</param>
  void open(String transportLayerLibrary, String connectionString);
  // <summary>
  // Close the connection to the transportLayerLibrary
  // </summary>
  void close();

  // <summary>
  // 
  // </summary>
  // <returns></returns>
  SessionLayer getSessionLayer();
  // <summary>
  // 
  // </summary>
  // <returns></returns>
  FrameLayer getFrameLayer();
  // <summary>
  // 
  // </summary>
  // <returns></returns>
  Transport getTransport();

  // <summary>
  // Returns statistics about the communication
  // </summary>
  // <returns>Read/write/error statistics</returns>
  ZWStatistics getStatistics();
  // <summary>
  // Update the capabilities and return a list of nodes as read from the ZWave module
  // </summary>
  // <returns>List of nodes</returns>
  Node[] enumerateNodes();
  // <summary>
  // Returns a copy of the node list stored in the DLL (does not read from ZWave module)
  // </summary>
  // <returns>Copy of node list</returns>
  Node[] getAllNodes();
  // <summary>
  // Reads the devicetype from the ZWave module
  // </summary>
  // <param name="nodeId">Node ID to get info for</param>
  // <returns></returns>
  Node zwaveGetNodeProtocolInfo(int nodeId);
  Node zwaveGetNodeProtocolInfo(int nodeId, boolean checkIfVirtual);
  // <summary>
  // Gets the node capabilities from the ZWave.dll list (does not read from ZWave module)
  // </summary>
  // <param name="nodeId">nodeID</param>
  // <returns>null if unkown ZWNode if it exist locally</returns>
  Node getNode(int nodeId);

  // <summary>
  // Simply check if the node Id is in the ZWave.dll copy of the nodetable
  // </summary>
  // <param name="id">id to look for</param>
  // <returns>true if it exist false if not</returns>
  boolean isNodePresent(int id);
  // <summary>
  // Returns the numeber of nodes in the ZWave.dll copy of the nodetable.
  // </summary>
  // <returns>number of nodes</returns>
  //int GetNodeCount();
  int getNodeCount();
  // <summary>
  // Return the ZWave Chiptype
  // </summary>
  // <returns>The ChipType</returns>
  ChipType chipType();
  // <summary>
  // Returns the ZWave Chip revision
  // </summary>
  // <returns>revision</returns>
  int chipRev();
  // <summary>
  // Returns the ZWave SerialAPI version
  // </summary>
  // <returns></returns>
  int serialApiVersion();

  // <summary>
  // Returns the library type and version used on the ZWaveModule
  // </summary>
  // <returns>Type and version</returns>
  VersionInfoType zwaveVersion();
  // <summary>
  // Transmit a frame to the node ID specified. If 0xFF is specified the frame is broadcasted
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">Transmit options (TRANSMIT_OPTION_xxx)</param>
  // <param name="timeout">Response timeout in ms</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendData(int nodeId, int[] data, TXOption txOptions, int timeout);
  // <summary>
  // 
  // </summary>
  // <param name="nodeId"></param>
  // <param name="data"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendData(int nodeId, int[] data, TXOption txOptions);
  // <summary>
  // Transmit a frame to the node ID specified. If 0xFF is specified the frame is broadcasted
  // </summary>
  // <param name="sourceId">Source node</param>
  // <param name="destinationId">Destination node</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">Transmit options (TRANSMIT_OPTION_xxx)</param>
  // <param name="timeout">Response timeout in ms</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendSlaveData(int sourceId, int destinationId, int[] data, TXOption txOptions, int timeout);

  // <summary>
  // Z-wave rediscovery needed.
  // </summary>
  // <param name="nodeId">The node id.</param>
  // <returns></returns>
  ZWaveRediscoveryNeededReturnValue zwaveRediscoveryNeeded(int nodeId);
  // <summary>
  // 
  // </summary>
  // <param name="sourceId"></param>
  // <param name="destinationId"></param>
  // <param name="data"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendSlaveData(int sourceId, int destinationId, int[] data, TXOption txOptions);
  // <summary>
  // Transmit a frame to the node IDs specified. If 0xFF is specified the frame is broadcasted
  // </summary>
  // <param name="nodeIdList">list of nodes to send to</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">if TRANSMIT_OPTION_ACK is specified each node gets a singlecast</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendDataMulti(List<Integer> nodeIdList, int[] data, TXOption txOptions);
  // <summary>
  // Transmit a frame to the node ID specified. If 0xFF is specified the frame is broadcasted
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">Transmit options (TRANSMIT_OPTION_xxx)</param>
  // <param name="timeout">Response timeout in ms</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption txOptions, int timeout);
  // <summary>
  // Transmita meta data frame to nodeID.
  // </summary>
  // <param name="nodeId"></param>
  // <param name="data"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption txOptions);
  // <summary>
  // Abort application initiated transmissions started by calling ZWaveSendData
  // </summary>
  void zwaveSendDataAbort();

  // <summary>
  // Enable Add node function
  // </summary>
  // <param name="mode">SLAVE/CONTROLLER/ANY</param>
  // <returns></returns>
  NodeStatus zwaveAddNodeToNetwork(Mode mode);
  // <summary>
  // Enable this controller to hand over its primary status
  // </summary>
  // <param name="mode">ControllerChangeMode START/STOP/FAILED</param>
  // <returns>Add node state</returns>
  NodeStatus zwaveControllerChange(ControllerChangeMode mode);
  // <summary>
  // Enable this controller to create a new primary. Only valid for SUC
  // </summary>
  // <param name="mode">START/STOP/FAILED</param>
  // <returns></returns>
  NodeStatus zwaveCreateNewPrimaryCtrl(CreateNewPrimaryControllerMode mode);
  // <summary>
  // Force the controller to assign SUC/SIS routes 
  // </summary>
  // <param name="destinationNodeId"></param>
  // <returns></returns>
  TXStatus zwaveAssignSucReturnRoute(int destinationNodeId);
  // <summary>
  // Assigns Return routes to a routing slave
  // </summary>
  // <param name="sourceNodeId">Node to receive the routes</param>
  // <param name="destinationNodeId">destination of the route</param>
  // <returns></returns>
  TXStatus zwaveAssignReturnRoute(int sourceNodeId, int destinationNodeId);
  // <summary>
  // Deletes the return routes assigned to a Routing Node
  // </summary>
  // <param name="sourceNodeId">Node to delete routes for</param>
  // <returns></returns>
  TXStatus zwaveDeleteReturnRoute(int sourceNodeId);
  // <summary>
  // Deletes the SUC return routes on a routing slave
  // </summary>
  // <param name="sourceNodeId">Node to delete SUC return routes</param>
  // <returns></returns>
  TXStatus zwaveDeleteSucReturnRoute(int sourceNodeId);
  // <summary>
  // Transmits the SUC node ID to the node specified
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="txOptions">Option</param>
  // <returns>Transmit status</returns>
  TXStatus zwaveSendSucId(int nodeId, TXOption txOptions);
  // <summary>
  // Enables SUC for this node
  // </summary>
  // <param name="enable">true/false</param>
  // <param name="capabilities"></param>
  // <returns>true if success false if not</returns>
  boolean zwaveEnableSuc(boolean enable, int capabilities);
  // <summary>
  // Check if a node ID is registered as failed
  // </summary>
  // <param name="nodeId"></param>
  // <returns>true/false</returns>
  boolean zwaveIsFailedNode(int nodeId);
  // <summary>
  // Get the home and node ID of this controller
  // </summary>
  // <param name="homeId"></param>
  // <param name="controllerNode"></param>
  // <returns></returns>
  TXStatus zwaveMemoryGetId(out Int32 homeId, out int controllerNode);
  // <summary>
  // Stores the specified buffer in ZWaveModule Nonvolatile memory
  // </summary>
  // <param name="offset">Application offset start address</param>
  // <param name="buffer">Buffer to write</param>`
  // <param name="length">number of bytes to write</param>
  void zwaveMemoryPutBuffer(long offset, int[] buffer, long length);
  // <summary>
  // Store the specified byte at the offset adress specified
  // </summary>
  // <param name="offset">adress</param>
  // <param name="value">value to write</param>
  // <returns></returns>
  TXStatus zwaveMemoryPutByte(long offset, int value);
  // <summary>
  // Reads the number of bytes from ZWaveModule nonvolatile memory
  // </summary>
  // <param name="offset">Start offset adress</param>
  // <param name="len">number of bytes to read</param>
  // <returns>array of read values</returns>
  int[] zwaveMemoryGetBuffer(long offset, int len);
  // <summary>
  // Reads the byte located at the offset specified
  // </summary>
  // <param name="offset"></param>
  // <returns></returns>
  int zwaveMemoryGetByte(long offset);

  // <summary>
  // Enables controller to remove nodes from the network
  // </summary>
  // <param name="mode">SLAVE/CONTROLLER/ANY/STOP</param>
  // <returns>status</returns>
  NodeStatus zwaveRemoveNodeFromNetwork(Mode mode);
  // <summary>
  // Only used during replication to transfer application information. See programming guide.
  // Used when the controller is in replication mode. It sends the payload and 
  // expects the receiver to respond with a command complete message 
  // (ZWaveREPLICATION_COMMAND_COMPLETE). Messages sent using this command should always 
  // be part of the Z-Wave controller replication command class.
  // </summary>
  // <param name="nodeId">destination</param>
  // <param name="data">payload</param>
  // <param name="txOptions">transmit options</param>
  // <returns></returns>
  TXStatus zwaveReplicationSend(int nodeId, int[] data, TXOption txOptions);
  // <summary>
  // Request network update from the SUC/SIS 
  // </summary>
  // <returns></returns>
  TXStatus zwaveRequestNetworkUpdate();
  // <summary>
  // Request nodeinformation frame from nodeId
  // </summary>
  // <param name="nodeId"></param>
  // <returns></returns>
  TXStatus zwaveRequestNodeInfo(int nodeId);
  // <summary>
  // Request that a node updates its neighbours
  // </summary>
  // <param name="nodeId"></param>
  // <returns></returns>
  RequestNeighbor zwaveRequestNodeNeighborUpdate(int nodeId);
  // <summary>
  // Send out this device Node information Frame
  // </summary>
  // <param name="destination">Destination node</param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendNodeInformation(int destination, TXOption txOptions);
  // <summary>
  // Reset all protocol data back to factory default (incl home and nodeID)
  // </summary>
  void zwaveSetDefault();
  // <summary>
  // Enable/disabl this device to be added to or removed from a network
  // </summary>
  // <param name="learnMode"></param>
  // <returns></returns>
  boolean zwaveSetLearnMode(boolean learnMode);
  // <summary>
  // Used when a Static controller wants to become SUC itself.
  // </summary>
  // <param name="sucState">true become SUC, false disable SUC</param>
  // <param name="capabilities"></param>
  // <param name="suc">Status</param>
  // <returns></returns>
  boolean zwaveSetSelfAsSuc(boolean sucState, int capabilities, out int suc);
  // <summary>
  // Assign or remove SUC on another controller in the network
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="sucState"></param>
  // <param name="txOptions"></param>
  // <param name="capabilities"></param>
  // <returns></returns>
  boolean zwaveSetSucNodeId(int nodeId, boolean sucState, TXOption txOptions, int capabilities);
  // <summary>
  // Get the controller capabilities/ current configuration
  // </summary>
  // <param name="slaveController"></param>
  // <returns></returns>
  /// Capabilities flag:
  /// Bit 0: 0 = Controller API; 1 = Slave API
  /// Bit 1: 0 = Timer functions not supported; 1 = Timer functions supported.
  /// Bit 2: 0 = Primary Controller; 1 = Secondary Controller
  /// Bit 3-7: reserved   
  int[] zwaveGetControllerCapabilities(out boolean slaveController) throws ApplicationLayerException;
  // <summary>
  // Get the Capabilities of the ZWave module serialAPI (supported functions)
  // </summary>
  // <returns>bitmask of supported capabilities</returns>
  int[] zwaveSerialApiGetCapabilities();

  // <summary>
  // Returns a comma separated string which contains a list of commands supported by Embedded module
  // Commands unhandled by ZWave.DLL are returned as the command number (Base 10)
  // </summary>
  // <returns>String of enumaration names</returns>
  String zwaveSupportedSerialCmds();

  // <summary>
  // Checks if a given byte command ID is supported by embedded module
  // </summary>
  // <param name="CommandId">id number to check</param>
  // <returns>true if supported, false if not</returns>
  boolean isSupportedSerialCmd(int CommandId);

  // <summary>
  // Set the Serial communication timeouts in 10ms
  // </summary>
  // <param name="acknowledgeTimeout">Timeout in 10ms waiting for ACK</param>
  // <param name="timeout">Timeout in 10ms waiting for another byte in frame</param>
  // <returns></returns>
  int[] zwaveSerialApiSetTimeout(int acknowledgeTimeout, int timeout);
  // <summary>
  // Make Z-Wave module do a software reset
  // </summary>
  void zwaveSerialApiSoftReset();
  // <summary>
  // Get the nodeId of the node that is currently assigned SUC or SIS 0 if none 
  // </summary>
  // <returns></returns>
  int zwaveGetSucNodeId();
  // <summary>
  // Request the ZWave module to remove a failing node from the network
  // </summary>
  // <param name="nodeId">nodeId to remove</param>
  // <returns>status</returns>
  DataPacket[] zwaveRemoveFailedNodeId(int nodeId);
  // <summary>
  // Request teh ZWave module to replace a failing node in the network
  // </summary>
  // <param name="nodeId">node Id to try and replace</param>
  // <returns></returns>
  boolean zwaveReplaceFailedNode(int nodeId);
  // <summary>
  // Enable or disable RF on the ZWave module.
  // </summary>
  // <param name="mode"></param>
  void zwaveSetRFReceiveMode(int mode);
  // <summary>
  // 
  // </summary>
  // <param name="powerLevel"></param>
  // <returns>The RF powerlevel set</returns>
  int zwaveRFPowerLevelSet(int powerLevel);
  // <summary>
  // Lock a response route to the nodeId supplied
  // </summary>
  // <param name="nodeId"></param>
  void zwaveLockRoutes(int nodeId);
  // <summary>
  // Sends a command complete. This should be used as a response to ReplicationSend
  // </summary>
  void zwaveReplicationReceiveComplete();
  // <summary>
  // Setup this nodes Nodeinformation
  // </summary>
  // <param name="listening"></param>
  // <param name="generic">Generic devicetype</param>
  // <param name="specific">Specific devicetype</param>
  // <param name="nodeParameter">Supported commandclasses</param>
  void zwaveSetNodeInformation(int listening, int generic, int specific, int[] nodeParameter);
  // <summary>
  // Disables the learn mode
  // </summary>
  void zwaveStopLearnMode();
  // <summary>
  // Returns true if a SIS is available
  // </summary>
  boolean isNodeIdServerPresent();
  // <summary>
  // 
  // </summary>
  boolean isControllerIsRealPrimary();
  // <summary>
  // 
  // </summary>
  boolean isControllerIsSis();
  // <summary>
  // 
  // </summary>
  boolean isControllerOnOtherNetwork();
  // <summary>
  // 
  // </summary>
  boolean isControllerIsSuc();
  // <summary>
  // 
  // </summary>
  boolean isRealTimeSystem();
  // <summary>
  // 
  // </summary>
  Library getLibraryType();
  // <summary>
  // 
  // </summary>
  String getLibraryVersion();

  // <summary>
  // Gets the node list.
  // </summary>
  // <value>The node list.</value>
  Node[] getNodeList();


  // region ZWaveINSTALLER
  // <summary>
  // Store the HomeId on the Controller in the external EEPROM
  // Only available on Installer ZWave modules
  // </summary>
  // <param name="homeId">four byte array with the homeId in</param>
  // <param name="nodeId"></param>
  boolean zwaveStoreHomeId(int homeId, int nodeId);
  // <summary>
  // Stores the nodeinformation for a specific node
  // Only available on Installer ZWave modules
  // </summary>
  // <param name="nodeId">nodeId</param>
  // <param name="nodeInfo">NodeInfo</param>
  // <returns></returns>
  TXStatus zwaveStoreNodeInfo(int nodeId, Node nodeInfo);

  // endregion

  // region ZWaveBRIDGE
  // <summary>
  // ZWaveSendSlaveNodeInformation with timeout
  // </summary>
  // <param name="sourceId"></param>
  // <param name="destinationId"></param>
  // <param name="txOptions"></param>
  // <param name="timeout"></param>
  // <returns></returns>
  TXStatus zwaveSendSlaveNodeInformation(int sourceId, int destinationId, TXOption txOptions, int timeout);
  // <summary>
  // ZWaveSendSlaveNodeInformation 
  // </summary>
  // <param name="sourceId"></param>
  // <param name="destinationId"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendSlaveNodeInformation(int sourceId, int destinationId, TXOption txOptions);
  // <summary>
  // ZWaveSetSlaveLearnMode(byte node, byte mode)
  // </summary>
  // <param name="node"></param>
  // <param name="mode"></param>
  // <returns></returns>
  boolean zwaveSetSlaveLearnMode(int node, int mode);
  // <summary>
  // ZWaveSetSlaveNodeInformation(byte nodeID, byte listening, byte generic, byte specific, byte[] nodeParm)
  // </summary>
  // <param name="nodeId"></param>
  // <param name="listening"></param>
  // <param name="generic"></param>
  // <param name="specific"></param>
  // <param name="nodeParameter"></param>
  void zwaveSetSlaveNodeInformation(int nodeId, int listening, int generic, int specific, int[] nodeParameter);
  // <summary>
  // ZWaveIsVirtualNode(byte nodeID)
  // </summary>
  // <param name="nodeId"></param>
  // <returns></returns>
  boolean zwaveIsVirtualNode(int nodeId);
  // endregion ZWaveBRIDGE
  
  // region ZWaveINSTALLER
  // <summary>
  // enable / disable the installer lib promiscuous mode
  // </summary>
  // <param name="enable"></param>
  // <returns></returns>
  boolean zwaveSetPromiscuousMode(boolean enable);
  // endregion

  // region ERTT
  // <summary>
  // 
  // </summary>
  // <param name="testCmd"></param>
  // <param name="testDelay"></param>
  // <param name="testPayloadLength"></param>
  // <param name="testCount"></param>
  // <param name="testTXOptions"></param>
  // <param name="maxLength"></param>
  // <param name="testNodeMask"></param>
  // <returns></returns>
  int zwaveTest(int testCmd, int testDelay, int testPayloadLength, int testCount, TXOption testTXOptions, int maxLength, int[] testNodeMask);
  // sendregion ERTT
}
