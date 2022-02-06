package dev.rgoussu.hexabank.rest.adapters.persistence;

import dev.rgoussu.hexabank.core.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.rest.adapters.persistence.model.AccountRecord;
import org.springframework.stereotype.Component;

/**
 * Account persistence adapter wrapping all repositories needed to register an account.
 * MongoDb is used as the backing store for account data.
 */
@Component
public class AccountPersistenceAdapter implements AccountPersistencePort {

  private final MongoAccountPersistenceRepository repository;

  AccountPersistenceAdapter(MongoAccountPersistenceRepository repository) {
    this.repository = repository;
  }

  @Override
  public Account findByAccountId(String accountId) throws NoSuchAccountException {
    return repository.findById(accountId)
        .map(AccountRecord::toAccount)
        .orElseThrow(() -> new NoSuchAccountException("No account with id " + accountId));
  }

  @Override
  public Account save(Account account) {
    return repository.save(AccountRecord.fromAccount(account)).toAccount();
  }
}
