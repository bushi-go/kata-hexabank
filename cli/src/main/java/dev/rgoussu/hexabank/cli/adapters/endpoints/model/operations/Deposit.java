package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.ports.driving.AccountOperationsPort;
import org.springframework.stereotype.Component;

@Component
public class Deposit extends MoneyBankOperation {
  private static final int DEPOSIT_CODE = 1;

  protected Deposit(
      AccountOperationsPort<String> service,
      AccountValidator validator,
      CliDisplay display) {
    super(service, validator, display, DEPOSIT_CODE, OperationType.DEPOSIT);
  }
}
