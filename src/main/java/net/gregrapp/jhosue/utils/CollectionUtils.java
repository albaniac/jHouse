/**
 * 
 */
package net.gregrapp.jhosue.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Greg Rapp
 *
 */
public class CollectionUtils
{
  /**
   * @param integers 
   * @return
   */
  public static int[] toIntArray(Collection<Integer> integers)
  {
      int[] ret = new int[integers.size()];
      Iterator<Integer> iterator = integers.iterator();
      for (int i = 0; i < ret.length; i++)
      {
          ret[i] = iterator.next().intValue();
      }
      return ret;
  }
}
