package dev.rgoussu.hexabank.cli.adapters.endpoints;

/**
 * Interface for account number validation.
 */
public interface AccountValidator {
  /**
   * Check if a given account number is valid.
   *
   * @param account account number
   * @return true if valid
   */
  boolean isValidAccount(String account);
}
