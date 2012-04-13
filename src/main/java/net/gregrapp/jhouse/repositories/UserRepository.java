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
public interface UserRepository extends JpaRepository<User, Long>
{
  User findByUsername(String username);
}
