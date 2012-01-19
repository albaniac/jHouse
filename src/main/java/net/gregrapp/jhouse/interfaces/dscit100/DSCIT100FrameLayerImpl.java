/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.gregrapp.jhouse.transports.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 *
 */
public class DSCIT100FrameLayerImpl implements DSCIT100FrameLayer
{
  private static final Logger logger = LoggerFactory
      .getLogger(DSCIT100FrameLayerImpl.class);
  
  private DSCIT100FrameLayerAsyncCallback handler;
  private BufferedReader reader;
  private boolean receiveThreadActive;
  @SuppressWarnings("unused")
  private Transport transport;
  private BufferedWriter writer;
  
  /**
   * 
   */
  public DSCIT100FrameLayerImpl(Transport transport)
  {
    this.transport = transport;
    this.writer = new BufferedWriter(new OutputStreamWriter(transport.getOutputStream()));
    this.reader = new BufferedReader(new InputStreamReader(transport.getInputStream()));
    this.receiveThreadActive = true;
    
    Thread receiveThread = new Thread(new Runnable()
    {
      public void run()
      {
        receiveThread();
      }
    });
    receiveThread.setPriority(Thread.MAX_PRIORITY);
    logger.debug("Starting receive thread");
    receiveThread.start();
  }

  private void receiveThread()
  {
    logger.info("Receiver thread started");
    
    String stringRead;
    
    while (receiveThreadActive)
    {
      try
      {
        stringRead = reader.readLine();
        DSCIT100DataFrame frame = new DSCIT100DataFrame(stringRead);
        if (frame.isValidChecksum())
          this.handler.frameReceived(frame);
      } catch (IOException e)
      {
        logger.warn("Error reading data: ", e);
      }
    }
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.dscit100.DSCIT100FrameLayer#setCallbackHandler(net.gregrapp.jhouse.interfaces.dscit100.DSCIT100FrameLayerAsyncCallback)
   */
  @Override
  public void setCallbackHandler(DSCIT100FrameLayerAsyncCallback handler)
  {
    this.handler = handler;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.dscit100.DSCIT100FrameLayer#write(net.gregrapp.jhouse.interfaces.zwave.DataFrame)
   */
  @Override
  public void write(DSCIT100DataFrame frame) throws DSCIT100FrameLayerException
  {
    logger.debug("Writing frame to transport");
    synchronized (this)
    {
      try
      {
        writer.write(frame.getFrame());
      } catch (IOException e)
      {
        throw new DSCIT100FrameLayerException("Error writing frame to transport: " + e.getLocalizedMessage());
      }
    }
  }

}
