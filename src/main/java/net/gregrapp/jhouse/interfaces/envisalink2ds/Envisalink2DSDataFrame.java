/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

/**
 * @author Greg Rapp
 * 
 */
public class Envisalink2DSDataFrame
{
  // End of frame CRLF
  private static final String EOF = "\r\n";
  private String checksum;
  private String command;

  private String data;

  /**
   * 
   */
  public Envisalink2DSDataFrame(String rawFrame)
      throws Envisalink2DSDataFrameException
  {
    parseRawFrame(rawFrame);
  }

  /**
   * @param command
   * @param data
   */
  public Envisalink2DSDataFrame(String command, String data)
  {
    this.command = command;
    this.data = data;
    this.checksum = calculateChecksum();
  }

  private String calculateChecksum()
  {
    int checksum = 0;

    for (int i = 0; i < command.length(); i++)
    {
      checksum += command.charAt(i);
    }

    for (int j = 0; j < data.length(); j++)
    {
      checksum += data.charAt(j);
    }

    String chk = Integer.toHexString(checksum).toUpperCase();
    return chk.substring(chk.length() - 2);
  }

  /**
   * @return the checksum
   */
  public String getChecksum()
  {
    return checksum;
  }

  /**
   * @return the command
   */
  public String getCommand()
  {
    return command;
  }

  /**
   * @return the data
   */
  public String getData()
  {
    return data;
  }

  public String getFrame()
  {
    StringBuffer frame = new StringBuffer();
    frame.append(command);
    frame.append(data);
    frame.append(calculateChecksum());
    frame.append(EOF);

    return frame.toString();
  }

  public String getFrameNoCrlf()
  {
    String frame = getFrame();
    if (frame.endsWith(EOF))
    {
      return frame.substring(0, frame.length() - EOF.length());
    } else
    {
      return frame;
    }
  }

  public boolean isValidChecksum()
  {
    return checksum.equals(calculateChecksum());
  }

  private void parseRawFrame(String rawFrame)
      throws Envisalink2DSDataFrameException
  {
    try
    {
      command = rawFrame.substring(0, 3);
      data = rawFrame.substring(3, rawFrame.length() - 2);
      checksum = rawFrame.substring(rawFrame.length() - 2);
    } catch (StringIndexOutOfBoundsException e)
    {
      throw new Envisalink2DSDataFrameException("error parsing raw frame");
    }
  }

  /**
   * @param checksum
   *          the checksum to set
   */
  public void setChecksum(String checksum)
  {
    this.checksum = checksum;
  }

  /**
   * @param command
   *          the command to set
   */
  public void setCommand(String command)
  {
    this.command = command;
  }

  /**
   * @param data
   *          the data to set
   */
  public void setData(String data)
  {
    this.data = data;
  }
}
