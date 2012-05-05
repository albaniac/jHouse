/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

/**
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSDataFrameException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Envisalink2DSDataFrameException()
  {

  }

  public Envisalink2DSDataFrameException(String describe)
  {
    super(describe);
  }

  public Envisalink2DSDataFrameException(String describe,
      Exception innerException)
  {
    super(describe, innerException);
  }
}
