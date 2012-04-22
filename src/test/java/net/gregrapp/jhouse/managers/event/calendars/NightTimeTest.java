package net.gregrapp.jhouse.managers.event.calendars;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.gregrapp.jhouse.services.event.calendars.NightTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class NightTimeTest
{

  @Test
  public void testIsTimeIncluded()
  {
    NightTime nightTime = new NightTime(40.05758, -82.87792);
    
    // Check that 12:00 noon returns false (not night time)
    assertFalse(nightTime.isTimeIncluded(1327683600000L));
    
    // Check that 1:00 am returns true (is night time)
    assertTrue(nightTime.isTimeIncluded(1327644000000L));
    
    // Check that 11:00 pm returns true (is night time)
    assertTrue(nightTime.isTimeIncluded(1327723200000L));
  }

}
