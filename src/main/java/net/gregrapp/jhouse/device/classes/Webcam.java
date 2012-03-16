/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * @author Greg Rapp
 * 
 */
public interface Webcam extends DeviceClass
{
  /**
   * @return the password to authenticate to the webcam
   */
  public String getPassword();

  /**
   * @return the still image URL for this webcam
   */
  public String getSnapshotUrl();

  /**
   * @return the username to authenticate to the webcam
   */
  public String getUsername();

  /**
   * @return the video stream URL for this webcam
   */
  public String getVideoUrl();

  /**
   * Set the password to authenticate to the webcam
   * 
   * @param password
   */
  public void setPassword(String password);

  /**
   * Set the base URL for this webcam
   */
  public void setUrl(String url);
  
  /**
   * Set the username to authenticate to the webcam
   * 
   * @param username
   */
  public void setUsername(String username);

  /**
   * Take a snapshot
   */
  public void takeSnapshot();

}
