package dev.rgoussu.hexabank.cli.operations.config;

import dev.rgoussu.hexabank.cli.operations.BankOperation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankOperationConfig {
  @Bean
  Map<Integer, BankOperation> operations(List<BankOperation> operationList){
    return operationList.stream().collect(Collectors.toMap(BankOperation::getCode, Function.identity()));
  }
}
