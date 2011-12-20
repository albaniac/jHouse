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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.transports.AbstractTransport;

/**
 * @author Greg Rapp
 * 
 */
public class SerialImpl extends AbstractTransport
{
  private static final Logger logger = LoggerFactory.getLogger(SerialImpl.class);

  /**
   * Serial port
   */
  private String serialPort;

  /**
   * Serial baud rate
   */
  private int serialBaud;

  private InputStream in;
  private OutputStream out;

  public SerialImpl(List<String> config)
  {
    super(config);
    logger.info("Instantiating SerialImpl instance");

    this.serialPort = this.config.get(0);
    logger.debug("Serial port set to {}", this.serialPort);
    this.serialBaud = Integer.valueOf(this.config.get(1)).intValue();
    logger.debug("Serial baud set to {}", this.serialBaud);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#init()
   */
  public void init()
  {
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

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#write(byte[])
   */
  public void write(byte[] buffer)
  {
    try
    {
      this.out.write(buffer);
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#read(int)
   */
  public byte[] read(int size)
  {
    byte[] bytesRead = new byte[size];
    try
    {
      int numBytesRead = in.read(bytesRead);
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return bytesRead;
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

}
