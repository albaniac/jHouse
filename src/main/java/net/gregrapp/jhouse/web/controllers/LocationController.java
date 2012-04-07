/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import net.gregrapp.jhouse.models.UserLocation;
import net.gregrapp.jhouse.services.LocationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Greg Rapp
 * 
 */

@Controller
@Scope("request")
@RequestMapping("/controllers/location")
public class LocationController
{
  private static final Logger logger = LoggerFactory
      .getLogger(LocationController.class);

  @Autowired
  private LocationService locationService;

  /**
   * Return methods available from this controller
   * 
   * @return dictionary of methods
   */
  @RequestMapping(value = "/config", method = RequestMethod.GET)
  public Model config()
  {
    Model model = new ExtendedModelMap();

    model.addAttribute("userUpdate", "controllers/location/userUpdate/");

    return model;
  }

  /**
   * Get the currently logged in user
   * 
   * @return the logged in user's principal
   */
  private String getPrincipal()
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

  /**
   * Update user location
   * 
   * @param latitude
   * @param longitude
   */
  @RequestMapping(value = "/userUpdate", method = RequestMethod.POST)
  public void userUpdate(@RequestBody UserLocation userLocation)
  {
    String username = this.getPrincipal();

    if (username != null && !"".equals(username))
    {
      if (locationService != null)
      {
        locationService.persistUserLocation(username, userLocation);
      } else
      {
        logger.warn("Location service is null");
      }

    } else
    {
      logger.warn("Unable to retrieve username from SecurityContext");
    }
  }
}
