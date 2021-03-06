/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.gregrapp.jhouse.models.UserLocation;
import net.gregrapp.jhouse.services.LocationService;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_LOCATION_USER')")
public class LocationController
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(LocationController.class);

  @Autowired
  private LocationService locationService;

  /**
   * Return methods available from this controller
   * 
   * @return dictionary of methods
   */
  /*
  @RequestMapping(method = RequestMethod.GET)
  public Model config()
  {
    Model model = new ExtendedModelMap();

    model.addAttribute("userUpdate", "controllers/location/user");
    model.addAttribute("newestAll", "controllers/location/newest");

    return model;
  }
*/
  /**
   * Get the currently logged in user
   * 
   * @return the logged in user's principal
   */
  private String getCurrentUsername()
  {
    logger.entry();
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

    logger.exit(username);
    return username;
  }

  /**
   * Add a user location
   * 
   * @param latitude
   * @param longitude
   */
  @RequestMapping(method = RequestMethod.PUT)
  public void putUserLocation(@RequestBody UserLocation userLocation)
  {
    logger.entry(userLocation);
    String username = this.getCurrentUsername();

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
      logger.warn("Unable to retrieve username [{}] from SecurityContext", username);
    }
    
    logger.exit();
  }
  
  /**
   * Get latest location for user
   */
  @RequestMapping(method = RequestMethod.GET)
  public Model getAllNewestLocation()
  {
    logger.entry();
    
    Model model = new ExtendedModelMap();
    List<HashMap<String,Object>> locations = new ArrayList<HashMap<String,Object>>();
    
    for (UserLocation location : locationService.getNewestLocationForEachUser())
    {
      HashMap<String,Object> locationMap = new HashMap<String,Object>();
      locationMap.put("firstname", location.getUser().getFirstName());
      locationMap.put("lastname", location.getUser().getLastName());
      locationMap.put("latitude", location.getLatitude());
      locationMap.put("longitude", location.getLongitude());
      locationMap.put("horizontalAccuracy", location.getHorizontalAccuracy());
      locationMap.put("timestamp", location.getTimestamp());
      locations.add(locationMap);
    }
    
    model.addAttribute("newestLocations", locations);
    
    logger.exit(model);
    return model;
  }
}
