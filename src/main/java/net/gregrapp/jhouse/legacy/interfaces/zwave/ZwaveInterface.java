/**
 * 
 */
package net.gregrapp.jhouse.legacy.interfaces.zwave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.gregrapp.jhouse.interfaces.AbstractInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveInterface extends AbstractInterface
    
{
  private static final Logger logger = LoggerFactory
      .getLogger(ZwaveInterface.class);

  private List<ZwaveSendJob> sendQueue = Collections.synchronizedList(new ArrayList<ZwaveSendJob>());

  private int currentCallback = 0;

  private int nodeId = -1;
  
  private int nodeCount = 0;
  /**
   * 
   */
  public ZwaveInterface()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  public void init()
  {
    new Thread(new ZwaveInterfaceReader(this)).start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  public synchronized void send(int[] buffer, int type, boolean response, boolean callback)
  {
    if ((this.currentCallback < 0) || (this.currentCallback > 255))
      this.currentCallback = 0;
    
    this.currentCallback++;
    
    ZwaveFrame frame = new ZwaveFrame(type, this.currentCallback, buffer);
    ZwaveSendJob job = new ZwaveSendJob(response, frame);
    this.sendQueue.add(job);
  }
  
  public List<ZwaveSendJob> getSendQueue()
  {
    return this.sendQueue;
  }

  /**
   * Get this interface's Zwave node ID
   * @return the node ID
   */
  public int getNodeId()
  {
    return nodeId;
  }

  /**
   * Set the Zwave node ID of this interface
   * @param nodeId the node ID to set
   */
  public void setNodeId(int nodeId)
  {
    this.nodeId = nodeId;
  }

  /**
   * Get the number of nodes discovered by this interface
   * @return the node count
   */
  public int getNodeCount()
  {
    return nodeCount;
  }

  /**
   * Set the number of nodes discovered by this interface
   * @param nodeCount the node count to set
   */
  public void setNodeCount(int nodeCount)
  {
    this.nodeCount = nodeCount;
  }
}
