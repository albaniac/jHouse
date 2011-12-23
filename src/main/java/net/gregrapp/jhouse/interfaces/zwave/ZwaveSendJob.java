/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

/**
 * @author Greg Rapp
 *
 */
public class ZwaveSendJob
{
  private boolean awaitAck = true;
  private boolean awaitResponse = false;
  private boolean response = false;
  private int sendCount = 0;
  private boolean sent = false;
  private boolean firstCallbackReceived = false;
  private int timeout = 1000;
  private ZwaveFrame frame;
  
  
  public ZwaveSendJob(boolean response, ZwaveFrame frame)
  {
    this.response = response;
    this.frame = frame;
  }
  
  public boolean isAwaitAck()
  {
    return awaitAck;
  }
  public void setAwaitAck(boolean awaitAck)
  {
    this.awaitAck = awaitAck;
  }
  public boolean isAwaitResponse()
  {
    return awaitResponse;
  }
  public void setAwaitResponse(boolean awaitResponse)
  {
    this.awaitResponse = awaitResponse;
  }
  public boolean isResponse()
  {
    return response;
  }
  public void setResponse(boolean response)
  {
    this.response = response;
  }
  public int getSendCount()
  {
    return sendCount;
  }
  public void setSendCount(int sendCount)
  {
    this.sendCount = sendCount;
  }
  public boolean isSent()
  {
    return sent;
  }
  public void setSent(boolean sent)
  {
    this.sent = sent;
  }
  public boolean isFirstCallbackReceived()
  {
    return firstCallbackReceived;
  }
  public void setFirstCallbackReceived(boolean firstCallbackReceived)
  {
    this.firstCallbackReceived = firstCallbackReceived;
  }
  public int getTimeout()
  {
    return timeout;
  }
  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }
  public ZwaveFrame getFrame()
  {
    return frame;
  }
  public void setFrame(ZwaveFrame frame)
  {
    this.frame = frame;
  }

  
}
