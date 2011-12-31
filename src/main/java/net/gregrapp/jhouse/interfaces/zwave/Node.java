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
public class Node
{
  private static final int LISTENING_SUPPORT = 0x80;
  private int basic;
  private int capability;
  private int[] cmdClasses;
  private int generic;
  private int id;
  private boolean isVirtual;
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

  // <summary>
  //
  // </summary>
  public int getBasic()
  {
    return basic;
  }

  // <summary>
  //
  // </summary>
  public int getCapability()
  {
    return capability;
  }

  // <summary>
  // Get Generic and specific Device Type
  // </summary>
  public int getGeneric()
  {
    return generic;
  }

  // <summary>
  // get Node Id
  // </summary>
  public int getId()
  {
    return id;
  }

  // <summary>
  //
  // </summary>
  public int getReserved()
  {
    return reserved;
  }

  // <summary>
  //
  // </summary>
  public int getSecurity()
  {
    return security;
  }

  // <summary>
  //
  // </summary>
  public int getSpecific()
  {
    return specific;
  }

  // <summary>
  //
  // </summary>
  public int[] getSupportedCmdClasses()
  {
    return cmdClasses;
  }

  // <summary>
  // Verify if node is a listening node
  // </summary>
  // <returns>True is Node is a listening device, False if Node is not a
  // listening device</returns>
  public boolean isNodeListening()
  {
    return ((capability & LISTENING_SUPPORT) != 0);
  }

  // <summary>
  //
  // </summary>
  public boolean isVirtual()
  {
    return isVirtual;
  }

  // <summary>
  //
  // </summary>
  public void setBasic(int value)
  {
    basic = value;
  }

  // <summary>
  //
  // </summary>
  public void setCapability(int value)
  {
    capability = value;
  }

  // <summary>
  // Set Generic and specific Device Type
  // </summary>
  public void setGeneric(int value)
  {
    generic = value;
  }

  // <summary>
  //
  // </summary>
  public void setReserved(int value)
  {
    reserved = value;
  }

  // <summary>
  //
  // </summary>
  public void setSecurity(int value)
  {
    security = value;
  }

  // <summary>
  //
  // </summary>
  public void setSpecific(int value)
  {
    specific = value;
  }

  // <summary>
  //
  // </summary>
  public void setSupportedCmdClasses(int[] value)
  {
    cmdClasses = value;
  }

  // <summary>
  //
  // </summary>
  public void setVirtual(boolean value)
  {
    isVirtual = value;
  }

  // <summary>
  //
  // </summary>
  // <returns></returns>
  @Override
  public String toString()
  {
    return "NodeId: " + id + " Type: " + basic + " Cap: " + capability
        + " Sec: " + security + " Res: " + reserved;
  }

}
