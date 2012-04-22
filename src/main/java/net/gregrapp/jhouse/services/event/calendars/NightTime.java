/**
 * 
 */
package net.gregrapp.jhouse.services.event.calendars;

import java.util.Date;

import org.drools.time.Calendar;

/**
 * Represents night time for a Drools KnowledgeSession
 * (after sunset, before sunrise)
 * 
 * @author Greg Rapp
 *
 */
public class NightTime implements Calendar
{
  private double latitude;
  private double longitude;

  /**
   * @param latitude
   * @param longitude
   */
  public NightTime(double latitude, double longitude)
  {
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  /* (non-Javadoc)
   * @see org.drools.time.Calendar#isTimeIncluded(long)
   */
  @Override
  public boolean isTimeIncluded(long timestamp)
  {
    long sunset = SunriseSunset.getSunset(latitude, longitude, new Date(timestamp)).getTimeInMillis();
    long sunrise = SunriseSunset.getSunrise(latitude, longitude, new Date(timestamp)).getTimeInMillis();

    if (timestamp > sunset
        || timestamp < sunrise)
      return true;
    else
      return false;
  }

}
