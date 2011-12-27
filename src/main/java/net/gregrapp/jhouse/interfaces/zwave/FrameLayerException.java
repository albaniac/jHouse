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
public class FrameLayerException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // <summary>
  //
  // </summary>
  public FrameLayerException()
  {

  }

  // <summary>
  //
  // </summary>
  public FrameLayerException(String describe)
  {
    super(describe);
  }

  // <summary>
  //
  // </summary>
  public FrameLayerException(String describe, Exception innerException)
  {
    // Add any type-specific logic for inner exceptions.

    super(describe, innerException);
  }

}
