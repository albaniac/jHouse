/**
 * 
 */
package net.gregrapp.jhouse.services.event.calendars;

import java.util.Date;

import org.drools.time.Calendar;

/**
 * Represents day time for a Drools KnowledgeSession
 * (after sunrise, before sunset)
 * 
 * @author Greg Rapp
 * 
 */
public class DayTime implements Calendar
{
  private double latitude;
  private double longitude;

  /**
   * @param latitude
   * @param longitude
   */
  public DayTime(double latitude, double longitude)
  {
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.drools.time.Calendar#isTimeIncluded(long)
   */
  @Override
  public boolean isTimeIncluded(long timestamp)
  {
    long sunset = SunriseSunset.getSunset(latitude, longitude, new Date(timestamp)).getTimeInMillis();
    long sunrise = SunriseSunset.getSunrise(latitude, longitude, new Date(timestamp)).getTimeInMillis();

    if (timestamp < sunset
        && timestamp > sunrise)
      return true;
    else
      return false;
  }
}
