package dev.rgoussu.hexabank.cli.adapters;

import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import org.springframework.stereotype.Component;

/**
 * CLI based account operation adapter.
 */
@Component
public class CliAccountOperationsAdapter implements AccountOperationsPort<Void> {
  @Override
  public Void deposit(String accountId, Money deposit) {
    return null;
  }
}
