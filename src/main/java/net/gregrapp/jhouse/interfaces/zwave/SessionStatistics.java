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

/**
 * @author Greg Rapp
 * 
 */
public class SessionStatistics
{
  public SessionStatistics()
  {
  }

  public SessionStatistics(SessionStatistics stats)
  {
    if (stats == null)
    {
      throw new NullPointerException("stats");
    }
    this.transmittedPackets = stats.transmittedPackets;
    this.receivedPackets = stats.receivedPackets;
    this.duplicatePackets = stats.duplicatePackets;
    this.asyncPackets = stats.asyncPackets;
    this.receiveTimeouts = stats.receiveTimeouts;
  }

  int transmittedPackets;

  int receivedPackets;

  int duplicatePackets;

  int asyncPackets;

  int receiveTimeouts;
}
