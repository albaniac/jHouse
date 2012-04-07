/**
 * 
 */
package net.gregrapp.jhouse.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Authentication entry point for REST web services
 * 
 * Authentication should only be done by a request to the correct URI. All other
 * requests should simply fail with a 401 UNAUTHORIZED status code if the user
 * is not authenticated.
 * 
 * @author Greg Rapp
 * 
 */

@Component("restAuthenticationEntryPoint")
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint
{

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.security.web.AuthenticationEntryPoint#commence(javax
   * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
   * org.springframework.security.core.AuthenticationException)
   */
  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException
  {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }
}
