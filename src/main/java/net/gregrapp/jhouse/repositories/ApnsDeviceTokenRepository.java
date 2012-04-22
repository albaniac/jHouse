/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import net.gregrapp.jhouse.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface ApnsDeviceTokenRepository extends JpaRepository<ApnsDevice, Long>
{
  ApnsDevice findByToken(String token);
}
