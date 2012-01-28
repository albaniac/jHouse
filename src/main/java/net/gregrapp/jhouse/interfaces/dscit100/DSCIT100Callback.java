/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

/**
 * @author Greg Rapp
 * 
 */
public interface DSCIT100Callback
{
  /**
   * @param zone
   * @param label
   */
  public void broadcastLabels(int zone, String label);

  /**
   * IT100 is requesting a security code for the previous request
   * 
   * @param partition
   *          partition originating the request
   */
  public void codeRequired(int partition);

  /**
   * @param partition
   * @param mode
   */
  public void paritionArmed(int partition, int mode);

  /**
   * @param partition
   */
  public void partitionDisarmed(int partition);

  /**
   * @param partition
   */
  public void specialClosing(int partition);

  /**
   * @param partition
   * @param userCode
   */
  public void userClosing(int partition, int userCode);

  /**
   * @param partition
   * @param userCode
   */
  public void userOpening(int partition, int userCode);

  /**
   * @param partition
   * @param zone
   */
  public void zoneAlarm(int partition, int zone);

  /**
   * @param partition
   * @param zone
   */
  public void zoneAlarmRestore(int partition, int zone);

  /**
   * @param zone
   */
  public void zoneOpen(int zone);

  /**
   * @param zone
   */
  public void zoneRestore(int zone);
}
