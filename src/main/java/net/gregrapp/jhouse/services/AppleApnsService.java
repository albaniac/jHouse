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
   *          ID of user
   * @param alertBody
   *          alert message
   */
  public void send(long userId, String alertBody);

  /**
   * Send Apple Push Notification service message to a user's registered devices
   * 
   * @param userId
   *          ID of user
   * @param alertBody
   *          alert message
   * @param badge
   *          number for application icon badge (0 = no badge)
   */
  public void send(long userId, String alertBody, int badge);

  /**
   * @param userId
   *          ID of user
   * @param alertBody
   *          alert message
   * @param badge
   *          number for application icon badge (0 = no badge)
   * @param sound
   *          sound to play (default sound="default")
   */
  public void send(long userId, String alertBody, int badge, String sound);

  /**
   * @param userId
   *          ID of user
   * @param alertBody
   *          alert message
   * @param sound
   *          sound to play (default sound="default")
   */
  public void send(long userId, String alertBody, String sound);

  /**
   * Send Apple Push Notification service message
   * 
   * @param token
   *          device token
   * @param alertBody
   *          alert message
   * @param badge
   *          number for application icon badge (0 = no badge)
   * @param sound
   *          sound to play (default sound="default")
   */
  //public void send(String token, String alertBody, int badge, String sound);

}