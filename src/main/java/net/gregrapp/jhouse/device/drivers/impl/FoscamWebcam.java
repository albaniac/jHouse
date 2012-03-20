/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.gregrapp.jhouse.device.classes.PtzWebcam;
import net.gregrapp.jhouse.device.drivers.types.AbstractDeviceDriver;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Foscam Webcam Driver
 * 
 * @author Greg Rapp
 * 
 */
public class FoscamWebcam extends AbstractDeviceDriver implements PtzWebcam
{
  private static final Logger logger = LoggerFactory
      .getLogger(FoscamWebcam.class);

  private static final String MJPEG_URI = "videostream.cgi?resolution=32&rate=0";
  // URI constants
  private static final String PTZ_CONTROL_URI = "decoder_control.cgi?command=%d";
  private static final int PTZ_DOWN = 2;

  private static final int PTZ_LEFT = 4;
  private static final int PTZ_RIGHT = 6;
  private static final int PTZ_STOP_INCREMENT = 1;
  private static final int PTZ_UP = 0;
  private static final String SNAPSHOT_URI = "snapshot.cgi";

  private static final String USER_AGENT_SAFARI = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.28 (KHTML, like Gecko) Version/3.2.2 Safari/525.28.1";
  // Local variables
  private int direction;
  // Properties
  private String password;
  private String url;
  private String username;

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Webcam#getPassword()
   */
  @Override
  public String getPassword()
  {
    return password;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Webcam#getSnapshotUrl()
   */
  @Override
  public String getSnapshotUrl()
  {
    String tmp = String.format("%s/%s", url, SNAPSHOT_URI);
    logger.debug("Getting snapshot URL [{}]", tmp);
    return tmp;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Webcam#getUsername()
   */
  @Override
  public String getUsername()
  {
    return username;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Webcam#getVideoUrl()
   */
  @Override
  public String getVideoUrl()
  {
    String tmp = String.format("%s/%s", url, MJPEG_URI);
    logger.debug("Getting video URL [{}]", tmp);
    return tmp;
  }

  /**
   * HTTP GET
   * 
   * @param url
   *          URL to get
   */
  private void httpGet(String url)
  {
    HttpURLConnection urlConnection = null;

    logger.debug("Accessing URL [{}]", url);
    try
    {
      urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("User-Agent", USER_AGENT_SAFARI);
      // urlConnection.setDoInput(false);
      if (username != null && !"".equals(username))
      {
        Base64 base64 = new Base64();
        if (password == null)
          password = "";

        String auth = base64.encodeAsString((username + ":" + password)
            .getBytes());
        urlConnection.setRequestProperty("Authorization", "Basic " + auth);
      }

      urlConnection.connect();

      // Read the result from the server
      BufferedReader inputReader = new BufferedReader(new InputStreamReader(
          urlConnection.getInputStream()));

      if (inputReader.readLine().startsWith("ok"))
      {
        logger.info("Command successful");
      } else
      {
        logger.info("Command failed");
      }
    } catch (MalformedURLException e)
    {
      logger.warn("Malformed URL", e);
    } catch (ProtocolException e)
    {
      logger.warn("Protocol exception", e);
    } catch (IOException e)
    {
      logger.warn("Error connecting to URL", e);
    } finally
    {
      if (urlConnection != null)
        urlConnection.disconnect();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.PtzCamera#panDown()
   */
  @Override
  public void panDown()
  {
    logger.debug("Command: Pan down");
    direction = PTZ_DOWN;
    String panUrl = String.format("%s/%s", url, PTZ_CONTROL_URI);
    panUrl = String.format(panUrl, direction);
    httpGet(panUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.PtzCamera#panLeft()
   */
  @Override
  public void panLeft()
  {
    logger.debug("Command: Pan left");
    direction = PTZ_LEFT;
    String panUrl = String.format("%s/%s", url, PTZ_CONTROL_URI);
    panUrl = String.format(panUrl, direction);
    httpGet(panUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.PtzCamera#panRight()
   */
  @Override
  public void panRight()
  {
    logger.debug("Command: Pan right");
    direction = PTZ_RIGHT;
    String panUrl = String.format("%s/%s", url, PTZ_CONTROL_URI);
    panUrl = String.format(panUrl, direction);
    httpGet(panUrl);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.PtzWebcam#panStop()
   */
  @Override
  public void panStop()
  {
    logger.debug("Command: Pan stop");
    String panUrl = String.format("%s/%s", url, PTZ_CONTROL_URI);
    panUrl = String.format(panUrl, direction + PTZ_STOP_INCREMENT);
    httpGet(panUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.PtzCamera#panUp()
   */
  @Override
  public void panUp()
  {
    logger.debug("Command: Pan up");
    direction = PTZ_UP;
    String panUrl = String.format("%s/%s", url, PTZ_CONTROL_URI);
    panUrl = String.format(panUrl, direction);
    httpGet(panUrl);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.Webcam#setPassword(java.lang.String)
   */
  @Override
  public void setPassword(String password)
  {
    this.password = password;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.Webcam#setUrl(java.lang.String)
   */
  @Override
  public void setUrl(String url)
  {
    logger.debug("Setting URL [{}]", url);
    this.url = url;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.Webcam#setUsername(java.lang.String)
   */
  @Override
  public void setUsername(String username)
  {
    logger.debug("Setting username [{}]", username);
    this.username = username;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Camera#takeSnapshot()
   */
  @Override
  public void takeSnapshot()
  {
    // TODO Auto-generated method stub

  }
}
