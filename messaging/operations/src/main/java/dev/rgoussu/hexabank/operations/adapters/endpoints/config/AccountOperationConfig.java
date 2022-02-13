package dev.rgoussu.hexabank.operations.adapters.endpoints.config;

import dev.rgoussu.hexabank.core.AccountServicesFactory;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.operations.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;
import javax.inject.Singleton;

public class AccountOperationConfig {

  @Singleton
  public AccountOperationService accountOperationService(
      ExchangeRateProviderPort exchangeRateProviderPort,
      AccountPersistencePort accountPersistencePort, AccountHistoryPort accountHistoryPort) {
    return AccountServicesFactory.INSTANCE.getOperationService(accountHistoryPort,
        exchangeRateProviderPort, accountPersistencePort);
  }
}
