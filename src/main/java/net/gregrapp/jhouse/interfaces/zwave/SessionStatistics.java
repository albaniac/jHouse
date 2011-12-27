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
  // <summary>
  //
  // </summary>
  public SessionStatistics()
  {
  }

  // <summary>
  //
  // </summary>
  // <param name="stats"></param>
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
