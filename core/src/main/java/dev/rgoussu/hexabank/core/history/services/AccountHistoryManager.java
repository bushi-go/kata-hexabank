package dev.rgoussu.hexabank.core.history.services;

import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driven.AccountHistoryPersistencePort;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.operations.model.entities.Account;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
import java.util.SortedSet;

/**
 * Account history manager.
 */
public class AccountHistoryManager implements AccountHistoryService {

  private final AccountHistoryPersistencePort accountHistoryPersistencePort;
  private final AccountPersistencePort accountPersistencePort;

  public AccountHistoryManager(AccountPersistencePort accountPersistencePort,
                               AccountHistoryPersistencePort accountHistoryPersistencePort) {
    this.accountPersistencePort = accountPersistencePort;
    this.accountHistoryPersistencePort = accountHistoryPersistencePort;
  }

  @Override
  public AccountSummary getAccountOperationSummary(String accountId)
      throws NoSuchAccountException {
    Account account = accountPersistencePort.findByAccountId(accountId);
    SortedSet<AccountOperationSummary> operationsList =
        accountHistoryPersistencePort.findAccountHistory(accountId);
    return AccountSummary.builder()
        .operations(operationsList)
        .accountId(accountId)
        .balance(account.getBalance())
        .build();
  }

  @Override
  public void recordOperationToHistory(String accountId, AccountOperationSummary operationSummary)
      throws NoSuchAccountException {
    Account account = accountPersistencePort.findByAccountId(accountId);
    accountHistoryPersistencePort.recordOperationSummary(account.getAccountId(), operationSummary);
  }
}
