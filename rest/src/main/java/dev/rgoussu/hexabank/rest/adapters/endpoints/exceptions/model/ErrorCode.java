package dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

/**
 * Application error code.
 */
public enum ErrorCode {
  EXCHANGE_RATE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),
  NO_ACCOUNT_NUMBER_PROVIDED(HttpStatus.BAD_REQUEST),
  NO_CURRENCY_PROVIDED(HttpStatus.BAD_REQUEST),
  NO_DEPOSIT_PROVIDED(HttpStatus.BAD_REQUEST),
  INVALID_DEPOSIT_AMOUNT(HttpStatus.BAD_REQUEST),
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
  UNKNOWN_ACCOUNT(HttpStatus.NOT_FOUND);

  private HttpStatus status;

  ErrorCode(HttpStatus status) {
    this.status = status;
  }

  @JsonValue
  public String code() {
    return this.name();
  }

  public HttpStatus status() {
    return status;
  }
}
