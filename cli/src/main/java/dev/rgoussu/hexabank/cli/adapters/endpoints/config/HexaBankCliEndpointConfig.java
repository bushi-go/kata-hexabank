package dev.rgoussu.hexabank.cli.adapters.endpoints.config;

import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.services.AccountOperationManager;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
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
      ExchangeRateProviderPort exchangeRateProviderPort,
      AccountPersistencePort accountPersistencePort) {
    return new AccountOperationManager(accountPersistencePort, exchangeRateProviderPort);
  }
}
