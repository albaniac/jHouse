/**
 * 
 */
package net.gregrapp.jhouse.managers.event.calendars;

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

  /* (non-Javadoc)
   * @see org.drools.time.Calendar#isTimeIncluded(long)
   */
  @Override
  public boolean isTimeIncluded(long timestamp)
  {
    // TODO Make LAT/LON in sunset/sunrise calculations configurable
    long sunset = SunriseSunset.getSunset(40.05758, -82.87792, new Date(timestamp)).getTime();
    long sunrise = SunriseSunset.getSunrise(40.05758, -82.87792, new Date(timestamp)).getTime();

    if (timestamp > sunset
        || timestamp < sunrise)
      return true;
    else
      return false;
  }

}
