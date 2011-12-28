package net.gregrapp.jhouse.transports;

/**
 * @author Greg Rapp
 * 
 */
public interface Transport
{
  /**
   * Initialize the Transport
   */
  public void init();

  /**
   * Write bytes to the Transport
   * 
   * @param buffer
   *          bytes to write
   * @return number of bytes successfully written
   * @throws TransportException 
   */
  public int write(int[] buffer) throws TransportException;

  /**
   * Read bytes from the Transport
   * 
   * @param buffer
   *          bytes read
   * @return number of bytes read from the Transport
   * @throws TransportException 
   */
  public int read(int[] buffer) throws TransportException;

  /**
   * Get number of bytes available to be read from the Transport
   * 
   * @return number of bytes
   */
  public int available();

  /**
   * Is the interface available for sending/receiving
   * 
   * @return
   */
  public boolean isOpen();

  /**
   * Shutdown the Transport
   */
  public void destroy();
}
