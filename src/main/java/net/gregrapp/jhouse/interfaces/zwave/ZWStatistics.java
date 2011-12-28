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

/**
 * @author Greg Rapp
 *
 */
public final class ZWStatistics
{
  // Transport layer
  // <summary>
  // 
  // </summary>
  int bytesTransmitted;
  // <summary>
  // 
  // </summary>
  int bytesReceived;
  // Frame layer
  // <summary>
  // 
  // </summary>
  int transmittedAcks;
  // <summary>
  // 
  // </summary>
  int transmittedNaks;
  // <summary>
  // 
  // </summary>
  int receivedAcks;
  // <summary>
  // 
  // </summary>
  int receivedNaks;
  // <summary>
  // 
  // </summary>
  int transmittedFrames;
  // <summary>
  // 
  // </summary>
  int receivedFrames;
  // <summary>
  // 
  // </summary>
  int retransmittedFrames;
  // <summary>
  // 
  // </summary>
  int droppedFrames;
  // Session layer
  // <summary>
  // 
  // </summary>
  int transmittedPackets;
  // <summary>
  // 
  // </summary>
  int receivedPackets;
  // <summary>
  // 
  // </summary>
  int duplicatePackets;
  // <summary>
  // 
  // </summary>
  int asyncPackets;
  // <summary>
  // 
  // </summary>
  int receiveTimeouts;
}
