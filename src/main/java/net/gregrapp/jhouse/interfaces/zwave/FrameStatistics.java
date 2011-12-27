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

/**
 * @author Greg Rapp
 *
 */
// <summary>
// 
// </summary>
public class FrameStatistics
{
  // <summary>
  // 
  // </summary>
  public FrameStatistics()
  {
  }

  // <summary>
  // 
  // </summary>
  // <param name="stats"></param>
  public FrameStatistics(FrameStatistics stats)
  {
    if (stats == null)
    {
      throw new NullPointerException("stats");
    }
    this.transmittedAcks = stats.transmittedAcks;
    this.transmittedNaks = stats.transmittedNaks;
    this.receivedAcks =  stats.receivedAcks;
    this.receivedNaks = stats.receivedNaks;
    this.transmittedFrames = stats.transmittedFrames;
    this.receivedFrames = stats.receivedFrames;
    this.retransmittedFrames = stats.retransmittedFrames;
    this.droppedFrames = stats.droppedFrames;
  }
  
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
}


