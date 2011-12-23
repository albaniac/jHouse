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
   */
  public void write(int[] buffer);

  /**
   * Read bytes from the Transport
   * 
   * @param size
   *          number of bytes to read
   * @return bytes read from the Transport
   */
  public int[] read(int size);

  /**
   * Get number of bytes available to be read from the Transport
   * 
   * @return number of bytes
   */
  public int available();

  /**
   * Shutdown the Transport
   */
  public void destroy();
}
