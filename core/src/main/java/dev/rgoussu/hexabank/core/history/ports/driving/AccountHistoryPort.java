package dev.rgoussu.hexabank.core.history.ports.driving;

import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.history.model.entities.AccountOperationsHistory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;

public interface AccountHistoryPort {
  /**
   * Get the history for the given account
   *
   * @param accountId the id of the account
   * @return the history of operations for the account
   * @throws NoSuchAccountException if the account does not exists
   */
  AccountOperationsHistory getAccountHistory(String accountId) throws NoSuchAccountException;

  void registerOperationToHistory(String accountId, AccountOperationSummary operation);
}
