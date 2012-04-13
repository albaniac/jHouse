/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;
import net.gregrapp.jhouse.services.lifecycle.BeanLifecycleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/driver")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER_USER')")
public class DriverController
{
  private static final Logger logger = LoggerFactory
      .getLogger(DriverController.class);

  @Autowired
  private ApplicationContext ctx;

  @RequestMapping(value = "/{id}/{method}", method = RequestMethod.GET)
  public @ResponseBody
  String deviceAction(@PathVariable("id") int id,
      @PathVariable("method") String method, Model model)
  {
    if (ctx.containsBean(BeanLifecycleService.DRIVER_BEAN_NAME_PREFIX + id))
    {
      DeviceDriver driver = (DeviceDriver) ctx
          .getBean(BeanLifecycleService.DRIVER_BEAN_NAME_PREFIX + id);

      if (driver == null)
      {
        model.addAttribute("stuff", "device is null man");
        return "home";
      }

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
    }
    return "home";
  }
}
