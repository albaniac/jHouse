/**
 * 
 */
package net.gregrapp.jhouse.managers.device;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.classes.DeviceClass;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.services.lifecycle.BeanLifecycleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Device manager class
 * 
 * @author Greg Rapp
 * 
 */

@Service
public class DeviceManagerImpl implements DeviceManager
{
  private static final Logger logger = LoggerFactory
      .getLogger(DeviceManager.class);

  //@Autowired(required = false)
  //Device[] devices;

  @Autowired
  ApplicationContext appContext;
  
  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#execute(int,
   * java.lang.String)
   */
  @Override
  public void execute(int deviceId, String method)
  {
    logger.debug("Executing method {} on device {}", method, deviceId);
    Device device = get(deviceId);

    if (device != null && device instanceof DriverDevice)
    {
      try
      {
        Method meth = ((DriverDevice) device).getDriver().getClass()
            .getMethod(method);
        meth.invoke(device);
      } catch (SecurityException e)
      {
        logger.warn(
            "Security exception while attempting to execute method [{}]",
            method);
      } catch (NoSuchMethodException e)
      {
        logger.warn("Method [{}] not found for device {}", method, deviceId);
      } catch (IllegalArgumentException e)
      {
        logger.warn("Illegal arguments for method [{}]", method);
      } catch (IllegalAccessException e)
      {
        logger.warn("Error executing method", e);
      } catch (InvocationTargetException e)
      {
        logger.warn("Error executing method", e);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#execute(int,
   * java.lang.String, java.lang.Object[])
   */
  @Override
  public void execute(int deviceId, String method, Object... args)
  {
    logger.debug("Executing method {} on device {}", method, deviceId);
    Device device = get(deviceId);

    if (device != null && device instanceof DriverDevice)
    {
      List<Class<?>> argClasses = new ArrayList<Class<?>>();
      for (Object arg : args)
      {
        argClasses.add(arg.getClass());
      }

      try
      {
        Method meth = ((DriverDevice) device).getDriver().getClass()
            .getMethod(method,
                argClasses.toArray(new Class[0]));
        meth.invoke(device, args);
      } catch (SecurityException e)
      {
        logger.warn(
            "Security exception while attempting to execute method: {}",
            method);
      } catch (NoSuchMethodException e)
      {
        logger.warn("Method not found for device {}: {}", deviceId, method);
      } catch (IllegalArgumentException e)
      {
        logger.warn("Illegal arguments for method: {}", method);
      } catch (IllegalAccessException e)
      {
        logger.warn("Error executing method", e);
      } catch (InvocationTargetException e)
      {
        logger.warn("Error executing method", e);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#get(int)
   */
  public Device get(int deviceId)
  {
    logger.debug("Getting device [{}]", deviceId);
 
    if (appContext.containsBean(BeanLifecycleService.DEVICE_BEAN_NAME_PREFIX+deviceId))
    {
      Device device = (Device)appContext.getBean(BeanLifecycleService.DEVICE_BEAN_NAME_PREFIX+deviceId);
      return device;
    }
    else
    {
      logger.warn("Device {} not found", deviceId);
      return null;
    }
  }

  
  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#get(int,
   * java.lang.Class)
   */
  public <T extends Device> T get(int deviceId, Class<T> type)
  {
    Device device = get(deviceId);
    if (type.isAssignableFrom(device.getClass()))
      return type.cast(get(deviceId));
    else
      return null;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#getDeviceClassesForDevice(net.gregrapp.jhouse.device.Device)
   */
  public String[] getDeviceClassesForDevice(Device device)
  {
    logger.debug("Getting device classes for device {}", device.getId());
    List<String> klasses = new ArrayList<String>();

    if (device instanceof DriverDevice)
    {
      for (Class<?> iface : ((DriverDevice) device).getDriver().getClass()
          .getInterfaces())
      {
        // Add the interface to the list if it is a DeviceClass
        if (DeviceClass.class.isAssignableFrom(iface))
        {
          klasses.add(iface.getSimpleName());
        }
      }
    }
    String[] strKlasses = klasses.toArray(new String[0]);
    logger.debug("DeviceDriver {} has classes: {}", ((DriverDevice) device)
        .getDriver().getClass().getSimpleName(), strKlasses);
    return strKlasses;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#getDevices()
   */
  @Override
  public Device[] getDevices()
  {
    Device[] devices = appContext.getBeansOfType(Device.class).values().toArray(new Device[0]);
    return devices;
  }

  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.managers.device.DeviceManager#getDriver(int, java.lang.Class)
   */
  public <T extends DeviceClass> T getDriver(int deviceId, Class<T> type)
  {
    Device device = get(deviceId);
    if (device instanceof DriverDevice)
    {
      DeviceDriver driver = ((DriverDevice) device).getDriver();
      if (type.isAssignableFrom(driver.getClass()))
        return type.cast(driver);
      else
        return null;
    }
    else
      return null;
  }
}
