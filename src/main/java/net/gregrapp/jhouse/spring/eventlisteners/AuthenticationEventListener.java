/**
 * 
 */
package net.gregrapp.jhouse.spring.eventlisteners;

import java.util.Calendar;

import net.gregrapp.jhouse.models.User;
import net.gregrapp.jhouse.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Listen for and handle authentication events
 * 
 * @author Greg Rapp
 * 
 */
@Component
public class AuthenticationEventListener implements
    ApplicationListener<AbstractAuthenticationEvent>
{
  private static final Logger logger = LoggerFactory
      .getLogger(AuthenticationEventListener.class);

  @Autowired
  private UserRepository userRepository;

  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent authEvent)
  {
    if (authEvent instanceof AuthenticationSuccessEvent)
    {
      logger.debug("Authentication success event received");
      
      String username = null;
      if (authEvent.getAuthentication() instanceof UserDetails)
      {
        username = ((UserDetails) authEvent.getAuthentication()).getUsername();
      } else
      {
        username = authEvent.getAuthentication().toString();
      }


      logger.debug("Getting entity for user [{}]", username);
      User user = userRepository.findByUsername(username);

      if (user != null)
      {
        logger.debug("Updating last login time for user entity [{}]",
            username);
        user.setLastLogin(Calendar.getInstance());
        userRepository.save(user);
      }
    } else if (authEvent instanceof AuthenticationFailureBadCredentialsEvent)
    {
      String username = null;
      if (authEvent.getAuthentication() instanceof UserDetails)
      {
        username = ((UserDetails) authEvent.getAuthentication()).getUsername();
      } else
      {
        username = authEvent.getAuthentication().toString();
      }

      // TODO Add lockout after N attempts
      logger
          .warn(
              "***** Authentication failed event received for user [{}], SHOULD PROBABLY DO SOME LOCKOUT STUFF HERE",
              username);
    }
  }

}
