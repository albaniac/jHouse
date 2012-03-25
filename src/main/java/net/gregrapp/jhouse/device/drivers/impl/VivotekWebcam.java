/**
 * 
 */
package net.gregrapp.jhouse.device.drivers.impl;

import net.gregrapp.jhouse.device.classes.Webcam;
import net.gregrapp.jhouse.device.drivers.types.AbstractDeviceDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vivotek Webcam Driver
 * 
 * @author Greg Rapp
 * 
 */
public class VivotekWebcam extends AbstractDeviceDriver implements Webcam
{
  private static final Logger logger = LoggerFactory
      .getLogger(VivotekWebcam.class);

  private static final String LOWRES_MJPEG_URI = "video3.mjpg";
  private static final String NORMALRES_MJPEG_URI = "video4.mjpg";
  private static final String SNAPSHOT_URI = "cgi-bin/viewer/video.jpg";

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
    return getVideoUrl(Resolution.NORMAL);
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.device.classes.Webcam#getVideoUrl(net.gregrapp.jhouse.device.classes.Webcam.Resolution)
   */
  @Override
  public String getVideoUrl(Resolution resolution)
  {
    String videoUrl = null;
    
    switch (resolution)
    {
    case LOW:
      videoUrl = String.format("%s/%s", url, LOWRES_MJPEG_URI);
      break;
    default:
      videoUrl = String.format("%s/%s", url, NORMALRES_MJPEG_URI);
      break;
    }
    
    logger.debug("Getting video URL [{}]", videoUrl);
    return videoUrl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.classes.Webcam#setPassword(java.lang.String)
   */
  @Override
  public void setPassword(String password)
  {
    this.password = password;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.device.classes.Webcam#setUrl(java.lang.String)
   */
  @Override
  public void setUrl(String url)
  {
    logger.debug("Setting URL [{}]", url);
    this.url = url;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.device.classes.Webcam#setUsername(java.lang.String)
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
