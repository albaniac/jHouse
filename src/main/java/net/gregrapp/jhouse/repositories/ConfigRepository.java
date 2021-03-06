/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import java.util.List;

import net.gregrapp.jhouse.models.Config;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface ConfigRepository extends JpaRepository<Config, Long>
{
  List<Config> findByNamespace(String namespace);
  Config findByNamespaceAndKey(String namespace, String key);
}
