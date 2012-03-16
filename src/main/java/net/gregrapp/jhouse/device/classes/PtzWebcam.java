/**
 * 
 */
package net.gregrapp.jhouse.device.classes;

/**
 * @author grapp
 * 
 */
public interface PtzWebcam extends Webcam
{
  public void panUp();

  public void panDown();

  public void panLeft();

  public void panRight();
  
  public void panStop();  
}
