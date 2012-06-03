/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

/**
 * @author Greg Rapp
 * 
 */
public interface Envisalink2DSCallback
{
  /**
   * Envisalink 2DS is requesting a security code for the previous request
   * 
   */
  public void codeRequired();

  /**
   * @param partition
   */
  public void invalidAccessCode(int partition);

  /**
   * Envisalink 2DS login session
   */
  public void loginInteraction(int type);

  /**
   * @param partition
   * @param mode
   */
  public void paritionArmed(int partition, int mode);

  /**
   * @param partition
   */
  public void partitionBusy(int partition);

  /**
   * @param partition
   */
  public void partitionDisarmed(int partition);

  /**
   * @param partition
   */
  public void partitionEntryDelay(int partition);

  /**
   * @param partition
   */
  public void partitionExitDelay(int partition);

  /**
   * @param partition
   */
  public void partitionFailedToArm(int partition);

  /**
   * @param partition
   */
  public void partitionInAlarm(int partition);

  /**
   * @param partition
   */
  public void partitionNotReady(int partition);

  /**
   * @param partition
   */
  public void partitionReady(int partition);

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
