/**
 * 
 */
package net.gregrapp.jhouse.transports.tcpclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gregrapp.jhouse.transports.AbstractTransport;
import net.gregrapp.jhouse.transports.TransportException;

/**
 * @author grapp
 * 
 */
public class TCPClientImpl extends AbstractTransport
{
  private static final Logger logger = LoggerFactory
      .getLogger(TCPClientImpl.class);

  private static final int CONNECT_TIMEOUT = 10000;

  private Socket tcp = null;

  private String host = null;

  private int port;

  public TCPClientImpl(List<String> config)
  {
    super(config);
    logger.info("Instantiating TCPClientImpl instance");
    this.host = this.config.get(0);
    logger.debug("Host set to {}", this.host);
    this.port = Integer.valueOf(this.config.get(1));
    logger.debug("TCP port set to {}", this.port);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.transports.AbstractTransport#destroy()
   */
  @Override
  public void destroy()
  {
    super.destroy();
    
    try
    {
      logger.debug("Closing TCP connection");
      this.tcp.close();
    } catch (IOException e)
    {
      logger.error("Error closing socket: ", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#init()
   */
  @Override
  public void init() throws TransportException
  {
    try
    {
      tcp = new Socket();
      tcp.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT);
    } catch (UnknownHostException e)
    {
      throw new TransportException("unknown host " + host + ":" + String.valueOf(port));
    } catch (SocketTimeoutException e)
    {
      throw new TransportException("connect timed out");
    } catch (IOException e)
    {
      throw new TransportException("socket connect error: ", e);
    }

    try
    {
      this.in = tcp.getInputStream();
      this.out = tcp.getOutputStream();
    } catch (IOException e)
    {
      throw new TransportException("error getting socket input/output stream");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.transports.Transport#isOpen()
   */
  @Override
  public boolean isOpen()
  {
    if (tcp == null)
      return false;
    else
      return tcp.isConnected();
  }

}
