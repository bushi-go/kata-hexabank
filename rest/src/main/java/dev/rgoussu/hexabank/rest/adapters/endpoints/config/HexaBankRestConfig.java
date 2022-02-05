package dev.rgoussu.hexabank.rest.adapters.endpoints.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.core.services.AccountOperationManager;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.serializer.MoneySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class HexaBankRestConfig {

    private final MoneySerializer moneySerializer;
    private static final String DATETIME_FORMAT="dd-MM-yyyy HH:mm:ss";

    public HexaBankRestConfig(MoneySerializer moneySerializer) {
        this.moneySerializer = moneySerializer;
    }

    // We manage the creation of this bean through an @Bean to avoid having spring framework dependencies creeping into the core domain
    @Bean
    public AccountOperationService accountOperationService(ExchangeRateProviderPort exchangeRateProviderPort,
                                                           AccountPersistencePort accountPersistencePort) {
        return new AccountOperationManager(accountPersistencePort, exchangeRateProviderPort);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)))
                .serializers(moneySerializer);
    }
}

