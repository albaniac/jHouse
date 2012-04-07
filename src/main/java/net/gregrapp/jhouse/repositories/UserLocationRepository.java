package net.gregrapp.jhouse.repositories;

import net.gregrapp.jhouse.models.UserLocation;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface UserLocationRepository extends JpaRepository<UserLocation, Long>
{

}
