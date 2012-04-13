/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import java.util.List;

import net.gregrapp.jhouse.models.UserLocation;

/**
 * @author Greg Rapp
 *
 */
public interface UserLocationRepositoryCustom
{

  List<UserLocation>getNewestLocationForEachUser();
}
