package dev.rgoussu.hexabank.core.operations.ports.driven;

import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.operations.model.entities.Account;

/**
 * Account persistence adapter, to retrieve and save account state.
 */
public interface AccountPersistencePort {
  /**
   * Find an account by its id. Throw a specific exception if none is found for the given id.
   *
   * @param accountId target account id
   * @return the account with its current balance
   * @throws NoSuchAccountException if no account match the given id
   */
  Account findByAccountId(String accountId) throws NoSuchAccountException;

  /**
   * Persist the current state of an account.
   *
   * @param account the account to save
   * @return the account once saved
   */
  Account save(Account account);
}
