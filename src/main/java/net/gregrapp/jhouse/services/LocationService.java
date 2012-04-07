/**
 * 
 */
package net.gregrapp.jhouse.services;

import net.gregrapp.jhouse.models.UserLocation;

/**
 * Service to manage location persistence
 * 
 * @author Greg Rapp
 * 
 */
public interface LocationService
{

  public void persistUserLocation(String username, UserLocation userLocation);
}
