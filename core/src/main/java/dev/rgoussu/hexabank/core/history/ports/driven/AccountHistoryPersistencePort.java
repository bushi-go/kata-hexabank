package dev.rgoussu.hexabank.core.history.ports.driven;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import java.util.SortedSet;

/**
 * Account persistence port.
 */
public interface AccountHistoryPersistencePort {

  /**
   * Persist the account history of operations.
   *
   * @param accountId the account id
   * @param history   the history to be saved
   */
  void recordOperationSummary(String accountId, AccountOperationSummary history);

  /**
   * Collect and return the account history for the given account.
   *
   * @param accountId the account id
   * @return the history of operations
   * @throws NoSuchAccountException if account could not be found
   */
  SortedSet<AccountOperationSummary> findAccountHistory(String accountId)
      throws NoSuchAccountException;

}
