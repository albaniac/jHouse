/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.gregrapp.jhouse.device.DriverDevice;
import net.gregrapp.jhouse.device.classes.PtzWebcam;
import net.gregrapp.jhouse.device.classes.Webcam;
import net.gregrapp.jhouse.device.classes.Webcam.Resolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Greg Rapp
 * 
 */

@Controller
@Scope("request")
@RequestMapping("/controllers/webcam")
public class WebcamController
{
  private static final Logger logger = LoggerFactory
      .getLogger(WebcamController.class);

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private HttpProxy httpProxy;

  @Autowired
  private HttpServletRequest request;

  /**
   * List webcams
   * 
   * @return list of webcams
   */
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public Model list()
  {
    logger.debug("Listing webcam beans");

    String baseUrl = String.format("%s://%s:%s/%s", request.getScheme(),
        request.getServerName(), request.getServerPort(),
        "jhouse/controllers/webcam/");

    Model model = new ExtendedModelMap();

    model.addAttribute("baseUrl", baseUrl);
    model.addAttribute("videoUri", "video/");
    model.addAttribute("panUpUri", "panUp/");
    model.addAttribute("panDownUri", "panDown/");
    model.addAttribute("panLeftUri", "panLeft/");
    model.addAttribute("panRightUri", "panRight/");
    model.addAttribute("panStopUri", "panStop/");

    List<HashMap<String, Object>> webcams = new ArrayList<HashMap<String, Object>>();

    Map<String, DriverDevice> devices = BeanFactoryUtils
        .beansOfTypeIncludingAncestors(appContext, DriverDevice.class);

    for (String beanName : devices.keySet())
    {
      DriverDevice device = devices.get(beanName);
      if (device.getDriver() instanceof Webcam)
      {
        HashMap<String, Object> webcam = new HashMap<String, Object>();

        webcam.put("id", device.getId());
        webcam.put("name", device.getName());
        webcam.put("classes", device.getDriver().getClass().getInterfaces());
        webcam.put("beanName", beanName);

        webcams.add(webcam);
      }
    }
    model.addAttribute("webcams", webcams);
    return model;
  }

  /**
   * Pan the webcam down
   * 
   * @param beanName
   *          name of bean
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/panDown/{beanName}", method = RequestMethod.GET)
  public void panDown(@PathVariable String beanName,
      HttpServletResponse response) throws Exception
  {
    logger.debug("Executing pan down on bean [{}]", beanName);

    if (appContext.containsBean(beanName)
        && ((DriverDevice) appContext.getBean(beanName)).getDriver() instanceof PtzWebcam)
    {
      PtzWebcam webcam = (PtzWebcam) ((DriverDevice) appContext
          .getBean(beanName)).getDriver();
      webcam.panDown();
    } else
    {
      throw new Exception("Invalid bean");
    }
  }

  /**
   * Pan the webcam left
   * 
   * @param beanName
   *          name of bean
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/panLeft/{beanName}", method = RequestMethod.GET)
  public void panLeft(@PathVariable String beanName,
      HttpServletResponse response) throws Exception
  {
    logger.debug("Executing pan left on bean [{}]", beanName);

    if (appContext.containsBean(beanName)
        && ((DriverDevice) appContext.getBean(beanName)).getDriver() instanceof PtzWebcam)
    {
      PtzWebcam webcam = (PtzWebcam) ((DriverDevice) appContext
          .getBean(beanName)).getDriver();
      webcam.panLeft();
    } else
    {
      throw new Exception("Invalid bean");
    }
  }

  /**
   * Pan the webcam right
   * 
   * @param beanName
   *          name of bean
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/panRight/{beanName}", method = RequestMethod.GET)
  public void panRight(@PathVariable String beanName,
      HttpServletResponse response) throws Exception
  {
    logger.debug("Executing pan right on bean [{}]", beanName);

    if (appContext.containsBean(beanName)
        && ((DriverDevice) appContext.getBean(beanName)).getDriver() instanceof PtzWebcam)
    {
      PtzWebcam webcam = (PtzWebcam) ((DriverDevice) appContext
          .getBean(beanName)).getDriver();
      webcam.panRight();
    } else
    {
      throw new Exception("Invalid bean");
    }
  }

  /**
   * Pan the webcam up
   * 
   * @param beanName
   *          name of bean
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/panUp/{beanName}", method = RequestMethod.GET)
  public void panUp(@PathVariable String beanName, HttpServletResponse response)
      throws Exception
  {
    logger.debug("Executing pan up on bean [{}]", beanName);

    if (appContext.containsBean(beanName)
        && ((DriverDevice) appContext.getBean(beanName)).getDriver() instanceof PtzWebcam)
    {
      PtzWebcam webcam = (PtzWebcam) ((DriverDevice) appContext
          .getBean(beanName)).getDriver();
      webcam.panUp();
    } else
    {
      throw new Exception("Invalid bean");
    }
  }

  /**
   * Stop the last pan request
   * 
   * @param beanName
   *          name of bean
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/panStop/{beanName}", method = RequestMethod.GET)
  public void panStop(@PathVariable String beanName,
      HttpServletResponse response) throws Exception
  {
    logger.debug("Executing pan stop on bean [{}]", beanName);
    if (appContext.containsBean(beanName)
        && ((DriverDevice) appContext.getBean(beanName)).getDriver() instanceof PtzWebcam)
    {
      PtzWebcam webcam = (PtzWebcam) ((DriverDevice) appContext
          .getBean(beanName)).getDriver();
      webcam.panStop();
    } else
    {
      throw new Exception("Invalid bean");
    }
  }

  /**
   * Video stream from webcam
   * 
   * @param resolution
   *          Video resolution
   * @param beanName
   *          DriverDevice bean name
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/video/{resolution}/{beanName}", method = RequestMethod.GET)
  public void video(@PathVariable Resolution resolution,
      @PathVariable String beanName, HttpServletResponse response)
      throws Exception
  {
    logger.debug("Getting {} resolution video stream for bean [{}]", resolution.toString().toLowerCase(), beanName);

    if (appContext.containsBean(beanName)
        && ((DriverDevice) appContext.getBean(beanName)).getDriver() instanceof Webcam)
    {
      Webcam webcam = (Webcam) ((DriverDevice) appContext.getBean(beanName))
          .getDriver();
      httpProxy.http(webcam.getVideoUrl(resolution), webcam.getUsername(),
          webcam.getPassword(), response);
    } else
    {
      throw new Exception("Invalid bean");
    }
  }

  /**
   * Normal resolution video stream from webcam
   * 
   * @param beanName
   *          DriverDevice bean name
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/video/{beanName}", method = RequestMethod.GET)
  public void video(@PathVariable String beanName, HttpServletResponse response)
      throws Exception
  {
    this.video(Resolution.NORMAL, beanName, response);
  }

}
