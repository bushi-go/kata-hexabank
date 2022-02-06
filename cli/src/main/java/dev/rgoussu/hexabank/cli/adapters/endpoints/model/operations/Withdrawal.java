package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.model.types.OperationType;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import org.springframework.stereotype.Component;

@Component
public class Withdrawal extends BankOperation{

  private static final int WITHDRAWAL_CODE = 2;

  protected Withdrawal(
      AccountOperationsPort<String> service,
      AccountValidator validator,
      CliDisplay display) {
    super(service, validator, display, WITHDRAWAL_CODE, OperationType.WITHDRAW);
  }
}
