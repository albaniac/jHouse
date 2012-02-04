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
  /**
   * @return
   */
  public boolean isReady();

  /**
   * 
   */
  public void destroy();
  
  /**
   * @param handler
   */
  public void setCallbackHandler(SessionLayerAsyncCallback handler);

  /**
   * @return
   */
  SessionStatistics getStatistics();

  /**
   * @param cmd
   * @param request
   * @return
   * @throws FrameLayerException
   */
  boolean requestWithNoResponse(DataFrame.CommandType cmd, DataPacket request)
      throws FrameLayerException;

  /**
   * @param cmd
   * @param request
   * @param sequenceCheck
   * @param timeout
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithResponse(DataFrame.CommandType cmd, DataPacket request,
      boolean sequenceCheck, int timeout) throws FrameLayerException;

  /**
   * @param cmd
   * @param request
   * @param sequenceCheck
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithResponse(DataFrame.CommandType cmd, DataPacket request,
      boolean sequenceCheck) throws FrameLayerException;

  /**
   * @param cmd
   * @param request
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithResponse(DataFrame.CommandType cmd, DataPacket request)
      throws FrameLayerException;

  /**
   * @param cmd
   * @param request
   * @param maxResponses
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithMultipleResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses) throws FrameLayerException;

  /**
   * @param cmd
   * @param request
   * @param maxResponses
   * @param sequenceCheck
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithMultipleResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses,
      boolean sequenceCheck) throws FrameLayerException;

  /**
   * @param cmd
   * @param request
   * @param maxResponses
   * @param sequenceCheck
   * @param timeout
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithMultipleResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses,
      boolean sequenceCheck,
      int timeout) throws FrameLayerException;

  /**
   * Requests a command which may give different numbers of callbacks. Supply
   * with Responses array that has room for worst case number of callbacks
   * 
   * @param cmd
   *          Z-Wave Command
   * @param request
   *          Parms for command
   * @param maxResponses
   *          Max number of responses
   * @param breakVal
   *          Values to end one
   * @param sequenceCheck
   *          If true use sequence check
   * @param timeout
   *          Timeout in ms
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithVariableResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses,
      int[] breakVal,
      boolean sequenceCheck,
      int timeout) throws FrameLayerException;

  /**
   * Requests a command which may give different numbers of callbacks. Supply
   * with Responses array that has room for worst case number of callbacks
   * 
   * @param cmd
   *          Z-Wave Command
   * @param request
   *          Parms for command
   * @param maxResponses
   *          Max number of responses
   * @param breakVal
   *          Values to end one
   * @param sequenceCheck
   *          If true use sequence check
   * @param timeout
   *          Timeout in ms
   * @return
   * @throws FrameLayerException
   */
  TXStatus requestWithVariableReturnsAndResponses(DataFrame.CommandType cmd,
      DataPacket request,
      int maxResponses,
      int[] breakVal,
      boolean sequenceCheck,
      int timeout) throws FrameLayerException;
}
