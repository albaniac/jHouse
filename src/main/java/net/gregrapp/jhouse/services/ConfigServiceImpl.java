/**
 * 
 */
package net.gregrapp.jhouse.services;

import net.gregrapp.jhouse.models.Config;
import net.gregrapp.jhouse.repositories.ConfigRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Greg Rapp
 * 
 */

@Service
public class ConfigServiceImpl implements ConfigService
{
  private static final Logger logger = LoggerFactory
      .getLogger(ConfigServiceImpl.class);

  @Autowired
  private ConfigRepository configRepository;

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.services.config.ConfigService#get(java.lang.String,
   * java.lang.String)
   */
  @Override
  public String get(String namespace, String key)
  {
    logger.debug("Getting config value [{}.{}]", namespace, key);
    if (configRepository != null)
    {
      Config config = configRepository.findByNamespaceAndKey(namespace, key);

      if (config != null)
      {
        logger.debug("Got config object - {}", config.toString());
        return config.getValue();
      }
    }
    else
    {
      logger.warn("Unable to retrieve config value [{}.{}]", namespace, key);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.services.config.ConfigService#set(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void set(String namespace, String key, String value)
  {
    logger.debug("Setting config value [{}.{}] to [{}]", new Object[] {
        namespace, key, value });
    if (configRepository != null)
    {
      Config config = configRepository.findByNamespaceAndKey(namespace, key);

      if (config != null)
      {
        logger.debug("Config value already exists, updating entry");
        config.setValue(value);
        if (configRepository != null)
          configRepository.save(config);
      }
      else
      {
        logger.debug("Config value doesn't exist, creating new entry");
        Config newConfig = new Config();
        newConfig.setNamespace(namespace);
        newConfig.setKey(key);
        newConfig.setValue(value);
        if (configRepository != null)
          configRepository.save(newConfig);
      }
    }
    else
    {
      logger.warn("Unable to set config value [{}.{}] to [{}]", new Object[] {
          namespace, key, value });
    }

  }
}
