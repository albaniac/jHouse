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
//          Revision:         $Revision: 1.7 $
//          Last Changed:     $Date: 2007/02/15 11:34:47 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Greg Rapp
 * 
 */
class TimeoutEvent
{
  private ScheduledExecutorService retransmissionTimeoutExecutor;
  private ScheduledFuture<?> retransmissionTimeoutExecutorFuture;

  private boolean timertimeout;

  public TimeoutEvent()
  {
    this.retransmissionTimeoutExecutor = Executors
        .newSingleThreadScheduledExecutor();
  }

  public boolean wait(int timeout)
  {
    // Reset the timer...
    if (retransmissionTimeoutExecutorFuture != null)
      retransmissionTimeoutExecutorFuture.cancel(false);

    retransmissionTimeoutExecutorFuture = retransmissionTimeoutExecutor
        .schedule(new Runnable()
        {
          public void run()
          {
            timerCallbackHandler();
          }
        }, timeout, TimeUnit.MILLISECONDS);

    // Clear the timeout flag...
    timertimeout = false;

    // Wait for the event to be signaled...
    try
    {
      this.wait();
    } catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Disable the timer
    retransmissionTimeoutExecutorFuture.cancel(false);

    return !timertimeout;
  }

  public void set()
  {
    this.notify();
  }

  public void timerCallbackHandler()
  {
    timertimeout = true;
    this.notify();
  }
}
