package dev.rgoussu.hexabank.core;

import dev.rgoussu.hexabank.core.history.ports.driven.AccountHistoryPersistencePort;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.operations.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.history.services.AccountHistoryManager;
import dev.rgoussu.hexabank.core.history.services.AccountHistoryService;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationManager;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;

public enum AccountServicesFactory {
  INSTANCE;


  public AccountOperationService getOperationService(AccountHistoryPort accountHistoryPort,
                                                     ExchangeRateProviderPort exchangeRateProviderPort,
                                                     AccountPersistencePort accountPersistencePort) {
    return new AccountOperationManager(accountHistoryPort, exchangeRateProviderPort,
        accountPersistencePort);
  }

  public AccountHistoryService getAccountHistoryService(
      AccountPersistencePort accountPersistencePort,
      AccountHistoryPersistencePort accountHistoryPersistencePort) {
    return new AccountHistoryManager(accountPersistencePort,accountHistoryPersistencePort);
  }

}
