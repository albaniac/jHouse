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
//          Author:   Morten Damsgaard, Linkage A/S
//
//          Last Changed By:  $Author: jrm $
//          Revision:         $Revision: 1.5 $
//          Last Changed:     $Date: 2007/01/26 12:22:19 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import net.gregrapp.jhouse.interfaces.zwave.Constants.TXStatus;

/**
 * @author Greg Rapp
 *
 */
public interface SessionLayer
{  
  // <summary>
  // 
  // </summary>
  public boolean isReady();
    
  // <summary>
  // 
  // </summary>
  // <param name="handler"></param>
  void setCallbackHandler(SessionLayerAsyncCallback handler);
  // <summary>
  // 
  // </summary>
  // <returns></returns>
  SessionStatistics getStatistics();

  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <returns></returns>
  boolean requestWithNoResponse(DataFrame.CommandType cmd, DataPacket request) throws FrameLayerException;
  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <param name="response"></param>
  // <param name="sequenceCheck"></param>
  // <param name="timeout"></param>
  // <returns></returns>
  TXStatus requestWithResponse(DataFrame.CommandType cmd, DataPacket request, 
     boolean sequenceCheck, int timeout) throws FrameLayerException;
  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <param name="response"></param>
  // <param name="sequenceCheck"></param>
  // <returns></returns>
  TXStatus requestWithResponse(DataFrame.CommandType cmd, DataPacket request, 
     boolean sequenceCheck) throws FrameLayerException;
  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <returns></returns>
  TXStatus requestWithResponse(DataFrame.CommandType cmd, DataPacket request) throws FrameLayerException;

  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <param name="maxResponses"></param>
  // <returns></returns>
  TXStatus requestWithMultipleResponses(DataFrame.CommandType cmd,
    DataPacket request,
    int maxResponses) throws FrameLayerException;
  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <param name="maxResponses"></param>
  // <param name="sequenceCheck"></param>
  // <returns></returns>
  TXStatus requestWithMultipleResponses(DataFrame.CommandType cmd,
    DataPacket request,
    int maxResponses,
    boolean sequenceCheck) throws FrameLayerException;
  // <summary>
  // 
  // </summary>
  // <param name="cmd"></param>
  // <param name="request"></param>
  // <param name="maxResponses"></param>
  // <param name="sequenceCheck"></param>
  // <param name="timeout"></param>
  // <returns></returns>
  TXStatus requestWithMultipleResponses(DataFrame.CommandType cmd,
    DataPacket request,
    int maxResponses,
    boolean sequenceCheck,
    int timeout) throws FrameLayerException;
  // <summary>
  // Requests a command which may give different numbers of callbacks.
  // Supply with Responses array that have room for worst case number of callbacks
  // </summary>
  // <param name="cmd">Zwave Cmd</param>
  // <param name="request">Parms for command</param>
  // <param name="maxResponses">Max number of responses</param>
  // <param name="breakVal">Values to end one</param>
  // <param name="sequenceCheck">if true use sequence check</param>
  // <param name="timeout">Timeout in ms</param>
  // <returns></returns>
  TXStatus requestWithVariableResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses,
      int[] breakVal,
      boolean sequenceCheck,
 int timeout) throws FrameLayerException;
 

   // <summary>
  // Requests a command which may give different numbers of callbacks.
  // Supply with Responses array that have room for worst case number of callbacks
  // </summary>
  // <param name="cmd">Zwave Cmd</param>
  // <param name="request">Parms for command</param>
  // <param name="maxResponses">Max number of responses</param>
  // <param name="breakVal">Values to end one</param>
  // <param name="sequenceCheck">if true use sequence check</param>
  // <param name="timeout">Timeout in ms</param>
  // <returns></returns>
  TXStatus requestWithVariableReturnsAndResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses,
      int[] breakVal,
      boolean sequenceCheck,
      int timeout) throws FrameLayerException;
}
