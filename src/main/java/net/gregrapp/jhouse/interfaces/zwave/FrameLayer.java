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
//          Revision:         $Revision: 1.3 $
//          Last Changed:     $Date: 2006/07/24 09:14:16 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 *
 */
// <summary>
// Summary description for FrameLayer.
// </summary>
public interface FrameLayer
{
  // <summary>
  // 
  // </summary>
  void open(Transport transport);
  // <summary>
  // 
  // </summary>
  void close();

  // <summary>
  // 
  // </summary>
  boolean write(DataFrame frame);

  // <summary>
  // 
  // </summary>
  void setCallbackHandler(FrameLayerAsyncCallback handler);

  // <summary>
  // 
  // </summary>
  FrameStatistics getStatistics();

  // <summary>
  // Enable tracing
  // </summary>
  void enableTracing(boolean enable);
}


