package dev.rgoussu.hexabank.cli.adapters.endpoints.config;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations.BankOperation;
import dev.rgoussu.hexabank.core.model.types.OperationType;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to provide a Map of all implemented operations, for convenience purposes.
 */

@Configuration
public class BankOperationConfig {
  @Bean
  Map<Integer, BankOperation> operations(List<BankOperation> operationList) {
    return operationList.stream()
        .collect(Collectors.toMap(BankOperation::getCode, Function.identity()));
  }

}
