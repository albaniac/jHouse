/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import net.gregrapp.jhouse.models.ApnsDevice;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Apple Push Notification service device repository
 * 
 * @author Greg Rapp
 *
 */
public interface ApnsDeviceRepository extends JpaRepository<ApnsDevice, Long>
{
  ApnsDevice findByToken(String token);
  ApnsDevice findByUuid(String uuid);
}
