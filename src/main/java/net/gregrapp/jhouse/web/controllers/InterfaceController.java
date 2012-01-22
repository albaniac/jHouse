/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.interfaces.NodeInterface;
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
@RequestMapping("/interface")
public class InterfaceController
{
  private static final Logger logger = LoggerFactory.getLogger(InterfaceController.class);
  
  @Autowired
  ApplicationContext ctx;
  
  @Autowired
  DeviceManager deviceManager;

  @RequestMapping(value = "/{name}/nodes", method = RequestMethod.GET)
  public @ResponseBody
  HashMap<String, HashMap<String,Object>> deviceAction(@PathVariable("name") String name, Model model)
  { 
    if (ctx.containsBean(name))
    {
      Object bean = ctx.getBean(name);
      
      if (bean instanceof NodeInterface)
      {
        return ((NodeInterface) bean).getNodes();
      }
    }
    return null;
  }
  
  @RequestMapping(value = "/{name}/{method}", method = RequestMethod.GET)
  public @ResponseBody
  String deviceAction(@PathVariable("name") String name,
      @PathVariable("method") String method, Model model)
  {
    if (ctx.containsBean(name))
    {
      Object bean = ctx.getBean(name);
      
      try
      {
        Method meth = bean.getClass().getMethod(method);
        Object ret = meth.invoke(bean);
        System.out.println(ret.toString());
        model.addAttribute("stuff", ret);
      } catch (SecurityException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (NoSuchMethodException e)
      {
        model.addAttribute("errorDetail", "No such method found");
        return "error";
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
    }
    return "home";
  }
}
