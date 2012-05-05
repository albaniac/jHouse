/**
 * 
 */
package net.gregrapp.jhouse.web.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that returns an HTTP 200 OK status response
 * @author Greg Rapp
 *
 */

@ResponseStatus(value = HttpStatus.OK)
public class SuccessOKException extends RuntimeException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
