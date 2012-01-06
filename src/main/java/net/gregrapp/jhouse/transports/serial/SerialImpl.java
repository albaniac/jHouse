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
import java.util.List;

import net.gregrapp.jhouse.transports.AbstractTransport;
import net.gregrapp.jhouse.transports.TransportException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class SerialImpl extends AbstractTransport
{
  private static final Logger logger = LoggerFactory
      .getLogger(SerialImpl.class);

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
  public void init() throws TransportException
  {
    logger.info("Initialzing instance of transport {}", this.getClass()
        .getName());
    CommPort commPort = null;
    try
    {
      CommPortIdentifier portIdentifier = CommPortIdentifier
          .getPortIdentifier(this.serialPort);
      commPort = portIdentifier.open(this.getClass().getName(), 2000);
    } catch (NoSuchPortException e)
    {
      throw new TransportException("Port not found", e);
    } catch (PortInUseException e)
    {
      throw new TransportException("Port already in use", e);
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
        throw new TransportException("Error initializing transport", e);
      } catch (IOException e)
      {
        throw new TransportException("Error initializing transport", e);
      }
    }
  }

  public boolean isOpen()
  {
    return true;
  }
}
