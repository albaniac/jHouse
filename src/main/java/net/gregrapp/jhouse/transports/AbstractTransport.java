/**
 * 
 */
package net.gregrapp.jhouse.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.utils.ArrayUtils;

/**
 * @author Greg Rapp
 *
 */
public abstract class AbstractTransport implements Transport
{
  /**
   * Configuration for this AbstractTransport
   */
  private static final Logger logger = LoggerFactory
      .getLogger(AbstractTransport.class);
  
  protected List<String> config;
  
  protected InputStream in;
  
  protected OutputStream out;
 
  protected int receivedBytes;

  protected int transmittedBytes;

  public AbstractTransport(List<String> config)
  {
    this.config = config;
  }

  public int available()
  {
    int bytesAvailable = 0;

    try
    {
      bytesAvailable = in.available();
    } catch (IOException e)
    {
      logger.error("Error determining number of bytes available on interface",
          e);
    }

    return bytesAvailable;
  }

  @Override
  public InputStream getReader()
  {
    return this.in;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.transports.Transport#getReceivedBytes()
   */
  public int getReceivedBytes()
  {
    return receivedBytes;
  }
  
  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.transports.Transport#getTransmittedBytes()
   */
  public int getTransmittedBytes()
  {
    return transmittedBytes;
  }  
  
  @Override
  public OutputStream getWriter()
  {
    return this.out;
  }

  protected void incrementReceivedBytes()
  {
    receivedBytes++;
  }
  
  protected void incrementTransmittedBytes()
  {
    transmittedBytes++;
  }

  public int read(int[] buffer) throws TransportException
  {
    byte[] bytesRead = new byte[buffer.length];
    int numBytes = 0;
    try
    {
      numBytes = in.read(bytesRead);
      for (int i = 0; i < numBytes; i++)
      {
        buffer[i] = (int) bytesRead[i];
        incrementReceivedBytes();
      }
    } catch (IOException e)
    {
      throw new TransportException("Transport read error", e);
    }
    logger.debug("Read {} bytes in from interface", numBytes);
    logger.trace("{}",
        ArrayUtils.toHexStringArray((Arrays.copyOfRange(buffer, 0, numBytes))));
    return numBytes;
  }

  public int write(int[] buffer) throws TransportException
  {
    logger.debug("Writing {} bytes out to interface", buffer.length);
    logger.trace("{}", ArrayUtils.toHexStringArray(buffer));
    try
    {
      for (int buf : buffer)
      {
        this.out.write(buf);
        incrementTransmittedBytes();
      }
    } catch (IOException e)
    {
      throw new TransportException("Transport write error", e);
    }
    return buffer.length;
  }

}
