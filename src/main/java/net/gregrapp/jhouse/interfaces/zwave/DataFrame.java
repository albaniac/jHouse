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

/**
 * A data frame
 * @author Greg Rapp
 *
 */
public class DataFrame
{
  /**
   * Data frame command IDs
   * 
   * @author Greg Rapp
   *
   */
  public enum CommandType
  {
    CmdApplicationCommandHandler(0x04),
    CmdApplicationControllerUpdate(0x49),
    CmdApplicationSlaveCommandHandler(0xa1),
    CmdClockCompare(0x32),
    CmdClockGet(0x31),
    CmdClockSet(0x30),
    CmdGetRoutingTableLine(0x80),
    CmdGetTXCounter(0x81),
    CmdLockRouteResponse(0x90),
    CmdMemoryGetBuffer(0x23),
    CmdMemoryGetByte(0x21),
    CmdMemoryGetId(0x20),
    CmdMemoryPutBuffer(0x24),
    CmdMemoryPutByte(0x22),
    CmdResetTXCounter(0x82),
    CmdRtcTimerCall(0x36),
    CmdRtcTimerCreate(0x33),
    CmdRtcTimerDelete(0x35),
    CmdRtcTimerRead(0x34),
    CmdSerialApiApplNodeInformation(0x03),
    CmdSerialApiGetCapabilities(0x07),
    CmdSerialApiGetInitData(0x02),
    CmdSerialApiSetTimeouts(0x06),
    CmdSerialApiSlaveNodeInfo(0xa0),
    CmdSerialApiSoftReset(0x08),
    CmdSerialApiTest(0x95),
    CmdStoreHomeId(0x84),
    CmdStoreNodeInfo(0x83),
    CmdTimerCall(0x73),
    CmdTimerCancel(0x72),
    CmdTimerRestart(0x71),
    CmdTimerStart(0x70),
    CmdZWaveAddNodeToNetwork(0x4a),
    CmdZWaveAssignReturnRoute(0x46),
    CmdZWaveAssignSucReturnRoute(0x51),
    CmdZWaveControllerChange(0x4d),
    CmdZWaveCreateNewPrimary(0x4c),
    CmdZWaveDeleteReturnRoute(0x47),
    CmdZWaveDeleteSucReturnRoute(0x55),
    CmdZWaveEnableSuc(0x52),
    CmdZWaveGetControllerCapabilities(0x05),
    CmdZWaveGetNodeProtocolInfo(0x41),
    CmdZWaveGetSucNodeId(0x56),
    CmdZWaveGetVersion(0x15),
    CmdZWaveGetVirtualNodes(0xa5),
    CmdZWaveIsFailedNode(0x62),
    CmdZWaveIsVirtualNode(0xa6),
    CmdZWaveRediscoveryNeeded(0x59),
    CmdZWaveRemoveFailedNodeId(0x61),
    CmdZWaveRemoveNodeFromNetwork(0x4b),
    CmdZWaveReplaceFailedNode(0x63),
    CmdZWaveReplicationCommandComplete(0x44),
    CmdZWaveReplicationSendData(0x45),
    CmdZWaveRequestNetworkUpdate(0x53),
    CmdZWaveRequestNodeInfo(0x60),
    CmdZWaveRequestNodeNeighborUpdate(0x48),
    CmdZWaveRFPowerLevelSet(0x17),
    CmdZWaveSendData(0x13),
    CmdZWaveSendDataAbort(0x16),
    CmdZWaveSendDataMeta(0x18),
    CmdZWaveSendDataMulti(0x14),
    CmdZWaveSendDataRouteDemo(0x91),
    CmdZWaveSendNodeInformation(0x12),
    CmdZWaveSendSlaveData(0xa3),
    CmdZWaveSendSlaveNodeInfo(0xa2),
    CmdZWaveSendSucId(0x57),
    CmdZWaveSetDefault(0x42),
    CmdZWaveSetLearnMode(0x50),
    CmdZWaveSetPromiscuousMode(0xd0),
    CmdZWaveSetRFReceiveMode(0x10),
    CmdZWaveSetSlaveLearnMode(0xa4),
    CmdZWaveSetSleepMode(0x11),
    CmdZWaveSetSucNodeId(0x54),
    None(0x00);

    public static CommandType getByVal(int value)
    {
      for (CommandType t : CommandType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }

    private int type;

    CommandType(int type)
    {
      this.type = type;
    }

    public int get()
    {
      return this.type;
    }
  }
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

    public static FrameType getByVal(int value)
    {
      for (FrameType t : FrameType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }

    private int type;

    FrameType(int type)
    {
      this.type = type;
    }

    public int get()
    {
      return this.type;
    }
  }
  // <summary>
  // HeaderType
  // </summary>
  public enum HeaderType
  {
    // <summary>
    // HEADER_ACK
    // </summary>
    Acknowledge(0x06),
    // <summary>
    // HEADER_CAN
    // </summary>
    Can(0x18),
    // <summary>
    // HEADER_NAK
    // </summary>
    NotAcknowledged(0x15),
    // <summary>
    // HEADER_SOF
    // </summary>
    StartOfFrame(0x01),
    // <summary>
    //
    // </summary>
    Unknown(0x00);

    public static HeaderType getByVal(int value)
    {
      for (HeaderType t : HeaderType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return null;
    }

    private int type;

    HeaderType(int type)
    {
      this.type = type;
    }

    public int get()
    {
      return this.type;
    }
  }
  private static final int BUFFER_SIZE = 100;
  private static final int IDX_CMD = 3;

  private static final int IDX_DATA = 4;
  private static final int IDX_HEADER = 0;

  private static final int IDX_LENGTH = 1;

  private static final int IDX_TYPE = 2;

  private CommandType cmd;

  private int[] payloadBuffer;

  private int payloadIdx;

  private Calendar timestamp;

  // <summary>
  // If true ToString() returns DataFrame command as the defined Enum
  // If false the numerical value is returned
  // </summary>
  public boolean toStringShowCmdAsEnum = true;

  private FrameType type;

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
  public boolean addPayload(int data)
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
  // Command
  // </summary>
  public CommandType getCommand()
  {
    return cmd;
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

  // <summary>
  // Type
  // </summary>
  public FrameType getFrameType()
  {
    return type;
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
  // TimeStamp
  // </summary>
  public Calendar getTimestamp()
  {
    return timestamp;
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
  // isPayloadFull()
  // </summary>
  // <returns></returns>
  public boolean isPayloadFull()
  {
    return payloadIdx > payloadBuffer.length - 1;
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
  public void setFrameType(FrameType value)
  {
    this.type = value;
  }

  // <summary>
  // String
  // </summary>
  // <returns></returns>
  @Override
  public String toString()
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
}
