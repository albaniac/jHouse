package net.gregrapp.jhouse.repositories;

import net.gregrapp.jhouse.models.UserLocation;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface UserLocationRepository extends JpaRepository<UserLocation, Long>, UserLocationRepositoryCustom
{
  //@Query("SELECT l FROM UserLocation c INNER JOIN (SELECT il.user_id,max(il.timestamp) FROM UserLocation il GROUP BY il.user_id) maxTime ON l.user_id = maxTime.user_id AND l.timestamp = maxTime.timestamp")
  //List<UserLocation> getNewestLocationForEachUser();
}
