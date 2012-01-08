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
//          Revision:         $Revision: 1.4 $
//          Last Changed:     $Date: 2006/07/24 09:14:16 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

/**
 * @author Greg Rapp
 * 
 */
/**
 * @author grapp
 *
 */
public class Node
{
  private static final int LISTENING_SUPPORT = 0x80;
  private int basic;
  private int capability;
  private int[] cmdClasses;
  private int generic;
  private int id;
  private boolean isVirtual;
  private int manufacturer;
  private int productId;
  private int productType;
  private int reserved;
  private int security;
  private int specific;
  
  // <summary>
  //
  // </summary>
  // <param name="id"></param>
  // <param name="capability"></param>
  // <param name="security"></param>
  // <param name="reserved"></param>
  // <param name="basic"></param>
  // <param name="generic"></param>
  // <param name="specific"></param>
  public Node(int id, int capability, int security, int reserved, int basic,
      int generic, int specific)
  {
    this(id, capability, security, reserved, basic, generic, specific, null);
  }

  // <summary>
  //
  // </summary>
  // <param name="id"></param>
  // <param name="capability"></param>
  // <param name="security"></param>
  // <param name="reserved"></param>
  // <param name="basic"></param>
  // <param name="generic"></param>
  // <param name="specific"></param>
  // <param name="cmdCl"></param>
  public Node(int id, int capability, int security, int reserved, int basic,
      int generic, int specific, int[] cmdCl)
  {
    this.id = id;
    this.capability = capability;
    this.security = security;
    this.reserved = reserved;
    this.basic = basic;
    this.generic = generic;
    this.specific = specific;
    if (cmdCl != null)
    {
      this.cmdClasses = new int[cmdCl.length];
      System.arraycopy(cmdCl, 0, this.cmdClasses, 0, cmdCl.length);
    }
  }

  // <summary>
  //
  // </summary>
  // <param name="id"></param>
  // <param name="capability"></param>
  // <param name="security"></param>
  // <param name="reserved"></param>
  // <param name="basic"></param>
  // <param name="generic"></param>
  // <param name="specific"></param>
  // <param name="cmdCl"></param>
  // <param name="isVirtual"></param>
  public Node(int id, int capability, int security, int reserved, int basic,
      int generic, int specific, int[] cmdCl, boolean isVirtual)
  {
    this.id = id;
    this.capability = capability;
    this.security = security;
    this.reserved = reserved;
    this.basic = basic;
    this.generic = generic;
    this.specific = specific;
    this.isVirtual = isVirtual;
    if (cmdCl != null)
    {
      this.cmdClasses = new int[cmdCl.length];
      System.arraycopy(cmdCl, 0, this.cmdClasses, 0, cmdCl.length);
    }
  }

  /**
   * @return Z-Wave node basic device type
   */
  public int getBasic()
  {
    return basic;
  }

  /**
   * @return Z-Wave node capability bitmask
   */
  public int getCapability()
  {
    return capability;
  }

  /**
   * @return Z-Wave node generic device type
   */
  public int getGeneric()
  {
    return generic;
  }

  /**
   * @return Z-Wave node ID
   */
  public int getId()
  {
    return id;
  }

  /**
   * @return the manufacturer
   */
  public int getManufacturer()
  {
    return manufacturer;
  }

  /**
   * @return the productId
   */
  public int getProductId()
  {
    return productId;
  }

  /**
   * @return the productType
   */
  public int getProductType()
  {
    return productType;
  }

  public int getReserved()
  {
    return reserved;
  }

  public int getSecurity()
  {
    return security;
  }

  /**
   * @return Z-Wave node specific device type
   */
  public int getSpecific()
  {
    return specific;
  }

  public int[] getSupportedCmdClasses()
  {
    return cmdClasses;
  }

  /**
   * Verify if node is a listening node
   * 
   * @return True if Node is a listening device, False if not
   */
  public boolean isNodeListening()
  {
    return ((capability & LISTENING_SUPPORT) != 0);
  }

  /**
   * @return True if Node is a virtual device, False if not
   */
  public boolean isVirtual()
  {
    return isVirtual;
  }

  public void setBasic(int value)
  {
    basic = value;
  }

  public void setCapability(int value)
  {
    capability = value;
  }

  public void setGeneric(int value)
  {
    generic = value;
  }

  /**
   * @param manufacturer the manufacturer to set
   */
  public void setManufacturer(int manufacturer)
  {
    this.manufacturer = manufacturer;
  }

  /**
   * @param productId the productId to set
   */
  public void setProductId(int productId)
  {
    this.productId = productId;
  }

  /**
   * @param productType the productType to set
   */
  public void setProductType(int productType)
  {
    this.productType = productType;
  }

  public void setReserved(int value)
  {
    reserved = value;
  }

  public void setSecurity(int value)
  {
    security = value;
  }

  public void setSpecific(int value)
  {
    specific = value;
  }

  public void setSupportedCmdClasses(int[] value)
  {
    cmdClasses = value;
  }

  public void setVirtual(boolean value)
  {
    isVirtual = value;
  }

  @Override
  public String toString()
  {
    return "NodeId: " + id + " Type: " + basic + " Cap: " + capability
        + " Sec: " + security + " Res: " + reserved;
  }
}
