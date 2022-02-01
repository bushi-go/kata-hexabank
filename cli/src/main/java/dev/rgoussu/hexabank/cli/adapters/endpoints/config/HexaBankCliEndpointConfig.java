package dev.rgoussu.hexabank.cli.adapters.endpoints.config;

import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.services.AccountOperationManager;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HexaBankCliEndpointConfig {

  // We manage the creation of this bean through an @Bean to avoid having spring framework dependencies creeping into the core domain
  @Bean
  public AccountOperationService accountOperationService(ExchangeRateProviderPort exchangeRateProviderPort, AccountPersistencePort accountPersistencePort){
    return new AccountOperationManager(accountPersistencePort,exchangeRateProviderPort);
  }
}
