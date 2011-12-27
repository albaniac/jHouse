/**
 * 
 */
package net.gregrapp.jhouse.legacy.interfaces.zwave;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.gregrapp.jhouse.transports.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveInterfaceReader implements Runnable
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveInterfaceReader.class);
  private List<ZwaveSendJob> sendQueue = null;
  private boolean run = true;
  private ZwaveInterface zwaveInterface = null;

  /**
   * Config Options
   */
  private static final int readLoopDelay = 100;
  private static final int ackTimeout = 100;
  private static final int responseTimeout = 100;
  private static final int firstCallbackTimeout = 10 * 1000;
  private static final int moreCallbackTimeout = 20 * 1000;
  private static final int resendCount = 3;
  private static final int maxCallbackJobs = 3;

  /**
   * @param iface
   */
  public ZwaveInterfaceReader(ZwaveInterface zwaveInterface)
  {
    this.zwaveInterface = zwaveInterface;
    this.sendQueue = zwaveInterface.getSendQueue();
  }

  /**
   * Shut down the ZwaveInterfaceReader thread
   */
  public void destroy()
  {
    this.run = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    logger.info("Starting reader thread");
    Transport transport = zwaveInterface.getTransport();
    while (run)
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

          if (ZwaveFrame.isValidChecksum(payload_len, payload))
          {
            logger.debug("Valid frame received, sending ACK");
            transport.write(new int[] { ZwaveConstants.ACK });
            decodePayload(payload);

          } else
          {
            logger.warn("Received invalid frame");
          }
        } else if (buffer[0] == ZwaveConstants.CAN)
        {
          logger.debug("CAN received");
          // Implement resendNAck
        } else if (buffer[0] == ZwaveConstants.NAK)
        {
          logger.debug("NAK received");
          // Implement resendNAck
        } else if (buffer[0] == ZwaveConstants.ACK)
        {
          logger.debug("ACK received");
          if (this.sendQueue.size() > 0)
          {
            synchronized (this.sendQueue)
            {
              Iterator<ZwaveSendJob> it = this.sendQueue.iterator();
              while (it.hasNext())
              {
                ZwaveSendJob job = it.next();
                if (job.isSent() && job.isAwaitAck())
                {
                  if (job.getCallback() == 0 && job.isAwaitResponse() == false)
                    // Not awaiting a callback or a response, so remove the job
                    it.remove();
                  else
                  {
                    // Got an ACK
                    job.setAwaitAck(false);
                    if (job.isAwaitResponse())
                      job.setTimeout(100);
                    else
                      job.setTimeout(firstCallbackTimeout);
                  }
                }
              }
            }
          }
        } else
        {
          logger.error("Out of frame flow");
        }
      } else
      // Nothing to receive, so let's send if we have anything in the send queue
      {
        if (this.sendQueue.size() > 0)
        {
          ZwaveSendJob nextJob = null;
          int awaitingCallbackJobs = 0;

          synchronized (this.sendQueue)
          {
            for (ZwaveSendJob job : this.sendQueue)
            {
              if (awaitingCallbackJobs >= maxCallbackJobs)
                // Too many jobs waiting for callback
                break;
              if (job.isSent())
              {
                if (job.isAwaitAck() || job.isAwaitResponse())
                {
                  // There is a job awaiting an ACK or RESPONSE so we can't send
                  // a new job
                  break;
                } else
                {
                  // If it is sent but not awaiting ACK or RESPONSE, then it's
                  // awaiting a CALLBACK
                  awaitingCallbackJobs++;
                }
              } else
              {
                nextJob = job;
                break;
              }
            }

            if (nextJob != null)
            {
              int[] frame = nextJob.getFrame().buildFrame();
              logger.debug("Sending job {}",
                  Arrays.toString(frame));
              transport.write(frame);
              nextJob.setSent(true);
              nextJob.setTimeout(ackTimeout);
              nextJob.incrementSendCount();
            }

            for (ZwaveSendJob job : this.sendQueue)
            {
              if (job.isSent())
              {
                job.decrementTimeout(readLoopDelay);
                if (job.getTimeout() <= 0)
                {
                  if (job.isAwaitAck())
                  {
                    logger.warn("No ACK received before timeout");
                    // TODO Add resend logic
                  } else if (job.isAwaitResponse())
                  {
                    logger.warn("No RESPONSE received before timeout");
                    // TODO Add resend logic
                  } else if (job.getCallback() > 0
                      && job.isFirstCallbackReceived() == false)
                  {
                    logger.warn("No callback received before timeout");
                    // TODO Add resend logic
                  } else
                  {
                    logger
                        .warn("Job not removed before timeout (waiting for more callbacks?).  Removing job.");
                    this.sendQueue.remove(job);
                  }
                }
              }
            }
          }
        }
      }

      try
      {
        Thread.sleep(readLoopDelay);
      } catch (InterruptedException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    logger.info("Reader thread is exiting");
  }

  private void decodePayload(int[] payload)
  {
    if (payload[0] == ZwaveConstants.RESPONSE)
      decodeResponse(Arrays.copyOfRange(payload, 1, payload.length));
    else if (payload[0] == ZwaveConstants.REQUEST)
      decodeRequest(Arrays.copyOfRange(payload, 1, payload.length));
    else
      logger.warn("Invalid payload type");
  }

  private void decodeResponse(int[] response)
  {
    ZwaveSendJob responseJob = null;
    synchronized (this.sendQueue)
    {
      for (ZwaveSendJob job : this.sendQueue)
      {
        if (job.isSent() && !job.isAwaitAck() && job.isAwaitResponse())
          responseJob = job;
        break;
      }
    }
      if (responseJob == null)
      {
        logger.warn("RESPONSE received without matching request");
        return;
      }
    responseJob.setAwaitResponse(false);
    if (responseJob.getCallback() == 0)
      // Remove the job if no callback is needed
      this.sendQueue.remove(responseJob);
    else
      responseJob.setTimeout(firstCallbackTimeout);

    switch (response[0])
    {
    case ZwaveConstants.FUNC_ID_ZW_GET_SUC_NODE_ID:
      logger.debug(
          "Got reply to GET_SUC_NODE_ID: {}",
          response[1] == 0 ? "No SUC" : String.format("SUC node is %s",
              response[1]));
      break;

    case ZwaveConstants.FUNC_ID_ZW_SET_SUC_NODE_ID:
      logger.debug("Got reply to FUNC_ID_ZW_SET_SUC_NODE_ID: %s",
          response[1] > 0 ? "started" : "failed");
      if (response[1] == 0 && responseJob.getCallback() != 0)
        this.sendQueue.remove(responseJob);
      break;

    case ZwaveConstants.FUNC_ID_ZW_ENABLE_SUC:
      logger.debug("Got reply to FUNC_ID_ZW_ENABLE_SUC: %s",
          response[1] > 0 ? "done" : "failed: trying to disable running SUC?");
      break;

    case ZwaveConstants.FUNC_ID_MEMORY_GET_ID:
      logger
          .debug(
              "Got reply to FUNC_ID_MEMORY_GET_ID, Home id: %1$#04x%2$02x%3$02x%4$02x, our node id: %d",
              new Object[] { response[1], response[2], response[3],
                  response[4], response[5] });
      zwaveInterface.setNodeId(response[5]);
      break;

    case ZwaveConstants.FUNC_ID_SERIAL_API_GET_CAPABILITIES:
      // TODO implement FUNC_ID_SERIAL_API_GET_CAPABILITIES
      break;

    case ZwaveConstants.FUNC_ID_SERIAL_API_GET_INIT_DATA:
      int versionByte;
      if (response[3] == ZwaveConstants.MAGIC_LEN)
      {
        versionByte = 4;
      } else
      {
        versionByte = 4 + ZwaveConstants.MAGIC_LEN;
      }

      logger
          .debug(
              "Got reply to FUNC_ID_SERIAL_API_GET_INIT_DATA: Version: %1$#04x, Z-Wave Chip: ZW%2d%2d",
              new Object[] { response[1], response[versionByte],
                  response[versionByte + 1] });
      logger.debug("Capabilities: %s, %s, %s, %s", new Object[] {
          (response[2] & 0x01) == 0x01 ? "Slave API" : "Controller API",
          (response[2] & 0x02) == 0x02 ? "Timer function supported"
              : "Timer function not supported",
          (response[2] & 0x04) == 0x04 ? "Secondary Controller"
              : "Primary Controller",
          (response[2] & 0xf8) == 0xf8 ? "some reserved bits"
              : "no reserved bit" });
      if (response[3] == ZwaveConstants.MAGIC_LEN)
      {
        zwaveInterface.setNodeCount(0);
        // self._node_protocol_count = 0
        for (int i = 4; i < 4 + ZwaveConstants.MAGIC_LEN; i++)
        {
          for (int j = 0; j < 8; j++)
          {
            if ((response[i] & (0x01 << j)) != 0)
            {
              int nodeId = (i - 4) * 8 + j + 1;
              zwaveInterface.setNodeCount(zwaveInterface.getNodeCount() + 1);
              // Request node protocol information and is_virtual flag
              // TODO implement zwRequestNodeProtocolInfo and zwRequestIsVirtual
              // self.zwRequestNodeProtocolInfo(node_id)
              // self.zwRequestIsVirtual(node_id)
            }
          }
        }
      }
      break;

    case ZwaveConstants.FUNC_ID_GET_ROUTING_TABLE_LINE:
      String nodes = "";
      for (int i = 1; i < 1 + ZwaveConstants.MAGIC_LEN; i++)
      {
        for (int j = 0; j < 8; j++)
        {
          if ((response[i] & (0x01 << j)) != 0)
            nodes += String.format("%d ", ((i - 1) * 8 + j + 1));
        }
      }
      logger.debug("List of neighbors: %s", nodes);
      break;

    case ZwaveConstants.FUNC_ID_ZW_GET_VIRTUAL_NODES:
      String virtualNodes = "";
      for (int i = 1; i < 1 + ZwaveConstants.MAGIC_LEN; i++)
      {
        for (int j = 0; j < 8; j++)
        {
          if ((response[i] & (0x01 << j)) != 0)
            virtualNodes += String.format("%d ", ((i - 1) * 8 + j + 1));
        }
      }
      logger.debug("List of virtual nodes: %s", virtualNodes);
      break;

    case ZwaveConstants.FUNC_ID_ZW_GET_NODE_PROTOCOL_INFO:
      logger.debug("Got reply to FUNC_ID_ZW_GET_NODE_PROTOCOL_INFO");
      // TODO Implement FUNC_ID_ZW_GET_NODE_PROTOCOL_INFO
      break;

    case ZwaveConstants.FUNC_ID_ZW_SEND_DATA:
      // TODO Implement FUNC_ID_ZW_SEND_DATA
      break;

    case ZwaveConstants.FUNC_ID_ZW_IS_VIRTUAL_NODE:
      // TODO Implement FUNC_ID_ZW_IS_VIRTUAL_NODE
      break;

    case ZwaveConstants.FUNC_ID_ZW_GET_CONTROLLER_CAPABILITIES:
      // TODO Implement FUNC_ID_ZW_GET_CONTROLLER_CAPABILITIES
      break;

    case ZwaveConstants.FUNC_ID_ZW_TYPE_LIBRARY:
      // TODO Implement FUNC_ID_ZW_TYPE_LIBRARY
      break;

    case ZwaveConstants.FUNC_ID_ZW_GET_VERSION:
      // TODO Implement FUNC_ID_ZW_GET_VERSION
      break;

    case ZwaveConstants.FUNC_ID_ZW_IS_FAILED_NODE:
      // TODO Implement FUNC_ID_ZW_IS_FAILED_NODE
      break;

    case ZwaveConstants.FUNC_ID_ZW_REMOVE_FAILED_NODE_ID:
      // TODO Implement FUNC_ID_ZW_REMOVE_FAILED_NODE_ID
      break;

    case ZwaveConstants.FUNC_ID_ZW_REPLACE_FAILED_NODE:
      // TODO Implement FUNC_ID_ZW_REPLACE_FAILED_NODE
      break;

    case ZwaveConstants.FUNC_ID_ZW_GET_NEIGHBOR_COUNT:
      // TODO Implement FUNC_ID_ZW_GET_NEIGHBOR_COUNT
      break;

    case ZwaveConstants.FUNC_ID_ZW_ARE_NODES_NEIGHBOURS:
      // TODO Implement FUNC_ID_ZW_ARE_NODES_NEIGHBOURS
      break;

    case ZwaveConstants.FUNC_ID_ZW_SET_SLAVE_LEARN_MODE:
      // TODO Implement FUNC_ID_ZW_SET_SLAVE_LEARN_MODE
      break;

    case ZwaveConstants.FUNC_ID_ZW_REQUEST_NETWORK_UPDATE:
      // TODO Implement FUNC_ID_ZW_REQUEST_NETWORK_UPDATE
      break;
    default:
      logger.warn("Unhandled response %#04x: %s", response[0],
          Arrays.toString(response));
      break;
    } // switch

  }

  private void decodeRequest(int[] request)
  {
    // TODO Implement callback handling

    switch (request[0])
    {
    case ZwaveConstants.FUNC_ID_ZW_SEND_DATA:
      // TODO Implement FUNC_ID_ZW_SEND_DATA
      break;

    case ZwaveConstants.FUNC_ID_ZW_ADD_NODE_TO_NETWORK:
      // TODO Implement FUNC_ID_ZW_ADD_NODE_TO_NETWORK
      break;

    case ZwaveConstants.FUNC_ID_ZW_REMOVE_NODE_FROM_NETWORK:
      // TODO Implement FUNC_ID_ZW_REMOVE_NODE_FROM_NETWORK
      break;

    case ZwaveConstants.FUNC_ID_ZW_CONTROLLER_CHANGE:
      // TODO Implement FUNC_ID_ZW_CONTROLLER_CHANGE
      break;

    case ZwaveConstants.FUNC_ID_ZW_SET_LEARN_MODE:
      // TODO Implement FUNC_ID_ZW_SET_LEARN_MODE
      break;

    case ZwaveConstants.FUNC_ID_ZW_REMOVE_FAILED_NODE_ID:
      // TODO Implement FUNC_ID_ZW_REMOVE_FAILED_NODE_ID
      break;

    case ZwaveConstants.FUNC_ID_ZW_REPLACE_FAILED_NODE:
      // TODO Implement FUNC_ID_ZW_REPLACE_FAILED_NODE
      break;

    case ZwaveConstants.FUNC_ID_ZW_SET_SLAVE_LEARN_MODE:
      // TODO Implement FUNC_ID_ZW_SET_SLAVE_LEARN_MODE
      break;

    case ZwaveConstants.FUNC_ID_APPLICATION_COMMAND_HANDLER:
      // TODO Implement FUNC_ID_APPLICATION_COMMAND_HANDLER
      break;

    case ZwaveConstants.FUNC_ID_ZW_APPLICATION_UPDATE:
      // TODO Implement FUNC_ID_ZW_APPLICATION_UPDATE
      break;

    case ZwaveConstants.FUNC_ID_APPLICATION_SLAVE_COMMAND_HANDLER:
      // TODO Implement FUNC_ID_APPLICATION_SLAVE_COMMAND_HANDLER
      break;

    case ZwaveConstants.FUNC_ID_ZW_REQUEST_NODE_NEIGHBOR_UPDATE:
      // TODO Implement FUNC_ID_ZW_REQUEST_NODE_NEIGHBOR_UPDATE
      break;

    case ZwaveConstants.FUNC_ID_ZW_REQUEST_NETWORK_UPDATE:
      // TODO Implement FUNC_ID_ZW_REQUEST_NETWORK_UPDATE
      break;

    case ZwaveConstants.FUNC_ID_ZW_SEND_NODE_INFORMATION:
      // TODO Implement FUNC_ID_ZW_SEND_NODE_INFORMATION
      break;

    case ZwaveConstants.FUNC_ID_ZW_SET_SUC_NODE_ID:
      // TODO Implement FUNC_ID_ZW_SET_SUC_NODE_ID
      break;

    case ZwaveConstants.FUNC_ID_ZW_SET_DEFAULT:
      // TODO Implement FUNC_ID_ZW_SET_DEFAULT
      break;

    default:
      logger.warn("Unhandled request %#04x: %s", request[0],
          Arrays.toString(request));
      break;
    } // switch
  }

}
