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

import java.util.List;

import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.ControllerCapabilities;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.MemoryGetId;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.NodeMask;
import net.gregrapp.jhouse.interfaces.zwave.ApplicationLayerImpl.SerialApiCapabilities;
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

/**
 * @author Greg Rapp
 * 
 */
public interface ApplicationLayer
{
  /**
   * 
   */
  public void destroy();

  /**
   * Returns a copy of the node list stored in the DLL (does not read from
   * Z-Wave module)
   * 
   * @return Copy of node list
   */
  Node[] getAllNodes();

  /**
   * Returns the Z-Wave Chip revision
   * 
   * @return the Z-Wave Chip revision
   */
  int getChipRev();

  /**
   * Returns the Z-Wave chip type
   * 
   * @return the Z-Wave {@link ChipType}
   */
  ChipType getChipType();

  /**
   * Get this controller's node ID
   * 
   * @return node ID
   */
  public int getControllerNodeId();

  // <summary>
  //
  // </summary>
  // <returns></returns>
  FrameLayer getFrameLayer();

  // <summary>
  //
  // </summary>
  Library getLibraryType();

  // <summary>
  //
  // </summary>
  String getLibraryVersion();

  /**
   * Returns the controllers home ID and node ID
   * 
   * @return an instance of {@link MemoryGetId} which contains the controller's
   *         home ID and node ID
   */
  public MemoryGetId getMemoryGetId();

  // <summary>
  // Gets the node capabilities from the Z-Wave.dll list (does not read from
  // Z-Wave module)
  // </summary>
  // <param name="nodeId">nodeID</param>
  // <returns>null if unkown ZWNode if it exist locally</returns>
  Node getNode(int nodeId);

  // <summary>
  // Returns the numeber of nodes in the Z-Wave.dll copy of the nodetable.
  // </summary>
  // <returns>number of nodes</returns>
  // int GetNodeCount();
  int getNodeCount();

  /**
   * Get routing table from node
   * 
   * @param nodeId
   * @param removeBadRepeaters
   * @param removeNonRepeatingDevices
   * @return
   * @throws FrameLayerException
   * @throws ApplicationLayerException
   */
  NodeMask getRoutingTableLine(int nodeId, boolean removeBadRepeaters,
      boolean removeNonRepeatingDevices) throws FrameLayerException,
      ApplicationLayerException;

  /**
   * Returns the Z-Wave SerialAPI version
   * 
   * @return
   */
  public int getSerialApiVersion();

  // <summary>
  //
  // </summary>
  // <returns></returns>
  SessionLayer getSessionLayer();

  // <summary>
  // Returns statistics about the communication
  // </summary>
  // <returns>Read/write/error statistics</returns>
  ZWStatistics getStatistics();

  /**
   * Returns a comma separated string which contains a list of commands
   * supported by embedded Z-Wave module. Unhandled commands are returned as the
   * command number (base 10)
   * 
   * @return <code>String</code> of command names
   */
  String getSupportedSerialCmds();

  /**
   * @return <code>true</code> if this controller is the real primary
   */
  boolean isControllerIsRealPrimary();

  /**
   * @return <code>true</code> if this controller is the SIS
   */
  boolean isControllerIsSis();

  /**
   * @return <code>true</code> if this controller is the SUC
   */
  boolean isControllerIsSuc();

  /**
   * @return <code>true</code> if this controller is on other network
   */
  boolean isControllerOnOtherNetwork();

  /**
   * @return <code>true</code> if a SIS is available
   */
  boolean isNodeIdServerPresent();

  // <summary>
  // Simply check if the node Id is in the Z-Wave.dll copy of the nodetable
  // </summary>
  // <param name="id">id to look for</param>
  // <returns>true if it exist false if not</returns>
  boolean isNodePresent(int id);

  // <summary>
  //
  // </summary>
  boolean isRealTimeSystem();

  /**
   * @return <code>true</code> if this is a slave controller
   */
  public boolean isSlaveController();

  // <summary>
  // Checks if a given byte command ID is supported by embedded module
  // </summary>
  // <param name="CommandId">id number to check</param>
  // <returns>true if supported, false if not</returns>
  boolean isSupportedSerialCmd(int CommandId);

  public void setCallbackHandler(ApplicationLayerAsyncCallback handler);

  // <summary>
  // Enable Add node function
  // </summary>
  // <param name="mode">SLAVE/CONTROLLER/ANY</param>
  // <returns></returns>
  NodeStatus zwaveAddNodeToNetwork(Mode mode) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Assigns Return routes to a routing slave
  // </summary>
  // <param name="sourceNodeId">Node to receive the routes</param>
  // <param name="destinationNodeId">destination of the route</param>
  // <returns></returns>
  TXStatus zwaveAssignReturnRoute(int sourceNodeId, int destinationNodeId)
      throws FrameLayerException;

  // <summary>
  // Force the controller to assign SUC/SIS routes
  // </summary>
  // <param name="destinationNodeId"></param>
  // <returns></returns>
  TXStatus zwaveAssignSucReturnRoute(int destinationNodeId)
      throws FrameLayerException;

  // <summary>
  // Enable this controller to hand over its primary status
  // </summary>
  // <param name="mode">ControllerChangeMode START/STOP/FAILED</param>
  // <returns>Add node state</returns>
  NodeStatus zwaveControllerChange(ControllerChangeMode mode)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Enable this controller to create a new primary. Only valid for SUC
  // </summary>
  // <param name="mode">START/STOP/FAILED</param>
  // <returns></returns>
  NodeStatus zwaveCreateNewPrimaryCtrl(CreateNewPrimaryControllerMode mode)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Deletes the return routes assigned to a Routing Node
  // </summary>
  // <param name="sourceNodeId">Node to delete routes for</param>
  // <returns></returns>
  TXStatus zwaveDeleteReturnRoute(int sourceNodeId) throws FrameLayerException;

  // <summary>
  // Deletes the SUC return routes on a routing slave
  // </summary>
  // <param name="sourceNodeId">Node to delete SUC return routes</param>
  // <returns></returns>
  TXStatus zwaveDeleteSucReturnRoute(int sourceNodeId)
      throws FrameLayerException;

  // <summary>
  // Enables SUC for this node
  // </summary>
  // <param name="enable">true/false</param>
  // <param name="capabilities"></param>
  // <returns>true if success false if not</returns>
  boolean zwaveEnableSuc(boolean enable, int capabilities)
      throws FrameLayerException;

  /**
   * Update the controller capabilities and return a list of nodes as read from
   * the Z-Wave module
   * 
   * @return array of {@link Node}s
   * @throws FrameLayerException
   * @throws ApplicationLayerException
   */
  Node[] zwaveEnumerateNodes() throws FrameLayerException,
      ApplicationLayerException;

  /**
   * Get the Zwave controller capabilities and current configuration
   * 
   * @return an instance of {@link ControllerCapabilities}
   * @throws ApplicationLayerException
   * @throws FrameLayerException
   */
  ControllerCapabilities zwaveGetControllerCapabilities()
      throws ApplicationLayerException, FrameLayerException;

  // <summary>
  // Reads the devicetype from the Z-Wave module
  // </summary>
  // <param name="nodeId">Node ID to get info for</param>
  // <returns></returns>
  Node zwaveGetNodeProtocolInfo(int nodeId) throws FrameLayerException,
      ApplicationLayerException;

  Node zwaveGetNodeProtocolInfo(int nodeId, boolean checkIfVirtual)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Get the nodeId of the node that is currently assigned SUC or SIS 0 if none
  // </summary>
  // <returns></returns>
  int zwaveGetSucNodeId() throws FrameLayerException, ApplicationLayerException;

  /**
   * Queries the controller for its the library type and version
   * 
   * @return Instance of {@link VersionInfoType}
   * @throws FrameLayerException
   * @throws ApplicationLayerException
   */
  public VersionInfoType zwaveGetVersion() throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Check if a node ID is registered as failed
  // </summary>
  // <param name="nodeId"></param>
  // <returns>true/false</returns>
  boolean zwaveIsFailedNode(int nodeId) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // ZWaveIsVirtualNode(byte nodeID)
  // </summary>
  // <param name="nodeId"></param>
  // <returns></returns>
  boolean zwaveIsVirtualNode(int nodeId) throws FrameLayerException,
      ApplicationLayerException;

  // endregion ZWaveBRIDGE
  // <summary>
  // Lock a response route to the nodeId supplied
  // </summary>
  // <param name="nodeId"></param>
  void zwaveLockRoutes(int nodeId) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Reads the number of bytes from ZWaveModule nonvolatile memory
  // </summary>
  // <param name="offset">Start offset adress</param>
  // <param name="len">number of bytes to read</param>
  // <returns>array of read values</returns>
  int[] zwaveMemoryGetBuffer(long offset, int len) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Reads the byte located at the offset specified
  // </summary>
  // <param name="offset"></param>
  // <returns></returns>
  int zwaveMemoryGetByte(long offset) throws FrameLayerException,
      ApplicationLayerException;

  /**
   * Query the Z-Wave controller for its home ID and node ID
   * 
   * @return instance of {@link MemoryGetId}
   * @throws FrameLayerException
   * @throws ApplicationLayerException
   */
  MemoryGetId zwaveMemoryGetId() throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Stores the specified buffer in ZWaveModule Nonvolatile memory
  // </summary>
  // <param name="offset">Application offset start address</param>
  // <param name="buffer">Buffer to write</param>`
  // <param name="length">number of bytes to write</param>
  void zwaveMemoryPutBuffer(long offset, int[] buffer, long length)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Store the specified byte at the offset adress specified
  // </summary>
  // <param name="offset">adress</param>
  // <param name="value">value to write</param>
  // <returns></returns>
  TXStatus zwaveMemoryPutByte(long offset, int value)
      throws FrameLayerException;

  /**
   * Request manufacturer specific info from a node
   * 
   * @param nodeId
   *          Z-Wave node ID
   * @throws ApplicationLayerException
   * @throws FrameLayerException
   */
  public void zwaveNodeManufacturerSpecific(int nodeId)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Z-wave rediscovery needed.
  // </summary>
  // <param name="nodeId">The node id.</param>
  // <returns></returns>
  ZWaveRediscoveryNeededReturnValue zwaveRediscoveryNeeded(int nodeId)
      throws FrameLayerException;

  // <summary>
  // Request the Z-Wave module to remove a failing node from the network
  // </summary>
  // <param name="nodeId">nodeId to remove</param>
  // <returns>status</returns>
  DataPacket[] zwaveRemoveFailedNodeId(int nodeId) throws FrameLayerException;

  // <summary>
  // Enables controller to remove nodes from the network
  // </summary>
  // <param name="mode">SLAVE/CONTROLLER/ANY/STOP</param>
  // <returns>status</returns>
  NodeStatus zwaveRemoveNodeFromNetwork(Mode mode) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Request teh Z-Wave module to replace a failing node in the network
  // </summary>
  // <param name="nodeId">node Id to try and replace</param>
  // <returns></returns>
  boolean zwaveReplaceFailedNode(int nodeId) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Sends a command complete. This should be used as a response to
  // ReplicationSend
  // </summary>
  void zwaveReplicationReceiveComplete() throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Only used during replication to transfer application information. See
  // programming guide.
  // Used when the controller is in replication mode. It sends the payload and
  // expects the receiver to respond with a command complete message
  // (ZWaveREPLICATION_COMMAND_COMPLETE). Messages sent using this command
  // should always
  // be part of the Z-Wave controller replication command class.
  // </summary>
  // <param name="nodeId">destination</param>
  // <param name="data">payload</param>
  // <param name="txOptions">transmit options</param>
  // <returns></returns>
  TXStatus zwaveReplicationSend(int nodeId, int[] data, TXOption[] txOptions)
      throws FrameLayerException;

  // <summary>
  // Request network update from the SUC/SIS
  // </summary>
  // <returns></returns>
  TXStatus zwaveRequestNetworkUpdate() throws FrameLayerException;

  // <summary>
  // Request nodeinformation frame from nodeId
  // </summary>
  // <param name="nodeId"></param>
  // <returns></returns>
  TXStatus zwaveRequestNodeInfo(int nodeId) throws FrameLayerException;

  // <summary>
  // Request that a node updates its neighbours
  // </summary>
  // <param name="nodeId"></param>
  // <returns></returns>
  RequestNeighbor zwaveRequestNodeNeighborUpdate(int nodeId)
      throws FrameLayerException;

  // <summary>
  //
  // </summary>
  // <param name="powerLevel"></param>
  // <returns>The RF powerlevel set</returns>
  int zwaveRFPowerLevelSet(int powerLevel) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  //
  // </summary>
  // <param name="nodeId"></param>
  // <param name="data"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendData(int nodeId, int[] data, TXOption[] txOptions)
      throws FrameLayerException;

  // <summary>
  // Transmit a frame to the node ID specified. If 0xFF is specified the frame
  // is broadcasted
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">Transmit options (TRANSMIT_OPTION_xxx)</param>
  // <param name="timeout">Response timeout in ms</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendData(int nodeId, int[] data, TXOption[] txOptions,
      int timeout) throws FrameLayerException;

  // <summary>
  // Abort application initiated transmissions started by calling ZWaveSendData
  // </summary>
  void zwaveSendDataAbort() throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Transmita meta data frame to nodeID.
  // </summary>
  // <param name="nodeId"></param>
  // <param name="data"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption[] txOptions)
      throws FrameLayerException;

  // <summary>
  // Transmit a frame to the node ID specified. If 0xFF is specified the frame
  // is broadcasted
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">Transmit options (TRANSMIT_OPTION_xxx)</param>
  // <param name="timeout">Response timeout in ms</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendDataMeta(int nodeId, int[] data, TXOption[] txOptions,
      int timeout) throws FrameLayerException;

  // <summary>
  // Transmit a frame to the node IDs specified. If 0xFF is specified the frame
  // is broadcasted
  // </summary>
  // <param name="nodeIdList">list of nodes to send to</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">if TRANSMIT_OPTION_ACK is specified each node gets
  // a singlecast</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendDataMulti(List<Integer> nodeIdList, int[] data,
      TXOption[] txOptions) throws FrameLayerException;

  // <summary>
  // Send out this device Node information Frame
  // </summary>
  // <param name="destination">Destination node</param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendNodeInformation(int destination, TXOption[] txOptions)
      throws FrameLayerException;

  // <summary>
  //
  // </summary>
  // <param name="sourceId"></param>
  // <param name="destinationId"></param>
  // <param name="data"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendSlaveData(int sourceId, int destinationId, int[] data,
      TXOption[] txOptions) throws FrameLayerException;

  // <summary>
  // Transmit a frame to the node ID specified. If 0xFF is specified the frame
  // is broadcasted
  // </summary>
  // <param name="sourceId">Source node</param>
  // <param name="destinationId">Destination node</param>
  // <param name="data">payload to send</param>
  // <param name="txOptions">Transmit options (TRANSMIT_OPTION_xxx)</param>
  // <param name="timeout">Response timeout in ms</param>
  // <returns>Transmit Result</returns>
  TXStatus zwaveSendSlaveData(int sourceId, int destinationId, int[] data,
      TXOption[] txOptions, int timeout) throws FrameLayerException;

  // <summary>
  // ZWaveSendSlaveNodeInformation
  // </summary>
  // <param name="sourceId"></param>
  // <param name="destinationId"></param>
  // <param name="txOptions"></param>
  // <returns></returns>
  TXStatus zwaveSendSlaveNodeInformation(int sourceId, int destinationId,
      TXOption[] txOptions) throws FrameLayerException;

  // region ZWaveBRIDGE
  // <summary>
  // ZWaveSendSlaveNodeInformation with timeout
  // </summary>
  // <param name="sourceId"></param>
  // <param name="destinationId"></param>
  // <param name="txOptions"></param>
  // <param name="timeout"></param>
  // <returns></returns>
  TXStatus zwaveSendSlaveNodeInformation(int sourceId, int destinationId,
      TXOption[] txOptions, int timeout) throws FrameLayerException;

  // <summary>
  // Transmits the SUC node ID to the node specified
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="txOptions">Option</param>
  // <returns>Transmit status</returns>
  TXStatus zwaveSendSucId(int nodeId, TXOption[] txOptions)
      throws FrameLayerException;

  // <summary>
  // Get the Capabilities of the Z-Wave module serialAPI (supported functions)
  // </summary>
  // <returns>bitmask of supported capabilities</returns>
  SerialApiCapabilities zwaveSerialApiGetCapabilities()
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Set the Serial communication timeouts in 10ms
  // </summary>
  // <param name="acknowledgeTimeout">Timeout in 10ms waiting for ACK</param>
  // <param name="timeout">Timeout in 10ms waiting for another byte in
  // frame</param>
  // <returns></returns>
  int[] zwaveSerialApiSetTimeout(int acknowledgeTimeout, int timeout)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Make Z-Wave module do a software reset
  // </summary>
  void zwaveSerialApiSoftReset() throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Reset all protocol data back to factory default (incl home and nodeID)
  // </summary>
  void zwaveSetDefault() throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // Enable/disabl this device to be added to or removed from a network
  // </summary>
  // <param name="learnMode"></param>
  // <returns></returns>
  boolean zwaveSetLearnMode(boolean learnMode) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Setup this nodes Nodeinformation
  // </summary>
  // <param name="listening"></param>
  // <param name="generic">Generic devicetype</param>
  // <param name="specific">Specific devicetype</param>
  // <param name="nodeParameter">Supported commandclasses</param>
  void zwaveSetNodeInformation(int listening, int generic, int specific,
      int[] nodeParameter) throws FrameLayerException,
      ApplicationLayerException;

  // region ZWaveINSTALLER
  // <summary>
  // enable / disable the installer lib promiscuous mode
  // </summary>
  // <param name="enable"></param>
  // <returns></returns>
  boolean zwaveSetPromiscuousMode(boolean enable) throws FrameLayerException,
      ApplicationLayerException;

  // endregion

  // endregion
  // <summary>
  // Enable or disable RF on the Z-Wave module.
  // </summary>
  // <param name="mode"></param>
  void zwaveSetRFReceiveMode(int mode) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Used when a Static controller wants to become SUC itself.
  // </summary>
  // <param name="sucState">true become SUC, false disable SUC</param>
  // <param name="capabilities"></param>
  // <param name="suc">Status</param>
  // <returns></returns>
  int zwaveSetSelfAsSuc(boolean sucState, int capabilities)
      throws FrameLayerException, ApplicationLayerException;

  // <summary>
  // ZWaveSetSlaveLearnMode(byte node, byte mode)
  // </summary>
  // <param name="node"></param>
  // <param name="mode"></param>
  // <returns></returns>
  boolean zwaveSetSlaveLearnMode(int node, int mode) throws FrameLayerException;

  // <summary>
  // ZWaveSetSlaveNodeInformation(byte nodeID, byte listening, byte generic,
  // byte specific, byte[] nodeParm)
  // </summary>
  // <param name="nodeId"></param>
  // <param name="listening"></param>
  // <param name="generic"></param>
  // <param name="specific"></param>
  // <param name="nodeParameter"></param>
  void zwaveSetSlaveNodeInformation(int nodeId, int listening, int generic,
      int specific, int[] nodeParameter) throws FrameLayerException,
      ApplicationLayerException;

  // <summary>
  // Assign or remove SUC on another controller in the network
  // </summary>
  // <param name="nodeId">Destination node</param>
  // <param name="sucState"></param>
  // <param name="txOptions"></param>
  // <param name="capabilities"></param>
  // <returns></returns>
  boolean zwaveSetSucNodeId(int nodeId, boolean sucState, TXOption[] txOptions,
      int capabilities) throws FrameLayerException;

  // <summary>
  // Disables the learn mode
  // </summary>
  void zwaveStopLearnMode() throws FrameLayerException,
      ApplicationLayerException;

  /**
   * Store the HomeId on the Controller in the external EEPROM
   * <p>
   * Only available on Installer Z-Wave modules
   * 
   * @param homeId
   * @param nodeId
   * @return <code>true</code> if successful
   * @throws FrameLayerException
   */
  boolean zwaveStoreHomeId(int homeId, int nodeId) throws FrameLayerException;

  /**
   * Stores the node information for a specific node in the external EEPROM
   * <p>
   * Only available on Installer Z-Wave modules
   * 
   * @param nodeId
   *          Z-Wave node ID
   * @param nodeInfo
   *          {@link Node} instance
   * @return an instance of {@link TXStatus}
   * @throws FrameLayerException
   */
  TXStatus zwaveStoreNodeInfo(int nodeId, Node nodeInfo)
      throws FrameLayerException;

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
  int zwaveTest(int testCmd, int testDelay, int testPayloadLength,
      int testCount, TXOption[] testTXOptions, int maxLength, int[] testNodeMask)
      throws FrameLayerException;

}
