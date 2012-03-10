/**
 * 
 */
package net.gregrapp.jhouse.events;

/**
 * Event representing a significant time
 * 
 * @author Greg Rapp
 * 
 */
public class TimeEvent extends AbstractEvent
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public enum TimeEventType
  {
    NOON, SUNRISE, SUNSET
  }

  private TimeEventType eventType;

  /**
   * @param eventType
   */
  public TimeEvent(TimeEventType eventType)
  {
    super();
    this.eventType = eventType;
  }

  /**
   * @return the eventType
   */
  public TimeEventType getEventType()
  {
    return eventType;
  }
}
