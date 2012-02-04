//////////////////////////////////////////////////////////////////////////////////////////////// 
//
//          #######
//          #   ##    ####   #####    #####  ##  ##   #####
//             ##    ##  ##  ##  ##  ##      ##  ##  ##
//            ##  #  ######  ##  ##   ####   ##  ##   ####
//           ##  ##  ##      ##  ##      ##   #####      ##
//          #######   ####   ##  ##  #####       ##  #####
//                                           #####
//          Z-Wave, the wireless language.
//
//          Copyright Zensys A/S, 2005
//
//          All Rights Reserved
//
//          Description:   
//
//          Author:   Morten Damsgaard, Linkage A/S
//
//          Last Changed By:  $Author: jrm $
//          Revision:         $Revision: 1.2 $
//          Last Changed:     $Date: 2006/07/24 09:14:16 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.Calendar;

/**
 * @author Greg Rapp
 * 
 */
public class Time
{
  /**
   * Weekday 1: monday...7: sunday
   */
  int weekday;

  /**
   * Hour 0...23
   */
  int hour;

  /**
   * Minute 0...59
   */
  int minute;

  /**
   * Initialize Time
   */
  public Time()
  {
    this.minute = this.hour = this.weekday = 0;
  }

  /**
   * Set Time
   * 
   * @param weekday
   *          mon - sun
   * @param hour
   *          0 - 23
   * @param minute
   *          0 - 59
   */
  public Time(int weekday, int hour, int minute)
  {
    this.minute = minute;
    this.hour = hour;
    this.weekday = weekday;
  }

  /**
   * Set Time
   * 
   * @param cal
   *          Calendar with day, hour and minute
   */
  public Time(Calendar cal)
  {
    this.minute = (int) cal.get(Calendar.MINUTE);
    this.hour = (int) cal.get(Calendar.HOUR);
    this.weekday = (int) cal.get(Calendar.DAY_OF_WEEK);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return String.format("Weekday: {} Hour: {} Minute: {}", weekday, hour,
        minute);
  }
}
