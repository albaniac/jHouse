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
public class ApplicationLayerException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ApplicationLayerException()
  {

  }

  public ApplicationLayerException(String describe)
  {
    super(describe);
  }

  public ApplicationLayerException(String describe, Exception innerException)
  {
    // Add any type-specific logic for inner exceptions.
    
    super(describe, innerException);
  }
}
