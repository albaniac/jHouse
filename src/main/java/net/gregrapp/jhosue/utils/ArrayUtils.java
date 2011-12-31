package net.gregrapp.jhosue.utils;

public class ArrayUtils
{
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
