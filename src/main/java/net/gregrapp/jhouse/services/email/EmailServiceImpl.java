/**
 * 
 */
package net.gregrapp.jhouse.services.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.gregrapp.jhouse.services.config.ConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Greg Rapp
 * 
 */
@Service
public class EmailServiceImpl implements EmailService
{

  /*
   * Config options
   */
  private static final String CONFIG_NAMESPACE = "net.gregrapp.jhouse.services.email.EmailService";
  private static final String HOST = "HOST";
  private static final String PORT = "PORT";
  private static final String FROM = "FROM";

  private static final Logger logger = LoggerFactory
      .getLogger(EmailServiceImpl.class);

  private ConfigService configService;
  private Session mailSession;

  private Transport mailTransport;
  private String smtpFrom;
  private Properties smtpProps = new Properties();

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.email.EmailService#send(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void send(String recipient, String subject, String message)
  {
    this.send(new String[] { recipient }, subject, message);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.services.email.EmailService#send(java.lang.String[],
   * java.lang.String, java.lang.String)
   */
  @Override
  public void send(String[] recipients, String subject, String message)
  {
    if (mailTransport == null)
    {
      logger.warn("Unable to send email message, EmailService has not been initialized");
      return;
    }
    
    logger.trace("Building email message to [{}]", recipients);
    MimeMessage mimeMessage = null;
    mimeMessage = new MimeMessage(mailSession);
    try
    {
      logger.trace("Setting email from address to [{}]", this.smtpFrom);
      mimeMessage.setFrom(new InternetAddress(this.smtpFrom));
      logger.trace("Setting subject to [{}]", subject);
      mimeMessage.setSubject(subject);
      for (int i = 0; i < recipients.length; i++)
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(
            recipients[i]));

      logger.trace("Setting email body to [{}]", message);
      mimeMessage.setText(message);

      logger.trace("Connecting to SMTP host");
      mailTransport.connect();

      logger.debug("Sending email message");
      Transport.send(mimeMessage);
    } catch (AddressException e)
    {
      logger
          .warn("Error sending email message, invalid email address specified");
    } catch (MessagingException e)
    {
      logger.warn("Error sending email message", e);
    } finally
    {
      try
      {
        logger.trace("Closing SMTP transport");
        mailTransport.close();
      } catch (MessagingException e)
      {
        logger.error("Error closing SMTP transport", e);
      }
    }
  }

  /**
   * 
   */
  @Autowired
  public void setConfigService(ConfigService configService)
  {
    this.configService = configService;

    String host = this.configService.get(CONFIG_NAMESPACE, HOST);
    if (host == null)
    {
      logger.error("Unable to initialize EmailService, SMTP HOST not set");
      return;
    }
    
    smtpProps.setProperty("mail.smtp.host", host);

    String port = this.configService.get(CONFIG_NAMESPACE, PORT);
    smtpProps.setProperty("mail.smtp.port", port);

    String from = this.configService.get(CONFIG_NAMESPACE, FROM);

    mailSession = Session.getDefaultInstance(smtpProps);

    try
    {
      mailTransport = mailSession.getTransport("smtp");
    } catch (NoSuchProviderException e)
    {
      logger.warn("Error getting mail SMTP transport", e);
    }

    this.smtpFrom = from;
  }

}
