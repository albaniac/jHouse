/**
 * 
 */
package net.gregrapp.jhouse.events;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Greg Rapp
 *
 */
public abstract class AbstractEvent implements Event, Serializable
{
  private static final long serialVersionUID = 3223094829023872387L;
  protected Calendar time;

  /**
   * @return the event time
   */
  public Calendar getTime()
  {
    return time;
  }
}
