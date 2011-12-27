//////////////////////////////////////////////////////////////// 
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
//          Author:   Morten Damsgaard, Linkage A/S
//
//          Last Changed By:  $Author: heh $
//          Revision:         $Revision: 1.7 $
//          Last Changed:     $Date: 2007/02/26 08:17:36 $
//
///////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.Calendar;

/**
 * @author Greg Rapp
 * 
 */

// <summary>
// DataFrame
// </summary>
public class DataFrame
{
  private int[] payloadBuffer;
  private int payloadIdx;
  private Calendar timestamp;
  private FrameType type;
  private CommandType cmd;

  // <summary>
  // If true ToString() returns DataFrame command as the defined Enum
  // If false the numerical value is returned
  // </summary>
  public boolean toStringShowCmdAsEnum = true;
  private static final int BUFFER_SIZE = 100;

  // <summary>
  // DataFrame()
  // </summary>
  public DataFrame()
  {
    this.timestamp = Calendar.getInstance();
    this.payloadBuffer = new int[BUFFER_SIZE];
  }

  // <summary>
  // DataFrame(int payloadSize)
  // </summary>
  // <param name="payloadSize"></param>
  public DataFrame(int payloadSize)
  {
    this.timestamp = Calendar.getInstance();
    this.payloadBuffer = new int[payloadSize];
  }

  // <summary>
  // AddPayload(int data)
  // </summary>
  // <param name="data"></param>
  // <returns></returns>
  public boolean AddPayload(int data)
  {
    if (payloadIdx > payloadBuffer.length - 1)
      return false;
    payloadBuffer[payloadIdx++] = data;
    return true;
  }

  // <summary>
  // AddPayload(int[] data)
  // </summary>
  // <param name="data"></param>
  // <returns></returns>
  public boolean addPayload(int[] data)
  {
    if (data == null)
    {
      throw new NullPointerException("data");
    }
    if ((payloadIdx + data.length) > payloadBuffer.length - 1)
      return false;
    System.arraycopy(data, 0, payloadBuffer, payloadIdx, data.length);
    payloadIdx += (int) data.length;
    return true;
  }

  // <summary>
  // getPayloadBuffer()
  // </summary>
  // <returns></returns>
  public int[] getPayloadBuffer()
  {
    return payloadBuffer;
  }

  // <summary>
  // Command
  // </summary>
  public CommandType getCommand()
  {
    return cmd;
  }

  // <summary>
  // Command
  // </summary>
  public void setCommand(CommandType value)
  {
    this.cmd = value;
  }

  // <summary>
  // Type
  // </summary>
  public FrameType getFrameType()
  {
    return type;
  }

  // <summary>
  // Type
  // </summary>
  public void setFrameType(FrameType value)
  {
    this.type = value;
  }

  // <summary>
  // isPayloadFull()
  // </summary>
  // <returns></returns>
  public boolean isPayloadFull()
  {
    return payloadIdx > payloadBuffer.length - 1;
  }

  // <summary>
  // isChecksumValid(int checksum)
  // </summary>
  // <param name="checksum"></param>
  // <returns></returns>
  public boolean isChecksumValid(int checksum)
  {
    return calculateChecksum() == checksum;
  }

  // <summary>
  // String
  // </summary>
  // <returns></returns>
  public String ToString()
  {
    StringBuilder sb = new StringBuilder(100);

    // Header...
    sb.append((int) HeaderType.StartOfFrame.get());
    sb.append(' ');

    // Length...
    sb.append((int) (payloadIdx + 3));
    sb.append(' ');

    // Type...
    sb.append((int) type.get());
    sb.append(' ');

    // Command...
    if (toStringShowCmdAsEnum)
    {
      sb.append(cmd.toString());
    } else
    {
      sb.append((int) cmd.get());
    }
    sb.append(' ');

    // Data payload...
    for (int i = 0; i < payloadIdx; i++)
    {
      sb.append(payloadBuffer[i]);
      sb.append(' ');
    }

    // Checksum...
    sb.append(calculateChecksum());

    return sb.toString();
  }

  // <summary>
  // GetFrameBuffer()
  // </summary>
  // <returns></returns>
  public int[] getFrameBuffer()
  {
    int[] buffer = new int[payloadIdx + 5];
    buffer[IDX_HEADER] = (int) HeaderType.StartOfFrame.get();
    buffer[IDX_LENGTH] = (int) (payloadIdx + 3);
    buffer[IDX_TYPE] = (int) type.get();
    buffer[IDX_CMD] = (int) cmd.get();
    System.arraycopy(payloadBuffer, 0, buffer, IDX_DATA, payloadIdx);
    buffer[buffer.length - 1] = calculateChecksum();
    return buffer;
  }

  private int calculateChecksum()
  {
    int calcChksum = 0xFF;
    calcChksum ^= (int) (payloadIdx + 3); // Length
    calcChksum ^= (int) type.get(); // Type
    calcChksum ^= (int) cmd.get(); // Command
    for (int i = 0; i < payloadIdx; i++)
      calcChksum ^= payloadBuffer[i]; // Data
    return calcChksum;
  }

  // <summary>
  // TimeStamp
  // </summary>
  public Calendar getTimestamp()
  {
    return timestamp;
  }

  // <summary>
  //
  // </summary>
  // <param name="df1"></param>
  // <param name="df2"></param>
  // <returns></returns>
  public boolean equals(DataFrame df1, DataFrame df2)
  {
    if (!(df1 instanceof DataFrame) || !(df2 instanceof DataFrame))
      return false;
    if (df1.getFrameType() != df2.getFrameType())
      return false;
    if (df1.getCommand() != df2.getCommand())
      return false;
    if (df1.payloadIdx != df2.payloadIdx)
      return false;
    for (int i = 0; i < df1.payloadIdx; i++)
      if (df1.payloadBuffer[i] != df2.payloadBuffer[i])
        return false;
    return true;
  }

  // <summary>
  // HeaderType
  // </summary>
  public enum HeaderType
  {
    // <summary>
    //
    // </summary>
    Unknown(0x00),
    // <summary>
    // HEADER_SOF
    // </summary>
    StartOfFrame(0x01),
    // <summary>
    // HEADER_ACK
    // </summary>
    Acknowledge(0x06),
    // <summary>
    // HEADER_NAK
    // </summary>
    NotAcknowledged(0x15),
    // <summary>
    // HEADER_CAN
    // </summary>
    Can(0x18);

    private int type;

    HeaderType(int type)
    {
      this.type = type;
    }

    public int get()
    {
      return this.type;
    }

    public static HeaderType getByVal(int value)
    {
      for (HeaderType t : HeaderType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }
  }

  private static final int IDX_HEADER = 0;
  private static final int IDX_LENGTH = 1;
  private static final int IDX_TYPE = 2;
  private static final int IDX_CMD = 3;
  private static final int IDX_DATA = 4;

  // <summary>
  // FrameType
  // </summary>
  public enum FrameType
  {
    // <summary>
    // FT_REQUEST
    // </summary>
    Request(0x00),
    // <summary>
    // FT_RESPONSE
    // </summary>
    Response(0x01);

    private int type;

    FrameType(int type)
    {
      this.type = type;
    }

    public int get()
    {
      return this.type;
    }

    public static FrameType getByVal(int value)
    {
      for (FrameType t : FrameType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }
  }

  // <summary>
  // Data frame command IDs
  // </summary>
  //
  public enum CommandType
  {
    // <summary>
    //
    // </summary>
    None(0x00),
    // <summary>
    //
    // </summary>
    CmdSerialApiGetInitData(0x02),
    // <summary>
    //
    // </summary>
    CmdSerialApiApplNodeInformation(0x03),
    // <summary>
    //
    // </summary>
    CmdApplicationCommandHandler(0x04),
    // <summary>
    //
    // </summary>
    CmdZWaveGetControllerCapabilities(0x05),
    // SERIALAPI VER. 4 added START
    // <summary>
    //
    // </summary>
    CmdSerialApiSetTimeouts(0x06),
    // <summary>
    //
    // </summary>
    CmdSerialApiGetCapabilities(0x07),
    // <summary>
    //
    // </summary>
    CmdSerialApiSoftReset(0x08),
    // SERIALAPI VER. 4 added END
    // <summary>
    //
    // </summary>
    CmdZWaveSetRFReceiveMode(0x10),
    // <summary>
    //
    // </summary>
    CmdZWaveSetSleepMode(0x11),
    // <summary>
    //
    // </summary>
    CmdZWaveSendNodeInformation(0x12),
    // <summary>
    //
    // </summary>
    CmdZWaveSendData(0x13),
    // <summary>
    //
    // </summary>
    CmdZWaveSendDataMulti(0x14),
    // <summary>
    //
    // </summary>
    // CMD_ZWaveGET_VERSION = 0x15),
    CmdZWaveGetVersion(0x15),
    // SERIALAPI VER. 4 added START
    // <summary>
    //
    // </summary>
    CmdZWaveSendDataAbort(0x16),
    // <summary>
    //
    // </summary>
    CmdZWaveRFPowerLevelSet(0x17),
    // <summary>
    //
    // </summary>
    CmdZWaveSendDataMeta(0x18),
    // SERIALAPI VER. 4 added END
    // <summary>
    //
    // </summary>
    CmdMemoryGetId(0x20),
    // <summary>
    //
    // </summary>
    CmdMemoryGetByte(0x21),
    // <summary>
    //
    // </summary>
    CmdMemoryPutByte(0x22),
    // <summary>
    //
    // </summary>
    CmdMemoryGetBuffer(0x23),
    // <summary>
    //
    // </summary>
    CmdMemoryPutBuffer(0x24),
    // <summary>
    //
    // </summary>
    CmdClockSet(0x30),
    // <summary>
    //
    // </summary>
    CmdClockGet(0x31),
    // <summary>
    //
    // </summary>
    CmdClockCompare(0x32),
    // <summary>
    //
    // </summary>
    CmdRtcTimerCreate(0x33),
    // <summary>
    //
    // </summary>
    CmdRtcTimerRead(0x34),
    // <summary>
    //
    // </summary>
    CmdRtcTimerDelete(0x35),
    // <summary>
    //
    // </summary>
    CmdRtcTimerCall(0x36),
    // <summary>
    //
    // </summary>
    CmdZWaveGetNodeProtocolInfo(0x41),
    // <summary>
    //
    // </summary>
    CmdZWaveSetDefault(0x42),
    // <summary>
    //
    // </summary>
    CmdZWaveReplicationCommandComplete(0x44),
    // <summary>
    //
    // </summary>
    CmdZWaveReplicationSendData(0x45),
    // <summary>
    //
    // </summary>
    CmdZWaveAssignReturnRoute(0x46),
    // <summary>
    //
    // </summary>
    CmdZWaveDeleteReturnRoute(0x47),
    // <summary>
    //
    // </summary>
    CmdZWaveRequestNodeNeighborUpdate(0x48),
    // <summary>
    //
    // </summary>
    CmdApplicationControllerUpdate(0x49),
    // Slave now also has the Update functionality
    // CMD_APPLICATION_UPDATE = 0x49),
    // <summary>
    //
    // </summary>
    CmdZWaveAddNodeToNetwork(0x4a),
    // <summary>
    //
    // </summary>
    CmdZWaveRemoveNodeFromNetwork(0x4b),
    // <summary>
    //
    // </summary>
    CmdZWaveCreateNewPrimary(0x4c),
    // <summary>
    //
    // </summary>
    CmdZWaveControllerChange(0x4d),
    // <summary>
    //
    // </summary>
    CmdZWaveSetLearnMode(0x50),
    // <summary>
    //
    // </summary>
    CmdZWaveAssignSucReturnRoute(0x51),
    // <summary>
    //
    // </summary>
    CmdZWaveEnableSuc(0x52),
    // <summary>
    //
    // </summary>
    CmdZWaveRequestNetworkUpdate(0x53),
    // <summary>
    //
    // </summary>
    CmdZWaveSetSucNodeId(0x54),
    // <summary>
    //
    // </summary>
    CmdZWaveDeleteSucReturnRoute(0x55),
    // <summary>
    //
    // </summary>
    CmdZWaveGetSucNodeId(0x56),
    // <summary>
    //
    // </summary>
    CmdZWaveSendSucId(0x57),

    // <summary>
    // Rediscovery needed call
    // </summary>
    CmdZWaveRediscoveryNeeded(0x59),
    // <summary>
    //
    // </summary>
    CmdZWaveRequestNodeInfo(0x60),
    // <summary>
    //
    // </summary>
    CmdZWaveRemoveFailedNodeId(0x61),
    // <summary>
    //
    // </summary>
    CmdZWaveIsFailedNode(0x62),
    // <summary>
    //
    // </summary>
    CmdZWaveReplaceFailedNode(0x63),
    // <summary>
    //
    // </summary>
    CmdTimerStart(0x70),
    // <summary>
    //
    // </summary>
    CmdTimerRestart(0x71),
    // <summary>
    //
    // </summary>
    CmdTimerCancel(0x72),
    // <summary>
    //
    // </summary>
    CmdTimerCall(0x73),
    // <summary>
    //
    // </summary>
    CmdGetRoutingTableLine(0x80),
    // <summary>
    //
    // </summary>
    CmdGetTXCounter(0x81),
    // <summary>
    //
    // </summary>
    CmdResetTXCounter(0x82),
    // <summary>
    //
    // </summary>
    CmdStoreNodeInfo(0x83),
    // <summary>
    //
    // </summary>
    CmdStoreHomeId(0x84),
    // <summary>
    //
    // </summary>
    CmdLockRouteResponse(0x90),
    // <summary>
    //
    // </summary>
    CmdZWaveSendDataRouteDemo(0x91),
    // <summary>
    //
    // </summary>
    CmdSerialApiTest(0x95),
    // <summary>
    //
    // </summary>
    CmdSerialApiSlaveNodeInfo(0xa0),
    // <summary>
    //
    // </summary>
    CmdApplicationSlaveCommandHandler(0xa1),
    // <summary>
    //
    // </summary>
    CmdZWaveSendSlaveNodeInfo(0xa2),
    // <summary>
    //
    // </summary>
    CmdZWaveSendSlaveData(0xa3),
    // <summary>
    //
    // </summary>
    CmdZWaveSetSlaveLearnMode(0xa4),
    // <summary>
    //
    // </summary>
    CmdZWaveGetVirtualNodes(0xa5),
    // <summary>
    //
    // </summary>
    CmdZWaveIsVirtualNode(0xa6),
    // <summary>
    //
    // </summary>
    CmdZWaveSetPromiscuousMode(0xd0);

    private int type;

    CommandType(int type)
    {
      this.type = type;
    }

    public int get()
    {
      return this.type;
    }

    public static CommandType getByVal(int value)
    {
      for (CommandType t : CommandType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }
  }
}
