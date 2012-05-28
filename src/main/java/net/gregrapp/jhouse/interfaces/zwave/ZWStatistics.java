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
  int bytesTransmitted;

  int bytesReceived;

  // Frame layer
  int transmittedAcks;

  int transmittedNaks;

  int receivedAcks;

  int receivedNaks;

  int transmittedFrames;

  int receivedFrames;

  int retransmittedFrames;

  int droppedFrames;

  // Session layer
  int transmittedPackets;

  int receivedPackets;

  int duplicatePackets;

  int asyncPackets;

  int receiveTimeouts;
}
