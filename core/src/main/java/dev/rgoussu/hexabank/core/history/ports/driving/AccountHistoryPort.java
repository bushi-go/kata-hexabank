package dev.rgoussu.hexabank.core.history.ports.driving;

import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;

/**
 * Account history driving port.
 */
public interface AccountHistoryPort<T> {
  /**
   * Get the history for the given account.
   *
   * @param accountId the id of the account
   * @return the history of operations for the account
   * @throws NoSuchAccountException if the account does not exists
   */
  AccountSummary getAccountHistory(String accountId) throws NoSuchAccountException;

  /**
   * Register an operation to the history of the account.
   *
   * @param accountId the id of the account
   * @param operation the operation summary
   * @return T return value depending on implementation
   */
   T registerOperationToHistory(String accountId, AccountOperationSummary operation);
}
