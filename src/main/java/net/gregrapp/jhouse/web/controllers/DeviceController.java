/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.managers.device.DeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Greg Rapp
 *
 */

@Controller
@RequestMapping("/device")
public class DeviceController
{
  private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
  
  @Autowired
  ApplicationContext ctx;
  
  @Autowired
  DeviceManager deviceManager;
  
  @RequestMapping(value = "/{id}/{method}", method = RequestMethod.GET)
  public @ResponseBody
  String deviceAction(@PathVariable("id") int id,
      @PathVariable("method") String method, Model model)
  {
    Device device = deviceManager.get(id);
    
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
  public @ResponseBody String deviceDetails(@PathVariable("id") int id, Model model)
  {
    Device device = deviceManager.get(id);
    String[] devclass = null;
    if (device != null && device instanceof DriverDevice)
    {
      devclass = deviceManager.getDeviceClassesForDevice(device);      
    }
    String strDevClasses = "";
    for (String s : devclass)
      strDevClasses += s + ",";
    strDevClasses.substring(0, strDevClasses.length()-1);
    model.addAttribute("stuff", strDevClasses); 
    
   return "home"; 
  }
}
