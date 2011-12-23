/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.InterfaceWriter;

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
    new Thread(new ZwaveInterfaceReader(this, sendQueue)).start();
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

}
