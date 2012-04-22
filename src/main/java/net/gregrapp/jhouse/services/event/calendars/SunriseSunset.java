/**
 * 
 */
package net.gregrapp.jhouse.services.event.calendars;

/**
 * Class to calculate the sunrise and sunset times at any location on the Earth.
 * 
 * Based on algorithm at
 * http://williams.best.vwh.net/sunrise_sunset_algorithm.htm

 * @author Greg Rapp
 *
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SunriseSunset
{

  static final public double OFFICIAL_ZENITH = 90.833333;
  static final public double CIVIL_ZENITH = 96;
  static final public double NAUTICAL_ZENITH = 102;
  static final public double ASTRONOMICAL_ZENITH = 108;

  /**
   * Gets the year of sunlight for an arbitrary point on the Earth. The sunlight
   * is represented
   * as a double.
   * 
   * @param year
   * @param latitude
   * @param longitude
   * @return Either 365 or 366 days of sunlight (depends on leap year)
   */
  public static List<Double> getYearOfSunlightForPoint(int year,
      double latitude, double longitude, TimeZone tz)
  {
    ArrayList<Double> returnList = new ArrayList<Double>();

    Calendar calendar = Calendar.getInstance();
    calendar.set(year, 11, 31); // month is 0 based
    int daysInYear = calendar.get(Calendar.DAY_OF_YEAR);

    calendar.set(year, 0, 1);
    for (int i = 0; i < daysInYear; i++)
    {
      returnList.add(new Double(getSunlightHours(latitude, longitude,
          calendar.getTime(), tz)));
      calendar.add(Calendar.HOUR_OF_DAY, 24);
    }
    return returnList;
  }

  /**
   * Returns the amount of sunlight in ms for a particular day at a particular
   * location
   * 
   * @param latitude
   * @param longitude
   * @param date
   * @return
   */
  public static long getSunlight(double latitude, double longitude, Date date)
  {
    return getSunlight(latitude, longitude, date,
        SunriseSunset.OFFICIAL_ZENITH, TimeZone.getDefault());
  }

  /**
   * Returns the amount of sunlight in ms for a particular day at a particular
   * location
   * 
   * @param latitude
   * @param longitude
   * @param date
   * @return
   */
  public static long getSunlight(double latitude, double longitude, Date date,
      TimeZone tz)
  {
    return getSunlight(latitude, longitude, date,
        SunriseSunset.OFFICIAL_ZENITH, tz);
  }

  /**
   * Returns the amount of sunlight in ms for a particular day at a particular
   * location
   * 
   * @param latitude
   *          location of sun calculation
   * @param longitude
   *          location of sun calculation
   * @param date
   *          date of sun calculation
   * @param zenith
   * @return amount of sunlight in ms
   */
  public static long getSunlight(double latitude, double longitude, Date date,
      double zenith, TimeZone tz)
  {
    Date sunrise = getSunrise(latitude, longitude, date, tz).getTime();
    Date sunset = getSunset(latitude, longitude, date, tz).getTime();

    long sunlight = 0;
    if (sunrise == null)
    {
      // return 0
    } else if (sunset == null)
    {
      sunlight = 86400000; // 24 hours
    } else
    {
      Calendar sunLightCal = Calendar.getInstance(tz);
      sunLightCal.setTime(sunrise);
      Calendar sunsetCal = Calendar.getInstance(tz);
      sunsetCal.setTime(sunset);

      long diff = sunsetCal.getTimeInMillis() - sunLightCal.getTimeInMillis();
      // long diffHours = diff / (60 * 60 * 1000);
      // long diffMinutes = diff / (60 * 1000);
      sunlight = diff;
    }

    return sunlight;
  }

  /**
   * Returns the amount of hours of sunlight for a particular location as a
   * double value. The whole number
   * is the number of hours, and the fraction is the minutes.
   * 
   * @param latitude
   * @param longitude
   * @param date
   * @return
   */
  public static double getSunlightHours(double latitude, double longitude,
      Date date)
  {
    return getSunlightHours(latitude, longitude, date,
        SunriseSunset.OFFICIAL_ZENITH, TimeZone.getDefault());
  }

  /**
   * Returns the amount of hours of sunlight for a particular location as a
   * double value. The whole number
   * is the number of hours, and the fraction is the minutes.
   * 
   * @param latitude
   *          location of sun calculation
   * @param longitude
   *          location of sun calculation
   * @param date
   *          date of sun calculation
   * @param zenith
   * @return double representation of the amount of sunlight in hours for a
   *         location
   */
  public static double getSunlightHours(double latitude, double longitude,
      Date date, double zenith, TimeZone tz)
  {
    long sunlight = getSunlight(latitude, longitude, date, tz);
    long minutes = Math.round(sunlight / (1000 * 60));
    return (minutes / 60.0);
  }

  private static double getSunlightHours(double latitude, double longitude,
      Date date, TimeZone tz)
  {
    return getSunlightHours(latitude, longitude, date,
        SunriseSunset.OFFICIAL_ZENITH, tz);
  }

  /**
   * Gets the sunset for today at a particular location
   * 
   * @param latitude
   * @param longitude
   * @return
   */
  public static Calendar getSunset(double latitude, double longitude)
  {
    return getSunset(latitude, longitude, new Date(),
        SunriseSunset.OFFICIAL_ZENITH, TimeZone.getDefault());
  }

  /**
   * Gets the sunset for today at a particular location with a particular zenith
   * 
   * @param latitude
   * @param longitude
   * @param zenith
   * @return GMT date of the rise. If the sun does not rise, returns null
   */
  public static Calendar getSunset(double latitude, double longitude, Date date)
  {
    return getSunset(latitude, longitude, date, SunriseSunset.OFFICIAL_ZENITH,
        TimeZone.getDefault());
  }

  /**
   * Gets the sunset for today at a particular location with a particular zenith
   * 
   * @param latitude
   * @param longitude
   * @param zenith
   * @return GMT date of the rise. If the sun does not rise, returns null
   */
  public static Calendar getSunset(double latitude, double longitude, Date date,
      TimeZone tz)
  {
    return getSunset(latitude, longitude, date, SunriseSunset.OFFICIAL_ZENITH,
        tz);
  }

  /**
   * Gets the time of a sunset for a particular location
   * 
   * @param latitude
   *          location of sunset
   * @param longitude
   *          location of sunset
   * @param date
   *          date of sunset
   * @return GMT date of the sunset. If the sun does not set, returns null
   */
  public static Calendar getSunset(double latitude, double longitude, Date date,
      double zenith, TimeZone tz)
  {
    double PiOver180 = Math.PI / 180;
    double One80OverPi = 180 / Math.PI;
    // Get the day of the year
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeZone(tz);
    calendar.setTime(date);
    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    int year = calendar.get(Calendar.YEAR);

    // convert the longitude to hour value
    double longitudeHour = longitude / 15;
    double settingTime = dayOfYear + ((18 - longitudeHour) / 24);

    // calculate the sun's mean anomaly
    double sunMeanAnomaly = (0.9856 * settingTime) - 3.289;

    // calculate the sun's true longitude
    double sunTrueLongitude = (sunMeanAnomaly
        + (1.916 * Math.sin(sunMeanAnomaly * PiOver180))
        + (0.020 * Math.sin(sunMeanAnomaly * 2 * PiOver180))
        + 282.634);
    if (sunTrueLongitude < 0.0)
    {
      sunTrueLongitude += 360;
    }
    if (sunTrueLongitude > 360)
    {
      sunTrueLongitude -= 360;
    }

    // calculate the Sun's right ascension
    double sunRightAscension = One80OverPi
        * Math.atan(0.91764 * Math.tan(PiOver180 * sunTrueLongitude));

    // right ascension value needs to be in the same quadrant as the sun's true
    // longitude
    double sunTrueLongitudeQuadrant = (Math.floor(sunTrueLongitude / 90) * 90);
    double sunRightAscensionQuadrant = (Math.floor(sunRightAscension / 90) * 90);
    sunRightAscension = sunRightAscension
        + (sunTrueLongitudeQuadrant - sunRightAscensionQuadrant);

    // convert right ascension value to hours
    sunRightAscension = sunRightAscension / 15;

    // calculate the sun's declination
    double sinDeclination = (0.39782 * (Math.sin(PiOver180 * sunTrueLongitude)));
    double cosDeclination = Math.cos(Math.asin(sinDeclination));

    // calculate the sun's local hour angle - use zenith as 90 50' cos(zenith) =
    // -0.01454
    double cosHour = ((-0.01454) - (sinDeclination * (Math.sin(PiOver180
        * latitude))))
        / (cosDeclination * Math.cos(PiOver180 * latitude));
    if (cosHour < -1)
    {
      // sun never sets
      return null;
    }

    // finish calculating local hour angle and convert to hours
    double localHourAngle = (One80OverPi * Math.acos(cosHour)) / 15;

    // calculate the local mean time of setting
    double localMeanTime = localHourAngle + sunRightAscension
        - (0.06571 * settingTime) - 6.622;

    // adjust back to UTC
    double utcTime = localMeanTime - longitudeHour;
    utcTime = (utcTime < 0) ? utcTime + 24 : utcTime;
    // utcTime = (utcTime > 24) ? utcTime - 24 : utcTime;

    // Get the GMT time
    int returnTime = (int) Math.floor(utcTime * 60 * 60 * 1000);

    calendar.clear();
    calendar.setTimeZone(tz);
    calendar.add(Calendar.MILLISECOND, returnTime);
    calendar.add(Calendar.MILLISECOND, tz.getOffset(date.getTime()));
    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
    calendar.set(Calendar.YEAR, year);

    return calendar;
  }

  /**
   * Gets the sunrise for today at a particular location
   * 
   * @param latitude
   * @param longitude
   * @return
   */
  public static Calendar getSunrise(double latitude, double longitude)
  {
    return getSunrise(latitude, longitude, new Date(),
        SunriseSunset.OFFICIAL_ZENITH, TimeZone.getDefault());
  }

  /**
   * Gets the sunrise for today at a particular location with a particular
   * zenith
   * 
   * @param latitude
   * @param longitude
   * @param zenith
   * @return GMT date of the rise. If the sun does not rise, returns null
   */
  public static Calendar getSunrise(double latitude, double longitude, Date date)
  {
    return getSunrise(latitude, longitude, date, SunriseSunset.OFFICIAL_ZENITH,
        TimeZone.getDefault());
  }

  public static Calendar getSunrise(double latitude, double longitude, Date date,
      TimeZone tz)
  {
    Calendar gmtDate = getSunrise(latitude, longitude, date,
        SunriseSunset.OFFICIAL_ZENITH, tz);
    return gmtDate; //new Date(gmtDate.getTime());
  }

  /**
   * Gets the sunrise for a particular date at a particular location
   * 
   * @param latitude
   * @param longitude
   * @param date
   * @param zenith
   * @return GMT date of the rise. If the sun does not rise, returns null
   */
  public static Calendar getSunrise(double latitude, double longitude, Date date,
      double zenith, TimeZone tz)
  {
    double PiOver180 = Math.PI / 180;
    double One80OverPi = 180 / Math.PI;
    // Get the day of the year
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTime(date);
    calendar.setTimeZone(tz);

    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    int year = calendar.get(Calendar.YEAR);

    // convert the longitude to hour value
    double longitudeHour = longitude / 15;
    double risingTime = dayOfYear + ((6 - longitudeHour) / 24);

    // calculate the sun's mean anomaly
    double sunMeanAnomaly = (0.9856 * risingTime) - 3.289;

    // calculate the sun's true longitude

    double sunTrueLongitude = (sunMeanAnomaly
        + (1.916 * Math.sin(sunMeanAnomaly * PiOver180))
        + (0.020 * Math.sin(sunMeanAnomaly * 2 * PiOver180))
        + 282.634);
    if (sunTrueLongitude < 0.0)
    {
      sunTrueLongitude += 360;
    }
    if (sunTrueLongitude > 360)
    {
      sunTrueLongitude -= 360;
    }

    // calculate the Sun's right ascension
    double sunRightAscension = One80OverPi
        * Math.atan(0.91764 * Math.tan(PiOver180 * sunTrueLongitude));

    // right ascension value needs to be in the same quadrant as the sun's true
    // longitude
    double sunTrueLongitudeQuadrant = (Math.floor(sunTrueLongitude / 90) * 90);
    double sunRightAscensionQuadrant = (Math.floor(sunRightAscension / 90) * 90);
    sunRightAscension = sunRightAscension
        + (sunTrueLongitudeQuadrant - sunRightAscensionQuadrant);

    // convert right ascension value to hours
    sunRightAscension = sunRightAscension / 15;

    // calculate the sun's declination
    double sinDeclination = (0.39782 * (Math.sin(PiOver180 * sunTrueLongitude)));
    double cosDeclination = Math.cos(Math.asin(sinDeclination));

    // calculate the sun's local hour angle - use zenith as 90 50' cos(zenith) =
    // -0.01454
    double cosHour = ((-0.01454) - (sinDeclination * (Math.sin(PiOver180
        * latitude))))
        / (cosDeclination * Math.cos(PiOver180 * latitude));
    if (cosHour > 1)
    {
      // sun never rises
      return null;
    }

    // finish calculating local hour angle and convert to hours
    double localHourAngle = (360 - (One80OverPi * Math.acos(cosHour))) / 15;

    // calculate the local mean time of rising
    double localMeanTime = localHourAngle + sunRightAscension
        - (0.06571 * risingTime) - 6.622;
    // localMeanTime = (localMeanTime < 0) ? localMeanTime + 24 : localMeanTime;
    // localMeanTime = (localMeanTime > 24) ? localMeanTime - 24 :
    // localMeanTime;

    // adjust back to UTC
    double utcTime = localMeanTime - longitudeHour;
    utcTime = (utcTime < 0) ? utcTime + 24 : utcTime;
    utcTime = (utcTime > 24) ? utcTime - 24 : utcTime;

    // Get the GMT time
    int returnTime = (int) Math.floor(utcTime * 60 * 60 * 1000);

    calendar.clear();
    calendar.setTimeZone(tz);
    calendar.add(Calendar.MILLISECOND, returnTime);
    calendar.add(Calendar.MILLISECOND, tz.getOffset(date.getTime()));
    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
    calendar.set(Calendar.YEAR, year);

    return calendar;
  }

}