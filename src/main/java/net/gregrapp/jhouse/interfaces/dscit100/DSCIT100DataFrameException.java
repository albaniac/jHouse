/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

/**
 * @author Greg Rapp
 *
 */
public class DSCIT100DataFrameException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DSCIT100DataFrameException()
  {
    
    
  }
  public DSCIT100DataFrameException(String describe)
  {
    super(describe);
  }
  
  public DSCIT100DataFrameException(String describe, Exception innerException)
  {
    super(describe, innerException);
  }
}
