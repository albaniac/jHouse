package net.gregrapp.jhouse.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Greg Rapp
 * 
 */
public interface Transport
{
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

  /**
   * @return an InputStream instance for this Transport
   */
  public InputStream getInputStream();

  /**
   * Get number of bytes received by this Transport
   * 
   * @return number of bytes
   */
  public int getReceivedBytes();

  /**
   * Get number of bytes transmitted by this Transport
   * 
   * @return number of bytes
   */
  public int getTransmittedBytes();

  /**
   * @return an OutputStream instance for this Transport
   */
  public OutputStream getOutputStream();

  /**
   * Initialize the Transport
   * 
   * @return
   * @throws TransportException
   */
  public void init() throws TransportException;

  /**
   * Is the interface available for sending/receiving
   * 
   * @return
   */
  public boolean isOpen();

  /**
   * Read bytes from the Transport
   * 
   * @param buffer
   *          bytes read
   * @return number of bytes read from the Transport
   * @throws IOException
   */
  public int read(int[] buffer) throws IOException;

  /**
   * Write bytes to the Transport
   * 
   * @param buffer
   *          bytes to write
   * @return number of bytes successfully written
   * @throws IOException
   */
  public int write(int[] buffer) throws IOException;
}
