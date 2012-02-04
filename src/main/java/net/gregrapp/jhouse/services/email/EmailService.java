package net.gregrapp.jhouse.services.email;

public interface EmailService
{

  public abstract void send(String recipient, String subject, String message);

  public abstract void send(String[] recipients, String subject, String message);
  
}