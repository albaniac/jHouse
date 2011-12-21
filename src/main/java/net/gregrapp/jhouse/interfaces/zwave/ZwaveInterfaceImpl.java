/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.interfaces.AbstractInterface;
import net.gregrapp.jhouse.interfaces.InterfaceWriter;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveInterfaceImpl extends AbstractInterface implements InterfaceWriter
{
  private static final Logger logger = LoggerFactory.getLogger(ZwaveInterfaceImpl.class);

  /**
   * 
   */
  public ZwaveInterfaceImpl()
  {
    this.init();
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#init()
   */
  public void init()
  {
    new Thread(new ZwaveReader(this)).start();
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.interfaces.Interface#destroy()
   */
  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  public void sendData(List<Integer> buffer)
  {
    this.sendData(buffer, null);
  }

  public void sendData(List<Integer> buffer, List<Object> params)
  {
    this.transport.write(buffer);
  }

}
