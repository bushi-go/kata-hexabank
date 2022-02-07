package dev.rgoussu.hexabank.core.history.services;

import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.history.model.entities.AccountHistory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driven.AccountHistoryPersistencePort;

public class AccountHistoryManager implements AccountHistoryService{

  private final AccountHistoryPersistencePort accountHistoryPersistencePort;

  public AccountHistoryManager(AccountHistoryPersistencePort accountHistoryPersistencePort) {
    this.accountHistoryPersistencePort = accountHistoryPersistencePort;
  }

  @Override
  public AccountHistory getAccountHistory(String accountId) throws NoSuchAccountException {
    return accountHistoryPersistencePort.findAccountHistory(accountId);
  }

  @Override
  public void recordOperationToHistory(String accountId, AccountOperationSummary operationSummary) {
    AccountHistory accountHistory = accountHistoryPersistencePort.findAccountHistory(accountId);
    accountHistoryPersistencePort.saveAccountHistory(accountHistory.registerOperation(operationSummary));
  }
}
