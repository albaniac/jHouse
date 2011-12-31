/**
 * 
 */
package net.gregrapp.jhouse.transports.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhosue.utils.ArrayUtils;
import net.gregrapp.jhouse.transports.AbstractTransport;
import net.gregrapp.jhouse.transports.TransportException;

/**
 * @author Greg Rapp
 * 
 */
public class SerialImpl extends AbstractTransport
{
  private static final Logger logger = LoggerFactory
      .getLogger(SerialImpl.class);

  private InputStream in;

  private OutputStream out;

  /**
   * Serial baud rate
   */
  private int serialBaud;
  /**
   * Serial port
   */
  private String serialPort;

  public SerialImpl(List<String> config)
  {
    super(config);
    logger.info("Instantiating SerialImpl instance");
    this.serialPort = this.config.get(0);
    logger.debug("Serial port set to {}", this.serialPort);
    this.serialBaud = Integer.valueOf(this.config.get(1)).intValue();
    logger.debug("Serial baud set to {}", this.serialBaud);

    init();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#available()
   */
  public int available()
  {
    int bytesAvailable = 0;

    try
    {
      bytesAvailable = in.available();
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return bytesAvailable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#init()
   */
  public void init()
  {
    logger.info("Initialzing transport {}", this.getClass().getName());
    CommPort commPort = null;
    try
    {
      CommPortIdentifier portIdentifier = CommPortIdentifier
          .getPortIdentifier(this.serialPort);
      commPort = portIdentifier.open(this.getClass().getName(), 2000);
    } catch (NoSuchPortException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (PortInUseException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (commPort instanceof SerialPort)
    {
      SerialPort serialPort = (SerialPort) commPort;
      try
      {
        logger.debug("Setting serial port parameters");
        serialPort.setSerialPortParams(this.serialBaud, SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        this.in = serialPort.getInputStream();
        this.out = serialPort.getOutputStream();
      } catch (UnsupportedCommOperationException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public boolean isOpen()
  {
    // TODO Auto-generated method stub
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#read(int)
   */
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
      /*
       * for (int i=0;i<size;i++) { bytesRead[i] = in.read(); }
       */
    } catch (IOException e)
    {
      throw new TransportException("Transport read error", e);
    }
    logger.debug("Read {} bytes in from interface - {}", numBytes,
        ArrayUtils.toHexStringArray((Arrays.copyOfRange(buffer, 0, numBytes))));
    return numBytes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#write(byte[])
   */
  public int write(int[] buffer) throws TransportException
  {
    logger.debug("Writing {} bytes out to interface - {}", buffer.length,
        ArrayUtils.toHexStringArray(buffer));
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
