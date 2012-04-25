package net.gregrapp.jhouse.services;

public interface AppleApnsService
{

  /**
   * Add/update APNs device record
   * 
   * @param username
   * @param uuid
   * @param token
   * @param description
   */
  public void putDevice(String username, String uuid, String token,
      String description);

  /**
   * Send Apple Push Notification service message to a user's registered devices
   * 
   * @param userId
   *          ID of user model
   * @param alertBody
   *          alert message
   */
  public void send(long userId, String alertBody);

  /**
   * Send Apple Push Notification service message to a user's registered devices
   * 
   * @param userId
   *          ID of user model
   * @param alertBody
   *          alert message
   * @param badge
   *          number for application icon badge (0 = no badge)
   */
  public void send(long userId, String alertBody, int badge);

  /**
   * Send Apple Push Notification service message
   * 
   * @param token
   *          device token
   * @param alertBody
   *          alert message
   * @param badge
   *          number for application icon badge (0 = no badge)
   */
  public abstract void send(String token, String alertBody, int badge);
}