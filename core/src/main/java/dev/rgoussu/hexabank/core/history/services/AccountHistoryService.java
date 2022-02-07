package dev.rgoussu.hexabank.core.history.services;

import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.history.model.entities.AccountOperationsHistory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;

/**
 * Interface to account history service
 */
public interface AccountHistoryService {

  /**
   * Get the history of operations for the given account
   * @param accountId the id of the account
   * @return its operation history
   * @throws NoSuchAccountException if the account does not exists
   */
  AccountOperationsHistory getAccountOperationSummary(String accountId) throws NoSuchAccountException;

  void recordOperationToHistory(String accountId,AccountOperationSummary operationSummary);
}
