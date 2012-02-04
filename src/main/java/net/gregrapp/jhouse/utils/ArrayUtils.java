package net.gregrapp.jhouse.utils;

public class ArrayUtils
{
  /**
   * Convert an int array to base 16 and return as a String
   * 
   * @param values an int array
   * @return String of integers in hex [0x01,0x02,0x03] 
   */
  public static String toHexStringArray(int[] values)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[");
    for (int i = 0; i < values.length; i++)
    {
      buffer.append(String.format("%#04x", 0xFF & values[i]));
      buffer.append(",");
    }
    buffer.deleteCharAt(buffer.length()-1);
    buffer.append("]");
    return buffer.toString();
  }
}
