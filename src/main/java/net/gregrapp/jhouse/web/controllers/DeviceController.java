/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.classes.DeviceClass;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.services.DeviceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
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
  private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
  
  @Autowired
  private DeviceService deviceService;
  
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public Model getAllDevices()
  {
    Model model = new ExtendedModelMap();
    
    List<HashMap<String,Object>> devices = new ArrayList<HashMap<String,Object>>();

    logger.debug("Getting all devices from the device service");
    for (Device device : deviceService.getDevices())
    {
      logger.trace("Building map for device [%s]", device.getName());
      HashMap<String,Object> deviceMap = new HashMap<String,Object>();
      deviceMap.put("id", device.getId());
      deviceMap.put("name", device.getName());
      deviceMap.put("text", device.getText());
      deviceMap.put("value", device.getValue());
      deviceMap.put("lastchange", device.getLastChange());
      
      if (device instanceof DriverDevice)
      {
        logger.debug("Device is a driver device, getting associated device classes");
        List<String> deviceClasses = new ArrayList<String>();
        DeviceDriver driver = ((DriverDevice)device).getDriver();
        for (Class<?> deviceClass : driver.getClass().getInterfaces())
        {
          if (DeviceClass.class.isAssignableFrom(deviceClass))
          {
            deviceClasses.add(deviceClass.getCanonicalName());
          }
        }
        logger.trace("Device classes associated with this device [%s]", deviceClasses.toString());
        deviceMap.put("classes", deviceClasses);
      }
      
      devices.add(deviceMap);
    }
    
    logger.debug("Done getting all devices");
    model.addAttribute("devices", devices);
    
    return model;
  }
  
  /*
  @RequestMapping(value = "/{id}/{method}", method = RequestMethod.GET)
  public @ResponseBody
  String deviceAction(@PathVariable("id") int id,
      @PathVariable("method") String method, Model model)
  {
    Device device = deviceService.get(id);
    
    if (device == null)
    {
      model.addAttribute("stuff", "device is null man");
      return "home";
    }
    
    if (!(device instanceof DriverDevice))
    {
      model.addAttribute("stuff", "device isn't a driver device");
      return "home";      
    }
    
    DeviceDriver driver = ((DriverDevice)device).getDriver();
    try
    {
      Method meth = driver.getClass().getMethod(method);
      meth.invoke(driver);
    } catch (SecurityException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e)
    {
      model.addAttribute("stuff", "no such method");
      return "home";
    } catch (IllegalArgumentException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return "home";
  }
  
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public @ResponseBody HashMap<String, Object> deviceDetails(@PathVariable("id") int id, Model model)
  {
    Device device = deviceService.get(id);
   // String[] devclass = null;
    HashMap<String, Object> ret = new HashMap<String,Object>();
    if (device == null)
    {
      ret.put("error", "device not found");
      return ret;
    } 
    else {
      ret.put("name", device.getName());
      ret.put("value", device.getValue());
      ret.put("text", device.getText());
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      ret.put("lastChange", dateFormat.format(device.getLastChange()));
      return ret;
    }
    
    //String strDevClasses = "";
    //for (String s : devclass)
    //  strDevClasses += s + ",";
    //strDevClasses.substring(0, strDevClasses.length()-1);
    //model.addAttribute("stuff", strDevClasses); 
    
   //return "home";
  }
  
  
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public @ResponseBody
  HashMap<String, Object> allDeviceDetails(Model model)
  {
    HashMap<String, Object> ret = new HashMap<String, Object>();
    
    List<HashMap<String,Object>> devices = new ArrayList<HashMap<String,Object>>();
    for (Device device : deviceService.getDevices())
    {
      HashMap<String,Object> val = new HashMap<String,Object>();
      val.put("id", device.getId());
      val.put("name", device.getName());
      val.put("value", device.getValue());
      val.put("text", device.getText());
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      val.put("lastChange", dateFormat.format(device.getLastChange().getTime()));
      
      devices.add(val);
    }
    ret.put("success", true);
    ret.put("devices", devices);
    return ret;
  }

*/
}
