/**
 * 
 */
package net.gregrapp.jhouse.events;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
  private Map<String, String> vars = new HashMap<String, String>();

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

  /**
   * Add event variable
   * 
   * @param key
   *          variable name
   * @param value
   *          variable value
   */
  public void addVar(String key, String value)
  {
    vars.put(key, value);
  }

  /**
   * Get event variable
   * 
   * @param key
   *          variable name
   * @return variable value
   */
  public String getVar(String key)
  {
    if (vars.containsKey(key))
      return vars.get(key);
    else
      return null;
  }
}
