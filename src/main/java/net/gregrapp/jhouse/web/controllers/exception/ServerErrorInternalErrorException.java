/**
 * 
 */
package net.gregrapp.jhouse.web.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that returns an HTTP 500 Internal Server Error status response
 * 
 * @author Greg Rapp
 *
 */

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerErrorInternalErrorException extends RuntimeException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
