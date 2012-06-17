/**
 * 
 */
package net.gregrapp.jhouse.web.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that returns an HTTP 400 Bad Request status response
 * 
 * @author Greg Rapp
 *
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ClientErrorBadRequestException extends RuntimeException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
