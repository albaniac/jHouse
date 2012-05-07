/**
 * 
 */
package net.gregrapp.jhouse.services;

import java.util.List;

import javax.annotation.PostConstruct;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.interfaces.Interface;
import net.gregrapp.jhouse.models.DeviceBean;
import net.gregrapp.jhouse.models.DriverBean;
import net.gregrapp.jhouse.models.InterfaceBean;
import net.gregrapp.jhouse.models.TransportBean;
import net.gregrapp.jhouse.repositories.DeviceBeanRepository;
import net.gregrapp.jhouse.repositories.DriverBeanRepository;
import net.gregrapp.jhouse.repositories.InterfaceBeanRepository;
import net.gregrapp.jhouse.repositories.TransportBeanRepository;
import net.gregrapp.jhouse.transports.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Greg Rapp
 * 
 */
@Service
@Transactional
public class BeanLifecycleServiceImpl implements BeanLifecycleService
{
  private static final Logger logger = LoggerFactory
      .getLogger(BeanLifecycleServiceImpl.class);

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private DeviceBeanRepository deviceBeanRepository;

  @Autowired
  private DriverBeanRepository driverBeanRepository;

  @Autowired
  private InterfaceBeanRepository interfaceBeanRepository;

  @Autowired
  private TransportBeanRepository transportBeanRepository;

  private DefaultListableBeanFactory beanFactory;

  /**
   * Initialize the bean life cycle service
   */
  @PostConstruct
  public void init()
  {
    beanFactory = (DefaultListableBeanFactory) appContext
        .getAutowireCapableBeanFactory();

    // Call method through Spring proxy so that transactions are created
    getSpringProxy().instantiateTransportBeans();
    getSpringProxy().instantiateInterfaceBeans();
    getSpringProxy().instantiateDriverBeans();
    getSpringProxy().instantiateDeviceBeans();
  }

  /**
   * Return Spring proxy of this service
   * 
   * @return service proxy
   */
  private BeanLifecycleService getSpringProxy()
  {
    return appContext.getBean(BeanLifecycleService.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.lifecycle.BeanLifecycleService#
   * instantiateDeviceBeans()
   */
  @Override
  public void instantiateDeviceBeans()
  {
    logger.info("Instantiating device beans");

    logger.debug("Retrieving all device beans from database");
    List<DeviceBean> beans = deviceBeanRepository.findAll();

    if (beans != null && beans.size() > 0)
    {
      if (beanFactory != null)
      {
        logger.debug("Iterating enabled device beans");
        for (DeviceBean bean : beans)
        {
          if (bean.getDriver() != null
              && bean.getDriver().getDriverInterface() != null
              && bean.getDriver().getDriverInterface().getTransport() != null
              && bean.getDriver().getDriverInterface().getTransport()
                  .isEnabled() == false)
          {
            logger.warn(
                "Transport disabled, cannot instantiate device bean [{}]",
                bean.toString());
            continue;
          }

          if (bean.getDriver() != null
              && bean.getDriver().getDriverInterface() != null
              && bean.getDriver().getDriverInterface().isEnabled() == false)
          {
            logger.warn(
                "Interface disabled, cannot instantiate device bean [{}]",
                bean.toString());
            continue;
          }

          if (bean.getDriver() != null && bean.getDriver().isEnabled() == false)
          {
            logger.warn("Driver disabled, cannot instantiate device bean [{}]",
                bean.toString());
            continue;
          }

          if (bean.isEnabled())
          {
            logger.info("Building bean definition for device bean [{}]",
                bean.toString());

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

            ConstructorArgumentValues args = new ConstructorArgumentValues();
            args.addIndexedArgumentValue(0, bean.getId());
            args.addIndexedArgumentValue(1, bean.getValueIndex());

            if (bean.getDriver() != null)
            {
              logger
                  .debug("Device is a DriverDevice, getting driver bean reference");
              DeviceDriver driver = (DeviceDriver) appContext
                  .getBean(DRIVER_BEAN_NAME_PREFIX + bean.getDriver().getId());
              args.addIndexedArgumentValue(2, driver);
            }

            beanDefinition.setConstructorArgumentValues(args);
            
            MutablePropertyValues propertyValues = new MutablePropertyValues();
            propertyValues.addPropertyValue("name", bean.getName());
            if (bean.getLocation() != null && bean.getLocation().getFloor() != null)
              propertyValues.addPropertyValue("floor", bean.getLocation().getFloor());
            if (bean.getLocation() != null && bean.getLocation().getRoom() != null)
              propertyValues.addPropertyValue("room", bean.getLocation().getRoom());            
            beanDefinition.setPropertyValues(propertyValues);
            
            beanDefinition.setBeanClassName(bean.getKlass());
            beanDefinition.setLazyInit(false);
            beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            logger.debug("Registering device bean definition");
            beanFactory.registerBeanDefinition(
                DEVICE_BEAN_NAME_PREFIX + bean.getId(), beanDefinition);
          } else
          {
            logger
                .info("Device bean not enabled, skipping [{}]", bean.toString());
          }
        }
        beanFactory.preInstantiateSingletons();
      } else
      {
        logger.warn("Cannot register device beans, BeanFactory is null");
      }
    } else
    {
      logger.warn("No device beans found in database");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.lifecycle.BeanLifecycleService#
   * instantiateDriverBeans()
   */
  @Override
  public void instantiateDriverBeans()
  {
    logger.info("Instantiating device driver beans");

    logger.debug("Retrieving all device driver beans from database");
    List<DriverBean> beans = driverBeanRepository.findAll();

    if (beans != null && beans.size() > 0)
    {
      if (beanFactory != null)
      {
        logger.debug("Iterating enabled interface beans");
        for (DriverBean bean : beans)
        {
          if (bean.getDriverInterface() != null
              && bean.getDriverInterface().getTransport() != null
              && bean.getDriverInterface().getTransport().isEnabled() == false)
          {
            logger.warn(
                "Transport disabled, cannot instantiate driver bean [{}]",
                bean.toString());
            continue;
          }

          if (bean.getDriverInterface() != null
              && bean.getDriverInterface().isEnabled() == false)
          {
            logger.warn(
                "Interface disabled, cannot instantiate driver bean [{}]",
                bean.toString());
            continue;
          }

          if (bean.isEnabled())
          {
            logger.info("Building bean definition for device driver bean [{}]",
                bean.toString());
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

            if (bean.getDriverInterface() != null)
            {
              logger.debug("Getting driver interface bean reference");
              Interface deviceInterface = (Interface) appContext
                  .getBean(INTERFACE_BEAN_NAME_PREFIX
                      + bean.getDriverInterface().getId());

              ConstructorArgumentValues args = new ConstructorArgumentValues();
              args.addGenericArgumentValue(deviceInterface);
              beanDefinition.setConstructorArgumentValues(args);
            }

            beanDefinition.setBeanClassName(bean.getKlass());

            if (bean.getProperties().size() > 0)
            {
              MutablePropertyValues propertyValues = new MutablePropertyValues();
              for (String key : bean.getProperties().keySet())
              {
                logger.debug("Setting property [{}] on bean to [{}]", key, bean
                    .getProperties().get(key));
                propertyValues.add(key, bean.getProperties().get(key));
              }
              beanDefinition.setPropertyValues(propertyValues);
            }

            if (bean.getDriverInterface() != null)
              beanDefinition.setLazyInit(true);
            else
              beanDefinition.setLazyInit(false);

            beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            logger.debug("Registering device driver bean definition");
            beanFactory.registerBeanDefinition(
                DRIVER_BEAN_NAME_PREFIX + bean.getId(), beanDefinition);
          } else
          {
            logger.info("Device driver bean not enabled, skipping [{}]",
                bean.toString());
          }
        }
        beanFactory.preInstantiateSingletons();
      } else
      {
        logger.warn("Cannot register device driver beans, BeanFactory is null");
      }
    } else
    {
      logger.warn("No device driver beans found in database");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.lifecycle.BeanLifecycleService#
   * instantiateInterfaceBeans()
   */
  @Override
  public void instantiateInterfaceBeans()
  {
    logger.info("Instantiating interface beans");

    logger.debug("Retrieving all interface beans from database");
    List<InterfaceBean> beans = interfaceBeanRepository.findAll();

    if (beans != null && beans.size() > 0)
    {
      if (beanFactory != null)
      {
        logger.debug("Iterating enabled interface beans");
        for (InterfaceBean bean : beans)
        {
          if (bean.getTransport().isEnabled() == false)
          {
            logger.warn(
                "Transport disabled, cannot instantiate interface bean [{}]",
                bean.toString());
            continue;
          }

          if (bean.isEnabled())
          {
            logger.info("Building bean definition for interface bean [{}]",
                bean.toString());
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

            beanDefinition.setBeanClassName(bean.getKlass());

            if (bean.getTransport() != null)
            {
              ConstructorArgumentValues args = new ConstructorArgumentValues();
              logger.debug("Getting transport bean reference");
              Transport transport = (Transport) appContext
                  .getBean(TRANSPORT_BEAN_NAME_PREFIX
                      + bean.getTransport().getId());
              args.addGenericArgumentValue(transport);
              beanDefinition.setConstructorArgumentValues(args);
            }

            beanDefinition.setLazyInit(true);
            beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            logger.debug("Registering interface bean definition");
            beanFactory.registerBeanDefinition(INTERFACE_BEAN_NAME_PREFIX
                + bean.getId(), beanDefinition);
          } else
          {
            logger.info("Interface bean not enabled, skipping [{}]",
                bean.toString());
          }
        }
        beanFactory.preInstantiateSingletons();
      } else
      {
        logger.warn("Cannot register interface beans, BeanFactory is null");
      }
    } else
    {
      logger.warn("No interface beans found in database");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.lifecycle.BeanLifecycleService#
   * instantiateTransportBeans()
   */
  @Override
  public void instantiateTransportBeans()
  {
    logger.info("Instantiating transport beans");

    logger.debug("Retrieving all transport beans from database");
    List<TransportBean> beans = transportBeanRepository.findAll();

    if (beans != null && beans.size() > 0)
    {
      if (beanFactory != null)
      {
        logger.debug("Iterating enabled transport beans");
        for (TransportBean bean : beans)
        {
          if (bean.isEnabled())
          {
            logger.info("Building bean definition for transport bean [{}]",
                bean.toString());
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

            beanDefinition.setBeanClassName(bean.getKlass());

            ConstructorArgumentValues args = new ConstructorArgumentValues();
            args.addGenericArgumentValue(bean.getConfig());

            beanDefinition.setConstructorArgumentValues(args);
            beanDefinition.setLazyInit(true);
            beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            logger.debug("Registering transport bean definition");
            beanFactory.registerBeanDefinition(TRANSPORT_BEAN_NAME_PREFIX
                + bean.getId(), beanDefinition);
          } else
          {
            logger.info("Transport bean not enabled, skipping [{}]",
                bean.toString());
          }
        }
        beanFactory.preInstantiateSingletons();
      } else
      {
        logger.warn("Cannot register transport beans, BeanFactory is null");
      }
    } else
    {
      logger.warn("No transport beans found in database");
    }
  }
}
