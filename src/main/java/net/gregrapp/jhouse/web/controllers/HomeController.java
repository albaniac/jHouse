package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.gregrapp.jhouse.device.classes.BinarySwitch;
import net.gregrapp.jhouse.device.types.Device;
import net.gregrapp.jhouse.managers.DeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	ApplicationContext ctx;
	
	@Autowired
	DeviceManager deviceManager;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
	  BinarySwitch l = (BinarySwitch)ctx.getBean("dinningRoomLight");
	  l.setOn();

	  logger.info("Welcome home! the client locale is "+ locale.toString());
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );

		return "home";
	}

  @RequestMapping(value = "/device/{id}", method = RequestMethod.GET)
  public String info(@PathVariable("id") int id, Model model)
  {
    Device dev = deviceManager.getDeviceForId(id);
    String[] devclass = null;
    if (dev != null)
      devclass = deviceManager.getDeviceClassesForDevice(dev);
   
    String strDevClasses = "";
    for (String s : devclass)
      strDevClasses += s + ",";
    
    model.addAttribute("stuff", strDevClasses); 
    
   return "home"; 
  }
  
  @RequestMapping(value = "/device/{id}/{method}", method = RequestMethod.GET)
  public String device(@PathVariable("id") int id, @PathVariable("method") String method, Model model)
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
}
