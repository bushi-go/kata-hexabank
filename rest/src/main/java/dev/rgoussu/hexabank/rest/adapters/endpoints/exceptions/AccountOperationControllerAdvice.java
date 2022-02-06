package dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions;

import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ApplicationError;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ErrorCode;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Controller advice to manage any exception thrown that get beyond the web layer.
 */
@ControllerAdvice
public class AccountOperationControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidOperationParameterException.class)
  protected ResponseEntity<ApplicationError> handleBadRequest(InvalidOperationParameterException ex,
                                                              WebRequest req) {
    return ApplicationError.builder().errorCode(ex.getErrorCode()).message(ex.getMessage())
        .timeStamp(Instant.now()).build().toResponse();
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApplicationError> handleUnexpected(Exception ex, WebRequest req) {
    return ApplicationError.builder().timeStamp(
            Instant.now()).message(ex.getMessage()).errorCode(ErrorCode.UNKNOWN_ERROR).build()
        .toResponse();
  }
}
