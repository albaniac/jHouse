/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import net.gregrapp.jhouse.models.DeviceBean;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface DeviceBeanRepository extends JpaRepository<DeviceBean, Long>
{
}
