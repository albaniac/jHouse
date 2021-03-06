/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.util.HashMap;
import java.util.Map;

import net.gregrapp.jhouse.services.ConfigService;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
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
@RequestMapping("/")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class SystemController
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(SystemController.class);

  @Autowired
  private ConfigService configService;
  
  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("isAuthenticated()")
  public Model config()
  {
    logger.entry();
    logger.debug("Getting system config");
    
    Model model = new ExtendedModelMap();
    
    Map<String,String> controllers = new HashMap<String,String>();
    controllers.put("webcam", configService.get("net.gregrapp.jhouse.web.controllers.WebcamController", "CONFIG_PATH"));
    controllers.put("location", configService.get("net.gregrapp.jhouse.web.controllers.LocationController", "CONFIG_PATH"));
    logger.debug("jHouse controllers [{}]", controllers.toString());
    
    model.addAttribute("controllers", controllers);
    
    logger.exit(model);
    return model;
  }

 
}
