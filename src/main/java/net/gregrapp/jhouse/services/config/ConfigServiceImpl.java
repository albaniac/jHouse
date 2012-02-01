/**
 * 
 */
package net.gregrapp.jhouse.services.config;

import net.gregrapp.jhouse.dao.ConfigDAO;
import net.gregrapp.jhouse.models.Config;

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
  private ConfigDAO configDao;

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.services.config.ConfigService#get(java.lang.String,
   * java.lang.String)
   */
  @Override
  public String get(String namespace, String opt)
  {
    logger.debug("Getting config value [{}.{}]", namespace, opt);
    if (configDao != null)
    {
      Config config = configDao.findByNamespaceAndOpt(namespace, opt);

      if (config != null)
      {
        logger.debug("Got config object - {}", config.toString());
        return config.getVal();
      }
    }
    else
    {
      logger.warn("Unable to retrieve config value [{}.{}]", namespace, opt);
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
  public void set(String namespace, String opt, String val)
  {
    logger.debug("Setting config value [{}.{}] to [{}]", new Object[] {
        namespace, opt, val });
    if (configDao != null)
    {
      Config config = configDao.findByNamespaceAndOpt(namespace, opt);

      if (config != null)
      {
        logger.debug("Config value already exists, updating entry");
        config.setVal(val);
        if (configDao != null)
          configDao.save(config);
      }
      else
      {
        logger.debug("Config value doesn't exist, creating new entry");
        Config newConfig = new Config();
        newConfig.setNamespace(namespace);
        newConfig.setOpt(opt);
        newConfig.setVal(val);
        if (configDao != null)
          configDao.save(newConfig);
      }
    }
    else
    {
      logger.warn("Unable to set config value [{}.{}] to [{}]", new Object[] {
          namespace, opt, val });
    }

  }

}
