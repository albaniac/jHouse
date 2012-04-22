package net.gregrapp.jhouse.services;

public interface EmailService
{

  public abstract void send(String recipient, String subject, String message);

  public abstract void send(String[] recipients, String subject, String message);
  
}