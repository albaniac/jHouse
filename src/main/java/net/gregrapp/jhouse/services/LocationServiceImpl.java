/**
 * 
 */
package net.gregrapp.jhouse.services;

import java.util.Calendar;
import java.util.List;

import net.gregrapp.jhouse.models.User;
import net.gregrapp.jhouse.models.UserLocation;
import net.gregrapp.jhouse.repositories.UserLocationRepository;
import net.gregrapp.jhouse.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage location persistence
 * 
 * @author Greg Rapp
 * 
 */
@Service
public class LocationServiceImpl implements LocationService
{
  private static final Logger logger = LoggerFactory
      .getLogger(LocationServiceImpl.class);

  @Autowired
  private UserLocationRepository userLocationRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional
  public void persistUserLocation(String username, UserLocation userLocation)
  {
    logger.debug("Getting persistence entity for user [{}]", username);
    User user = userRepository.findByUsername(username);

    if (user != null)
    {
      userLocation.setUser(user);
      if (userLocation.getTimestamp() == null)
      {
        logger.debug("No timestamp specified, defaulting to current time");
        userLocation.setTimestamp(Calendar.getInstance());
      }
      
      logger.info("Persisting user location for user [{}]", username);
      userLocationRepository.save(userLocation);
      logger.info("Location successfully persisted for user [{}]", username);
    } else
    {
      logger.warn("Could not find an entity for user [{}]", username);
    }
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.services.LocationService#getNewestLocationForEachUser()
   */
  @Transactional(readOnly = true)
  @Override
  public List<UserLocation> getNewestLocationForEachUser()
  {
    return userLocationRepository.getNewestLocationForEachUser();
  }  
}
