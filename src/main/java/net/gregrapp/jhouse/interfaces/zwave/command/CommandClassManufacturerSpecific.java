/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave.command;

/**
 * @author Greg Rapp
 * 
 */
public interface CommandClassManufacturerSpecific extends CommandClass
{
  /**
   * Request Z-Wave manufacturer specific report
   */
  public void commandClassManufacturerSpecificGet();

  /**
   * Z-Wave manufacturer specific report callback
   * 
   * @param manufacturer
   *          manufacturer ID
   * @param productType
   *          product type ID
   * @param productId
   *          product ID
   */
  public void commandClassManufacturerSpecificReport(int manufacturer,
      int productType, int productId);
}
