package dev.rgoussu.hexabank.cli.adapters.endpoints;

import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.history.services.AccountHistoryService;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import org.springframework.stereotype.Component;

/**
 * Driven adapter for the account history.
 */
@Component
public class CliAccountHistoryAdapter implements AccountHistoryPort {

  private final AccountHistoryService accountHistoryService;

  CliAccountHistoryAdapter(AccountHistoryService accountHistoryService) {
    this.accountHistoryService = accountHistoryService;
  }

  @Override
  public AccountSummary getAccountHistory(String accountId)
      throws NoSuchAccountException {
    return accountHistoryService.getAccountOperationSummary(accountId);
  }

  @Override
  public void registerOperationToHistory(String accountId, AccountOperationSummary operation) {
    accountHistoryService.recordOperationToHistory(accountId, operation);
  }
}
