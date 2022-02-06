package dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions;

import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ErrorCode;
import lombok.Getter;

/**
 * Exception thrown if we detect that the operation parameter are invalid.
 * The error code will give out the details about which part is invalid.
 */
@Getter
public class InvalidOperationParameterException extends RuntimeException {
  private final ErrorCode errorCode;

  public InvalidOperationParameterException(ErrorCode errorCode,
                                            String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
