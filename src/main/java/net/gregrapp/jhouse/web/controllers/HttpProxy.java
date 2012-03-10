/**
 * 
 */
package net.gregrapp.jhouse.web.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Greg Rapp
 * 
 */

@Controller
@Scope("request")
@RequestMapping("/proxy")
public class HttpProxy
{
  private static final Logger logger = LoggerFactory
      .getLogger(HttpProxy.class);

  @Autowired
  private HttpServletRequest request;

  @RequestMapping(value = "/http", method = RequestMethod.GET)
  public void http(@RequestParam("site") String site,
      @RequestParam("user") String user, @RequestParam("pass") String pass,
      HttpServletResponse response)
  {
    BufferedInputStream webToProxyBuf = null;
    BufferedOutputStream proxyToClientBuf = null;
    int oneByte;

    logger.debug("Creating connection");
    HttpURLConnection urlConn = null;
    try
    {
      logger.debug("Opening connection");
      urlConn = (HttpURLConnection) (new URL(
          "http://" + site))
          .openConnection();
      String methodName = request.getMethod();
      urlConn.setRequestMethod(methodName);

      for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();)
      {
        String headerName = e.nextElement().toString();
        urlConn.setRequestProperty(headerName, request.getHeader(headerName));
      }

      if (user != null && !"".equals(user))
      {
        Base64 base64 = new Base64();
        String auth = base64.encodeAsString((user + ":" + pass).getBytes());
        urlConn.setRequestProperty("Authorization", "Basic " + auth);
      }
      logger.debug("Connecting");
      urlConn.connect();

      if (methodName.equals("POST"))
      {
        BufferedInputStream clientToProxyBuf = new BufferedInputStream(
            request.getInputStream());
        BufferedOutputStream proxyToWebBuf = new BufferedOutputStream(
            urlConn.getOutputStream());

        while ((oneByte = clientToProxyBuf.read()) != -1)
          proxyToWebBuf.write(oneByte);

        proxyToWebBuf.flush();
        proxyToWebBuf.close();
        clientToProxyBuf.close();
      }

      response.setStatus(urlConn.getResponseCode());
      for (Iterator<Entry<String, List<String>>> i = urlConn.getHeaderFields().entrySet().iterator(); i
          .hasNext();)
      {
        Map.Entry<String, List<String>> mapEntry = (Map.Entry<String, List<String>>) i.next();
        if (mapEntry.getKey() != null)
          response.setHeader(mapEntry.getKey().toString(),
              ((List<?>) mapEntry.getValue()).get(0).toString());
      }

      webToProxyBuf = new BufferedInputStream(
          urlConn.getInputStream());
      proxyToClientBuf = new BufferedOutputStream(
          response.getOutputStream());

      while ((oneByte = webToProxyBuf.read()) != -1)
        proxyToClientBuf.write(oneByte);

      proxyToClientBuf.flush();
      proxyToClientBuf.close();
      webToProxyBuf.close();
      urlConn.disconnect();
    } catch (MalformedURLException e)
    {
      logger.warn("Invalid URL specified [{}]", site);
    } catch (SocketException e)
    {
    } catch (IOException e)
    {
      logger.warn("Connection error: ", e);
    }
  }
}
