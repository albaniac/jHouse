/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import net.gregrapp.jhouse.transports.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSFrameLayerImpl implements Envisalink2DSFrameLayer
{
  private static final Logger logger = LoggerFactory
      .getLogger(Envisalink2DSFrameLayerImpl.class);
  private Envisalink2DSFrameLayerAsyncCallback handler;
  private BufferedReader reader;
  private Thread receiveThread;
  private boolean receiveThreadActive;

  private Transport transport;
  private PrintWriter writer;

  /**
   * 
   */
  public Envisalink2DSFrameLayerImpl(Transport transport)
  {
    this.transport = transport;

    this.startReceiveThread();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#close
   * ()
   */
  @Override
  public void destroy()
  {
    logger.debug("Destroying frame layer");
    this.receiveThreadActive = false;
    transport.destroy();
  }

  /**
   * Data receive thread loop
   * 
   * @throws Envisalink2DSFrameLayerException
   */
  private void receiveThread()
  {
    logger.info("Receive thread started");

    String stringRead = null;

    while (receiveThreadActive)
    {
      try
      {
        stringRead = reader.readLine();
        logger.trace("Received data [{}]", stringRead);
        if (stringRead == null)
        {
          logger.error("Null string received, terminating receive thread");
          receiveThreadActive = false;
        } else
        {
          Envisalink2DSDataFrame frame = new Envisalink2DSDataFrame(stringRead);
          if (this.handler == null)
          {
            logger.warn("Data received [{}] but handler not set", stringRead);
          } else
          {
            if (frame.isValidChecksum())
            {
              this.handler.frameReceived(frame);
            } else
            {
              logger.warn("Invalid data received [{}]", stringRead);
            }
          }
        }
      } catch (IOException e)
      {
        if (this.receiveThreadActive)
        {
          logger.warn("Error reading data: ", e);
          this.receiveThreadActive = false;
        }
      } catch (Envisalink2DSDataFrameException e)
      {
        logger.warn("Error parsing raw data [{}]", stringRead);
      }

      if (!transport.isOpen())
      {
        logger.error("Transport closed, exiting receive thread");
        receiveThreadActive = false;
      }
    }
    logger.info("Receive thread exiting");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#
   * setCallbackHandler (net.gregrapp.jhouse.interfaces.envisalink2ds.
   * Envisalink2DSFrameLayerAsyncCallback)
   */
  @Override
  public void setCallbackHandler(Envisalink2DSFrameLayerAsyncCallback handler)
  {
    logger.debug("Callback handler set to [{}]", handler.getClass().getName());
    this.handler = handler;
  }

  /**
   * Start receive thread run loop
   */
  public void startReceiveThread()
  {
    if (transport == null || transport.getOutputStream() == null
        || transport.getInputStream() == null)
    {
      logger.error("Transport in invalid state, cannot start receive thread.");
      return;
    }

    this.writer = new PrintWriter(transport.getOutputStream(), true);
    this.reader = new BufferedReader(new InputStreamReader(
        transport.getInputStream()));

    this.receiveThreadActive = true;

    receiveThread = new Thread(new Runnable()
    {
      public void run()
      {
        receiveThread();
      }
    });

    receiveThread.setPriority(Thread.MAX_PRIORITY);
    receiveThread.setDaemon(true);
    logger.debug("Starting receive thread");
    receiveThread.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.interfaces.envisalink2ds.Envisalink2DSFrameLayer#write
   * (net.gregrapp .jhouse.interfaces.zwave.DataFrame)
   */
  @Override
  public void write(Envisalink2DSDataFrame frame)
      throws Envisalink2DSFrameLayerException
  {
    logger.debug("Writing frame to transport");
    synchronized (this)
    {
      logger.trace("Sending frame [{}]", frame.getFrameNoCrlf());
      writer.println(frame.getFrame());
    }
  }
}
