package net.gregrapp.jhouse.transports;

/**
 * @author Greg Rapp
 *
 */
public class TransportException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // <summary>
  //
  // </summary>
  public TransportException()
  {

  }

  // <summary>
  //
  // </summary>
  public TransportException(String describe)
  {
    super(describe);
  }

  // <summary>
  //
  // </summary>
  public TransportException(String describe, Exception innerException)
  {
    // Add any type-specific logic for inner exceptions.

    super(describe, innerException);
  }

}
