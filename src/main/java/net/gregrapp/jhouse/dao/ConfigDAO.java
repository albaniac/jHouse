/**
 * 
 */
package net.gregrapp.jhouse.dao;

import java.util.List;

import net.gregrapp.jhouse.models.Config;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Greg Rapp
 *
 */
public interface ConfigDAO extends JpaRepository<Config, Long>
{
  public List<Config> findByNamespace(String namespace);
  public Config findByNamespaceAndOpt(String namespace, String opt);
}
