/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.managers.device.DeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
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
  private ApplicationContext ctx;
  
  @Autowired
  private DeviceManager deviceManager;
  
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
  public @ResponseBody HashMap<String, Object> deviceDetails(@PathVariable("id") int id, Model model)
  {
    Device device = deviceManager.get(id);
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
    
    /*String strDevClasses = "";
    for (String s : devclass)
      strDevClasses += s + ",";
    strDevClasses.substring(0, strDevClasses.length()-1);
    model.addAttribute("stuff", strDevClasses); 
    
   return "home"; */
  }
  
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public @ResponseBody
  HashMap<String, Object> allDeviceDetails(Model model)
  {
    HashMap<String, Object> ret = new HashMap<String, Object>();
    
    List<HashMap<String,Object>> devices = new ArrayList<HashMap<String,Object>>();
    for (Device device : deviceManager.getDevices())
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

}
