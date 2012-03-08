/**
 * 
 */
package net.gregrapp.jhouse.events;

import java.io.Serializable;
import java.util.Calendar;

/**
 * An application event
 * 
 * @author Greg Rapp
 *
 */
public abstract class AbstractEvent implements Event, Serializable
{
  private static final long serialVersionUID = 3223094829023872387L;
  protected Calendar time;

  public AbstractEvent()
  {
    this.time = Calendar.getInstance();
  }
  
  /**
   * @return the event time
   */
  public long getTime()
  {
    return time.getTimeInMillis();
  }
}
