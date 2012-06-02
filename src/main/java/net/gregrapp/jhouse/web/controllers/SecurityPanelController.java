/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gregrapp.jhouse.device.Device;
import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.classes.SecurityPanel;
import net.gregrapp.jhouse.services.DeviceService;
import net.gregrapp.jhouse.web.controllers.DeviceController.DeviceAction;
import net.gregrapp.jhouse.web.controllers.exception.ClientErrorBadRequestException;
import net.gregrapp.jhouse.web.controllers.exception.ServerErrorInternalErrorException;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
@RequestMapping("/controllers/securityPanel")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SECURITYPANEL_USER')")
public class SecurityPanelController
{

  private static final XLogger logger = XLoggerFactory
      .getXLogger(SecurityPanelController.class);

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private DeviceService deviceService;

  private Map<String, Object> getAndParseSecurityPanelDevices()
  {
    logger.entry();

    logger.debug("Getting devices for class [{}]",
        SecurityPanel.class.getName());
    List<DriverDevice> devices = deviceService
        .getDriverDevicesForClass(SecurityPanel.class.getName());

    Map<String, Object> system = new HashMap<String, Object>();
    List<HashMap<String, Object>> zones = new ArrayList<HashMap<String, Object>>();

    logger.debug("Iterating devices");
    for (Device device : devices)
    {
      DriverDevice driverDevice = (DriverDevice) device;
      int driverIndex = driverDevice.getDriverIndex();
      if (driverIndex == 10)
      {
        logger.trace("Got system status");
        system.put("status", driverDevice.getText());
        system.put("statusLastChange", driverDevice.getLastChange());
      } else if (driverIndex == 11)
      {
        logger.trace("Got last armed by value");
        system.put("lastArmedBy", driverDevice.getText());
        system.put("armedByLastChange", driverDevice.getLastChange());
      } else if (driverIndex == 12)
      {
        logger.trace("Got last disarmed by value");
        system.put("lastDisarmedBy", driverDevice.getText());
        system.put("disarmedByLastChange", driverDevice.getLastChange());
      } else if (driverIndex >= 101 && driverIndex <= 164)
      {
        logger.trace("Got zone state");
        HashMap<String, Object> zone = new HashMap<String, Object>();
        zone.put("zone", driverIndex - 100);
        zone.put("name", driverDevice.getName());
        zone.put("state", driverDevice.getText());
        zone.put("lastChanged", driverDevice.getLastChange());
        zones.add(zone);
      }
    }

    // Sort zones by zone number
    Collections.sort(zones, new Comparator<HashMap<String, Object>>()
    {
      @Override
      public int compare(HashMap<String, Object> arg0,
          HashMap<String, Object> arg1)
      {
        return (Integer) arg0.get("zone") - (Integer) arg1.get("zone");
      }
    });

    logger.debug("Building return map");
    Map<String, Object> returnMap = new HashMap<String, Object>();
    returnMap.put("system", system);
    returnMap.put("zones", zones);

    logger.exit(returnMap);
    return returnMap;
  }

  /**
   * Returns system and zone information
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET)
  public Model getDefault()
  {
    logger.entry();

    Map<String, Object> securityPanel = this.getAndParseSecurityPanelDevices();

    Model model = new ExtendedModelMap();
    model.addAttribute("securityPanel", securityPanel);

    logger.exit(model);
    return model;
  }

  @RequestMapping(value = "/{method}", method = RequestMethod.PUT)
  public Model putMethod(@PathVariable String method,
      @RequestBody DeviceAction action)
  {
    logger.entry(method, action);

    Model model = new ExtendedModelMap();

    Map<String, SecurityPanel> securityPanels = BeanFactoryUtils
        .beansOfTypeIncludingAncestors(appContext, SecurityPanel.class);

    if (securityPanels.size() != 1)
    {
      model.addAttribute("status", "error");
      model.addAttribute("description",
          "only one bean of type SecurityPanel expected");
    } else
    {
      SecurityPanel securityPanel = securityPanels.values().toArray(
          new SecurityPanel[0])[0];

      List<Class<?>> klasses = new ArrayList<Class<?>>();
      for (Object obj : action.getArgs())
      {
        klasses.add(obj.getClass());
      }

      Method methodObject = null;
      try
      {
        methodObject = securityPanel.getClass().getMethod(method,
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
        Object retVal = methodObject.invoke(securityPanel, new Object[] {});
        if (retVal != null)
        {
          logger.debug("Return value [{}]", retVal.toString());
          model.addAttribute("message", retVal.toString());
        }
      } catch (IllegalArgumentException e)
      {
        logger.warn("Illegal argument(s) specified for method [{}]: ", method,
            e);
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
    }

    model.addAttribute("status", "ok");

    logger.exit(model);
    return model;
  }
}
