package dev.rgoussu.hexabank.core.history.ports.driven;

import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.history.model.entities.AccountHistory;

public interface AccountHistoryPersistencePort {

  /**
   * Persist the account history of operations
   *
   * @param history the history to be saved
   */
  void saveAccountHistory(AccountHistory history);

  /**
   * Collect and return the account history for the given account
   * @param accountId the account id
   * @return the history of operations
   * @throws NoSuchAccountException if account could not be found
   */
  AccountHistory findAccountHistory(String accountId) throws NoSuchAccountException;

}
