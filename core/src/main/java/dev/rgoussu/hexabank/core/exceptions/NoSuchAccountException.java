package dev.rgoussu.hexabank.core.exceptions;

/**
 * Business exception throw if a given account could not be found.
 */
public class NoSuchAccountException extends RuntimeException {
  public NoSuchAccountException(String s) {
    super(s);
  }
}
