package dev.rgoussu.hexabank.cli.adapters.endpoints.config;

import dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations.BankOperation;
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
