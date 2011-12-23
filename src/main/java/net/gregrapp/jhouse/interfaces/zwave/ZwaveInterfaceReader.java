/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.Arrays;
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
public class ZwaveInterfaceReader extends AbstractInterfaceReader
{
  private static final Logger logger = LoggerFactory.getLogger(ZwaveInterfaceReader.class);
  private List<ZwaveSendJob> sendQueue = null;
  
  /**
   * @param iface
   */
  public ZwaveInterfaceReader(Interface iface, List<ZwaveSendJob> sendQueue)
  {
    super(iface);
    this.sendQueue = sendQueue;
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
      {
        logger.debug("{} serial bytes available", numBytesAvailable);
        int[] buffer = transport.read(1);
        
        if (buffer[0] == ZwaveConstants.SOF)
        {
          logger.debug("Received SOF byte");
          
          int payload_len = transport.read(1)[0];
          logger.debug("Received payload length byte [{}]", payload_len);
          
          int[] payload = transport.read(payload_len);
          logger.debug("Payload received {}", Arrays.toString(payload));
          
          
          //ZwaveFrame frame = ZwaveFrame.parseRawFrame(frame_len, payload);
          if (ZwaveFrame.isValidChecksum(payload_len, payload))
          {
            logger.debug("Valid frame received, sending ACK");
            transport.write(new int[] {ZwaveConstants.ACK});
          } else
          {
            logger.warn("Received invalid frame");            
          }
        }
        else if (buffer[0] == ZwaveConstants.CAN)
        {
          logger.debug("CAN received");
        }
        else if (buffer[0] == ZwaveConstants.NAK)
        {
          logger.debug("NAK received");          
        }
        else if (buffer[0] == ZwaveConstants.ACK)
        {
          logger.debug("ACK received");          
        }
        else
        {
          logger.error("Out of frame flow");
        }
      }
      else
      {
        if (this.sendQueue.size() > 0)
        {
          ZwaveSendJob nextJob = null;
          int awaitingCallbackJobs = 0;
          
          synchronized (this.sendQueue)
          {
            for (ZwaveSendJob job : this.sendQueue)
            {
              if (job.isSent())
              {
                if (job.isAwaitAck() || job.isAwaitResponse())
                {
                  // There is a job awaiting an ACK or RESPONSE so we can't send a new job
                  break; 
                } 
                else
                {
                  awaitingCallbackJobs++;
                }
              }
              else
              {
                nextJob = job;
                break;
              }
            }
            
            if (nextJob != null)
            {
              logger.debug("Sending job {}", Arrays.toString(nextJob.getFrame().buildFrame()));
              transport.write(nextJob.getFrame().buildFrame());
              nextJob.setSent(true);
              nextJob.setSendCount(nextJob.getSendCount()+1);
              
            }
          }
        }
      }
    }
  }

}
