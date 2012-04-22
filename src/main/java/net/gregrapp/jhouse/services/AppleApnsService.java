package net.gregrapp.jhouse.services;

public interface AppleApnsService
{

  /**
   * Send Apple Push Notification service message
   * @param token deivce token
   * @param alertBody alert message
   * @param badge number for application icon badge (0 removes badge)
   */
  public abstract void send(String token, String alertBody, int badge);

}