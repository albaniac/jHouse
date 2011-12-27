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

  /// <summary>
  /// Weekday 1: monday...7: sunday   
  /// </summary>
  int weekday;
  /// <summary>
  /// Hour 0...23   
  /// </summary>
  int hour;
  /// <summary>
  ///  Minute 0...59  
  /// </summary>
  int minute;

  /// <summary>
  /// Initialize Time
  /// </summary>
  public Time()
  {
    this.minute = this.hour = this.weekday = 0;
  }

  /// <summary>
  /// Set Time 
  /// </summary>
  /// <param name="weekday">mon - sun</param>
  /// <param name="hour">0 - 23</param>
  /// <param name="minute">0 - 59</param>
  public Time(int weekday, int hour, int minute)
  {
    this.minute = minute;
    this.hour = hour;
    this.weekday = weekday;
  }
  
  /// <summary>
  /// Set Time
  /// </summary>
  /// <param name="cal">Calendar with day, hour and minute</param>
  public Time(Calendar cal)
  {
    this.minute = (int)cal.get(Calendar.MINUTE);
    this.hour = (int)cal.get(Calendar.HOUR);
    this.weekday = (int)cal.get(Calendar.DAY_OF_WEEK);
  }
}
