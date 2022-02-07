package dev.rgoussu.hexabank.core.operations.exceptions;

/**
 * Exception thrown if the exchange rate between two currencies could not be found.
 */
public class UnavailableExchangeRateException extends RuntimeException {

  public UnavailableExchangeRateException(String message) {
    super(message);
  }

  public UnavailableExchangeRateException(String message, Throwable cause) {
    super(message, cause);
  }
}
