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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */

public class SerialImpl
{
  private static final Logger logger = LoggerFactory
      .getLogger(SerialImpl.class);

  private SerialPort serialPort;
  
  /**
   * Serial baud rate
   */
  private int serialBaud;
  /**
   * Serial port
   */
  private String serialPortString;

  public SerialImpl(List<String> config)
  {
    logger.info("Instantiating SerialImpl instance");
    //this.serialPortString = this.config.get(0);
    logger.debug("Serial port set to {}", this.serialPortString);
    //this.serialBaud = Integer.valueOf(this.config.get(1)).intValue();
    logger.debug("Serial baud set to {}", this.serialBaud);
  }

  public void destroy()
  {
    logger.info("Closing serial port");
    this.serialPort.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#init()
   */
  public void init()
  {
    logger.info("Initialzing instance of transport {}", this.getClass()
        .getName());
    CommPort commPort = null;
    try
    {
      CommPortIdentifier portIdentifier = CommPortIdentifier
          .getPortIdentifier(this.serialPortString);
      commPort = portIdentifier.open(this.getClass().getName(), 2000);
    } catch (NoSuchPortException e)
    {
    } catch (PortInUseException e)
    {
    }

    if (commPort instanceof SerialPort)
    {
      serialPort = (SerialPort) commPort;
      try
      {
        logger.debug("Setting serial port parameters");
        serialPort.setSerialPortParams(this.serialBaud, SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        //this.in = serialPort.getInputStream();
        //this.out = serialPort.getOutputStream();
      } catch (UnsupportedCommOperationException e)
      {
      } //catch (IOException e)
      //{
      //}
    }
  }

  public boolean isOpen()
  {
    return true;
  }
}
