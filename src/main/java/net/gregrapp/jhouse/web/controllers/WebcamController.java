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
import net.gregrapp.jhouse.device.classes.Webcam;
import net.gregrapp.jhouse.device.drivers.types.DeviceDriver;

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
  private static final String VIDEO_URL = "video";

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private HttpProxy httpProxy;

  /**
   * List webcams
   * 
   * @return list of webcams
   */
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public Model list()
  {
    Model model = new ExtendedModelMap();
    List<HashMap<String, Object>> webcams = new ArrayList<HashMap<String, Object>>();

    Map<String, DriverDevice> devices = BeanFactoryUtils
        .beansOfTypeIncludingAncestors(appContext, DriverDevice.class);

    for (String key : devices.keySet())
    {
      DriverDevice device = devices.get(key);
      if (device.getDriver() instanceof Webcam)
      {
        HashMap<String, Object> webcam = new HashMap<String, Object>();
        webcam.put("id", device.getId());
        webcam.put("name", device.getName());
        webcam.put("classes", device.getDriver().getClass().getInterfaces());
        webcam.put("beanName", key);
        webcam.put(
            "videoUrl",
            String.format("%s://%s:%s/%s/%s", request.getScheme(), request.getServerName(),
                request.getServerPort(), "jhouse/controllers/webcam/video", key));
        webcams.add(webcam);
      }
    }
    model.addAttribute("webcams", webcams);
    return model;
  }

  /**
   * Video stream from webcam
   * 
   * @param beanName DriverDevice bean name
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/video/{beanName}", method = RequestMethod.GET)
  public void video(@PathVariable String beanName, HttpServletResponse response)
      throws Exception
  {
    if (appContext.containsBean(beanName))
    {
      Webcam webcam = (Webcam) ((DriverDevice)appContext.getBean(beanName)).getDriver();
      httpProxy.http(webcam.getVideoUrl(),
          //"familycam.crazynoodle.net/videostream.cgi?resolution=32&rate=0",
          webcam.getUsername(), webcam.getPassword(), response);
    } else
    {
      throw new Exception("Invalid bean name");
    }
  }
}
