package dev.rgoussu.hexabank.rest.adapters.endpoints.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.services.AccountOperationManager;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.serializer.MoneySerializer;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for jackson object mapper and wiring of the core domain to its adapters.
 */
@Configuration
public class HexaBankRestConfig {

  private static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
  private final MoneySerializer moneySerializer;

  public HexaBankRestConfig(MoneySerializer moneySerializer) {
    this.moneySerializer = moneySerializer;
  }


  /**
   * Bean for the account operation service.
   *
   * @param exchangeRateProviderPort exchange rate provider driven port
   * @param accountPersistencePort account persistence driven port
   * @return properly wired up account operations service
   */
  @Bean
  public AccountOperationService accountOperationService(
      ExchangeRateProviderPort exchangeRateProviderPort,
      AccountPersistencePort accountPersistencePort) {
    // TODO find a way to avoid this implementation to creep out of the core domain
    // a Factory ought to do the trick
    return new AccountOperationManager(accountPersistencePort, exchangeRateProviderPort);
  }

  /**
   * Slightly adjusted object mapper.
   *
   * @return Jackson2ObjectMapperBuilderCustomizer customized to our needs
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
        .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)))
        .serializers(moneySerializer);
  }
}

