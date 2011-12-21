/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.interfaces.AbstractInterfaceReader;
import net.gregrapp.jhouse.interfaces.Interface;
import net.gregrapp.jhouse.transports.Transport;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveReader extends AbstractInterfaceReader
{
  private static final Logger logger = LoggerFactory.getLogger(ZwaveReader.class);

  /**
   * @param iface
   */
  public ZwaveReader(Interface iface)
  {
    super(iface);
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    logger.info("Starting reader thread");
    Transport transport = iface.getTransport();
    while (true)
    {
      int numBytesAvailable = transport.available();
      if (numBytesAvailable > 0)
        logger.debug("{} serial bytes available", numBytesAvailable);
        List<Integer> buffer = transport.read(numBytesAvailable);
        
        if (buffer.get(0) == 0x01)
        {
          logger.debug("Received SOF byte");
        }
        
      {
        
      }
      
    }
  }

}
