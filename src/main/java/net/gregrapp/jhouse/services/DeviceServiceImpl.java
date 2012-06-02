/**
 * 
 */
package net.gregrapp.jhouse.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.classes.DeviceClass;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
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
public class DeviceServiceImpl implements DeviceService
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(DeviceService.class);

  @Autowired
  private ApplicationContext appContext;

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceService#execute(int,
   * java.lang.String)
   */
  @Override
  public void execute(int deviceId, String method)
  {
    logger.entry(deviceId, method);

    logger.debug("Executing method [{}] on device [{}]", method, deviceId);
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
        logger.warn("Method [{}] not found for device [{}]", method, deviceId);
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

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceService#execute(int,
   * java.lang.String, java.lang.Object[])
   */
  @Override
  public void execute(int deviceId, String method, Object... args)
  {
    logger.entry(deviceId, method, args);

    logger.debug("Executing method [{}] on device [{}]", method, deviceId);
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
            .getMethod(method, argClasses.toArray(new Class[0]));
        meth.invoke(device, args);
      } catch (SecurityException e)
      {
        logger.warn(
            "Security exception while attempting to execute method [{}]",
            method);
      } catch (NoSuchMethodException e)
      {
        logger.warn("Method not found for device [{}]: [{}]", deviceId, method);
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

    logger.exit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceService#get(int)
   */
  public Device get(int deviceId)
  {
    logger.entry(deviceId);

    logger.debug("Getting device [{}]", deviceId);

    if (appContext.containsBean(BeanLifecycleService.DEVICE_BEAN_NAME_PREFIX
        + deviceId))
    {
      Device device = (Device) appContext
          .getBean(BeanLifecycleService.DEVICE_BEAN_NAME_PREFIX + deviceId);
      logger.exit(device);
      return device;
    } else
    {
      logger.warn("Device [{}] not found", deviceId);
      logger.exit(null);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceService#get(int,
   * java.lang.Class)
   */
  public <T extends Device> T get(int deviceId, Class<T> type)
  {
    logger.entry(deviceId, type);

    Device device = get(deviceId);
    if (type.isAssignableFrom(device.getClass()))
    {
      logger.exit(device);
      return type.cast(device);
    } else
    {
      logger.exit(null);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.managers.device.DeviceService#getDeviceClassesForDevice
   * (net.gregrapp.jhouse.device.Device)
   */
  public String[] getDeviceClassesForDevice(Device device)
  {
    logger.entry(device);

    logger.debug("Getting device classes for device [{}]", device.getId());
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
    logger.debug("DeviceDriver [{}] has classes: [{}]", ((DriverDevice) device)
        .getDriver().getClass().getSimpleName(), strKlasses);

    logger.exit(strKlasses);
    return strKlasses;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceService#getDevices()
   */
  @Override
  public List<Device> getDevices()
  {
    logger.entry();

    Device[] arrDevices = appContext.getBeansOfType(Device.class).values()
        .toArray(new Device[0]);

    List<Device> devices = Arrays.asList(arrDevices);

    logger.exit(devices);
    return devices;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.managers.device.DeviceService#getDriver(int,
   * java.lang.Class)
   */
  public <T extends DeviceClass> T getDriver(int deviceId, Class<T> type)
  {
    logger.entry(deviceId, type);

    Device device = get(deviceId);
    if (device instanceof DriverDevice)
    {
      DeviceDriver driver = ((DriverDevice) device).getDriver();
      if (type.isAssignableFrom(driver.getClass()))
      {
        logger.exit(driver);
        return type.cast(driver);
      } else
      {
        logger.exit(null);
        return null;
      }
    } else
    {
      logger.exit(null);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.gregrapp.jhouse.services.DeviceService#getDriverDevices()
   */
  @Override
  public List<DriverDevice> getDriverDevices()
  {
    logger.entry();

    DriverDevice[] arrDevices = appContext.getBeansOfType(DriverDevice.class)
        .values().toArray(new DriverDevice[0]);

    List<DriverDevice> devices = Arrays.asList(arrDevices);

    logger.exit(devices);
    return devices;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.gregrapp.jhouse.services.DeviceService#getDriverDevicesForClass(java
   * .lang.String)
   */
  @Override
  public List<DriverDevice> getDriverDevicesForClass(String klassName)
  {
    logger.entry(klassName);

    Class<?> klass = null;

    try
    {
      klass = Class.forName(klassName);
    } catch (ClassNotFoundException e)
    {
      logger.warn("Class [{}] not found", klassName);
      logger.exit(null);
      return null;
    }

    List<DriverDevice> allDevices = this.getDriverDevices();

    List<DriverDevice> devices = new ArrayList<DriverDevice>();

    for (DriverDevice device : allDevices)
    {
      if (klass.isAssignableFrom(device.getDriver().getClass()))
      {
        devices.add(device);
      }
    }

    logger.exit(devices);
    return devices;
  }
}
