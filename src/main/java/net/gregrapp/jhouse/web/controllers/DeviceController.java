/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.classes.DeviceClass;
import net.gregrapp.jhouse.device.classes.SecurityPanel;
import net.gregrapp.jhouse.device.classes.Webcam;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.services.DeviceService;
import net.gregrapp.jhouse.web.controllers.exception.ClientErrorBadRequestException;
import net.gregrapp.jhouse.web.controllers.exception.ServerErrorInternalErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Greg Rapp
 * 
 */

@Controller
@RequestMapping("/controllers/device")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DEVICE_USER')")
public class DeviceController
{
  public static class DeviceAction
  {
    private Object[] args;

    /**
     * @return the args
     */
    public Object[] getArgs()
    {
      return args;
    }

    /**
     * @param args
     *          the args to set
     */
    public void setArgs(Object[] args)
    {
      this.args = args;
    }
  }

  private static final Logger logger = LoggerFactory
      .getLogger(DeviceController.class);

  @Autowired
  private DeviceService deviceService;

  /**
   * Get current state of all devices
   * 
   * @return map containing all devices and their states
   */
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public Model getAllDevices()
  {
    Model model = new ExtendedModelMap();

    List<HashMap<String, Object>> devices = new ArrayList<HashMap<String, Object>>();

    logger.debug("Getting all devices from the device service");
    for (Device device : deviceService.getDevices())
    {
      logger.trace("Building map for device [%s]", device.getName());
      HashMap<String, Object> deviceMap = new HashMap<String, Object>();
      deviceMap.put("id", device.getId());
      deviceMap.put("name", device.getName());
      deviceMap.put("text", device.getText());
      deviceMap.put("value", device.getValue());
      deviceMap.put("lastchange", device.getLastChange());

      if (device instanceof DriverDevice)
      {
        logger
            .debug("Device is a driver device, getting associated device classes");
        List<String> deviceClasses = new ArrayList<String>();
        DeviceDriver driver = ((DriverDevice) device).getDriver();

        // Don't include security panel devices
        if (driver instanceof SecurityPanel)
          continue;

        for (Class<?> deviceClass : driver.getClass().getInterfaces())
        {
          if (DeviceClass.class.isAssignableFrom(deviceClass))
          {
            deviceClasses.add(deviceClass.getCanonicalName());
          }
        }
        logger.trace("Device classes associated with this device [{}]",
            deviceClasses.toString());
        deviceMap.put("classes", deviceClasses);
      }

      devices.add(deviceMap);
    }

    logger.debug("Done getting all devices");
    model.addAttribute("devices", devices);

    return model;
  }

  /**
   * Get current state of all devices
   * 
   * @return map containing all devices and their states
   */
  @RequestMapping(value = "/all/bylocation", method = RequestMethod.GET)
  public Model getAllDevicesByLocation()
  {
    Model model = new ExtendedModelMap();

    // Map<String, HashMap<String, Object>> devices = new HashMap<String,
    // HashMap<String, Object>>();
    HashMap<String, ArrayList<HashMap<String, Object>>> devices = new HashMap<String, ArrayList<HashMap<String, Object>>>();
    // ArrayList<ArrayList<HashMap<String, Object>>> devices = new
    // ArrayList<ArrayList<HashMap<String, Object>>>();

    logger.debug("Getting all devices from the device service");
    for (Device device : deviceService.getDevices())
    {
      logger.trace("Building map for device [%s]", device.getName());
      HashMap<String, Object> deviceMap = new HashMap<String, Object>();
      deviceMap.put("id", device.getId());
      deviceMap.put("name", device.getName());
      deviceMap.put("text", device.getText());
      deviceMap.put("value", device.getValue());
      deviceMap.put("lastchange", device.getLastChange());
      deviceMap.put("floor", device.getFloor());
      deviceMap.put("room", device.getRoom());
      
      if (device instanceof DriverDevice)
      {
        logger
            .debug("Device is a driver device, getting associated device classes");
        List<String> deviceClasses = new ArrayList<String>();
        DeviceDriver driver = ((DriverDevice) device).getDriver();

        // Don't include security panel or webcam devices since we display those
        // separately
        if (driver instanceof SecurityPanel || driver instanceof Webcam)
          continue;

        for (Class<?> deviceClass : driver.getClass().getInterfaces())
        {
          if (DeviceClass.class.isAssignableFrom(deviceClass))
          {
            deviceClasses.add(deviceClass.getCanonicalName());
          }
        }
        logger.trace("Device classes associated with this device [{}]",
            deviceClasses.toString());
        deviceMap.put("classes", deviceClasses);
      }
      String location = String.format("%s - %s", device.getFloor(),
          device.getRoom());

      if (devices.containsKey(location)
          && devices.get(location) instanceof ArrayList)
      {
        devices.get(location).add(deviceMap);
      } else
      {
        ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
        newList.add(deviceMap);
        devices.put(location, newList);
      }
    }

    logger.debug("Done getting all devices");
    // Return devices as a list grouped by location (for iOS grouped table)
    model.addAttribute("devices",
        new ArrayList<ArrayList<HashMap<String, Object>>>(devices.values()));

    return model;
  }

  /**
   * Get a device's state
   * 
   * @param deviceId
   * @return map containing device state
   */
  @RequestMapping(value = "/{deviceId}", method = RequestMethod.GET)
  public Model getDevice(@PathVariable int deviceId)
  {
    Model model = new ExtendedModelMap();

    logger.debug("Getting device [{}]", deviceId);
    Device device = deviceService.get(deviceId);

    if (device == null)
    {
      model.addAttribute("status", "error");
      model.addAttribute("message", "device not found");
      return model;
    }

    logger.trace("Building map for device [{}]", device.getName());
    HashMap<String, Object> deviceMap = new HashMap<String, Object>();
    deviceMap.put("id", device.getId());
    deviceMap.put("name", device.getName());
    deviceMap.put("text", device.getText());
    deviceMap.put("value", device.getValue());
    deviceMap.put("lastchange", device.getLastChange());

    if (device instanceof DriverDevice)
    {
      logger
          .debug("Device is a driver device, getting associated device classes");
      List<String> deviceClasses = new ArrayList<String>();
      DeviceDriver driver = ((DriverDevice) device).getDriver();
      for (Class<?> deviceClass : driver.getClass().getInterfaces())
      {
        if (DeviceClass.class.isAssignableFrom(deviceClass))
        {
          deviceClasses.add(deviceClass.getCanonicalName());
        }
      }
      logger.trace("Device classes associated with this device [%s]",
          deviceClasses.toString());
      deviceMap.put("classes", deviceClasses);
    }

    logger.debug("Done getting device");
    model.addAttribute("device", deviceMap);

    return model;
  }

  @RequestMapping(value = "/{deviceId}/action/{method}", method = {
      RequestMethod.GET, RequestMethod.PUT })
  public Model putDeviceAction(@PathVariable int deviceId,
      @PathVariable String method, @RequestBody DeviceAction action)
  {
    Model model = new ExtendedModelMap();

    logger.debug("Getting device [{}]", deviceId);
    Device device = deviceService.get(deviceId);

    if (device == null)
    {
      model.addAttribute("status", "error");
      model.addAttribute("message", "device not found");
      return model;
    }

    if (!(device instanceof DriverDevice))
    {
      model.addAttribute("status", "error");
      model.addAttribute("message", "device is not a driver device");
      return model;
    }

    DeviceDriver driver = ((DriverDevice) device).getDriver();

    model.addAttribute("status", "ok");

    List<Class<?>> klasses = new ArrayList<Class<?>>();
    for (Object obj : action.getArgs())
    {
      klasses.add(obj.getClass());
    }

    Method methodOjbect = null;
    try
    {
      methodOjbect = driver.getClass().getMethod(method,
          klasses.toArray(new Class<?>[0]));
    } catch (SecurityException e)
    {
      logger.warn("Security exception accessing method [{}]: ", method, e);
      throw new ServerErrorInternalErrorException();
    } catch (NoSuchMethodException e)
    {
      logger.warn("Method [{}] not found", method);
      throw new ClientErrorBadRequestException();
    }

    try
    {
      Object retVal = methodOjbect.invoke(driver, action.getArgs());
      if (retVal != null)
      {
        logger.debug(retVal.toString());
        model.addAttribute("message", retVal.toString());
      }
    } catch (IllegalArgumentException e)
    {
      logger.warn("Illegal argument(s) specified for method [{}]: ", method, e);
      throw new ClientErrorBadRequestException();
    } catch (IllegalAccessException e)
    {
      logger.warn("Illegal access exception: ", e);
      throw new ServerErrorInternalErrorException();
    } catch (InvocationTargetException e)
    {
      logger.warn("Invocation target exception: ", e);
      throw new ServerErrorInternalErrorException();
    }

    return model;
  }
}
