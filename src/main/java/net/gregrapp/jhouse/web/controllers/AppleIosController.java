/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import net.gregrapp.jhouse.models.ApnsDevice;
import net.gregrapp.jhouse.services.AppleApnsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Greg Rapp
 * 
 */

@Controller
@RequestMapping("/controllers/appleios")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLE_IOS_USER')")
public class AppleIosController
{
  private static final Logger logger = LoggerFactory
      .getLogger(AppleIosController.class);

  @Autowired
  private AppleApnsService apnsService;

  /**
   * Get the currently logged in user
   * 
   * @return the logged in user's principal
   */
  private String getCurrentUsername()
  {
    String username = null;
    Object principal = SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    if (principal instanceof UserDetails)
    {
      username = ((UserDetails) principal).getUsername();
    } else
    {
      username = principal.toString();
    }

    return username;
  }

  @RequestMapping(value = "/device_tokens", method = RequestMethod.PUT)
  @PreAuthorize("isAuthenticated()")
  public void putDeviceToken(@RequestBody ApnsDevice device)
  {
    logger.debug("");

    String username = this.getCurrentUsername();

    if (username != null && !"".equals(username))
    {
      logger.debug("Updating APNs device token");
      logger
          .debug(
              "APNs device details [username={}, uuid={}, token={}, description={}]",
              new Object[] { username, device.getUuid(), device.getToken(), device.getDescription() });
      apnsService.putDevice(username, device.getUuid(), device.getToken(), device.getDescription());
    } else
    {
      logger.warn("Unable to retrieve username [{}] from SecurityContext", username);
    }
  }

}