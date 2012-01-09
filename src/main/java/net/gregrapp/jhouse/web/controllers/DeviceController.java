/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.gregrapp.jhouse.device.types.Device;
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
    Device dev = deviceManager.getDeviceForId(id);
    if (dev == null)
    {
      model.addAttribute("stuff", "stuff is null man");
      return "home";
    }

    try
    {
      Method meth = dev.getClass().getMethod(method);
      meth.invoke(dev);
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
    Device dev = deviceManager.getDeviceForId(id);
    String[] devclass = null;
    if (dev != null)
      devclass = deviceManager.getDeviceClassesForDevice(dev);
   
    String strDevClasses = "";
    for (String s : devclass)
      strDevClasses += s + ",";
    strDevClasses.substring(0, strDevClasses.length()-1);
    model.addAttribute("stuff", strDevClasses); 
    
   return "home"; 
  }
}
