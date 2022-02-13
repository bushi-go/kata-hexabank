package dev.rgoussu.hexabank.rest.adapters.endpoints;

import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.history.services.AccountHistoryService;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto.AccountSummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account history adapter.
 * Embed a single rest entry point to get a summary for a given account
 */
@RestController
@Slf4j
public class AccountHistoryAdapter implements AccountHistoryPort<Void> {


  private final AccountHistoryService accountHistoryService;

  AccountHistoryAdapter(AccountHistoryService accountHistoryService) {
    this.accountHistoryService = accountHistoryService;
  }

  @GetMapping("/account/{accountId}/summary")
  public ResponseEntity<?> getAccountSummary(@PathVariable("accountId") String accountId) {
    AccountSummary summary = accountHistoryService.getAccountOperationSummary(accountId);
    return ResponseEntity.ok(AccountSummaryDto.fromOperationsHistory(summary));
  }

  @Override
  public AccountSummary getAccountHistory(String accountId)
      throws NoSuchAccountException {
    return accountHistoryService.getAccountOperationSummary(accountId);
  }

  @Override
  public Void registerOperationToHistory(String accountId, AccountOperationSummary operation) {
    accountHistoryService.recordOperationToHistory(accountId, operation);
    return null;
  }
}
