package dev.rgoussu.hexabank.cli.adapters.endpoints.config;

import dev.rgoussu.hexabank.core.AccountServicesFactory;
import dev.rgoussu.hexabank.core.history.ports.driven.AccountHistoryPersistencePort;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.history.services.AccountHistoryService;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.operations.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for setting up the domain bean and wire it up with spring dependency injection.
 * Doing it this way allows us to avoid any dependency to the core domain to any framework.
 */
@Configuration
public class HexaBankCliEndpointConfig {

  @Bean
  public AccountOperationService accountOperationService(
      AccountHistoryPort accountHistoryPort,
      ExchangeRateProviderPort exchangeRateProviderPort,
      AccountPersistencePort accountPersistencePort) {
    return AccountServicesFactory.INSTANCE.getOperationService(accountHistoryPort,
        exchangeRateProviderPort, accountPersistencePort);
  }

  @Bean
  public AccountHistoryService accountHistoryService(
      AccountPersistencePort accountPersistencePort,
      AccountHistoryPersistencePort accountHistoryPersistencePort) {
    return AccountServicesFactory.INSTANCE.getAccountHistoryService(accountPersistencePort,
        accountHistoryPersistencePort);
  }
}
