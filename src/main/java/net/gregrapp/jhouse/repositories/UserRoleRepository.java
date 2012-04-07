/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import net.gregrapp.jhouse.models.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Long>
{
}
