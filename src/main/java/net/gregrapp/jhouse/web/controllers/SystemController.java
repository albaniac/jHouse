/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.util.HashMap;
import java.util.Map;

import net.gregrapp.jhouse.services.config.ConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SystemController
{
  private static final Logger logger = LoggerFactory
      .getLogger(SystemController.class);

  @Autowired
  private ConfigService configService;
  
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public Model config()
  {
    logger.debug("Getting system config");
    
    Model model = new ExtendedModelMap();
    
    Map<String,String> controllers = new HashMap<String,String>();
    controllers.put("webcam", configService.get("net.gregrapp.jhouse.web.controllers.WebcamController", "configPath"));
    controllers.put("location", configService.get("net.gregrapp.jhouse.web.controllers.LocationController", "configPath"));
    logger.debug("jHouse controllers [{}]", controllers.toString());
    
    model.addAttribute("controllers", controllers);
    
    return model;
  }

 
}
