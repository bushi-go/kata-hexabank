package dev.rgoussu.hexabank.cli.adapters.endpoints;

import dev.rgoussu.hexabank.core.model.dto.DepositResult;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CLI based account operation adapter.
 */
@Component
@Slf4j
public class CliAccountOperationsAdapter
    implements AccountOperationsPort<String>, AccountValidator {
  private final AccountOperationService accountOperationService;

  CliAccountOperationsAdapter(AccountOperationService accountOperationService) {
    this.accountOperationService = accountOperationService;
  }

  @Override
  public String deposit(String accountId, Money deposit) {
    log.info("[Account n° {}] processing deposit on account", accountId);
    DepositResult result = accountOperationService.processDeposit(accountId, deposit);
    String message;
    switch (result.getStatus()) {
      case SUCCESS -> {
        log.info(
            "[Account n° {}] successfully processed deposit. New balance : {}", accountId,
            result.getBalance());
        message = "Deposit successfull, new balance : " + result.getBalance();
      }
      case FAILURE -> {
        log.info("[Account n° {}] failed to process deposit : {}", accountId,
            result.getError());
        message = "Failed to process deposit :" + result.getError();
      }
      default -> message = "Deposit is a " + result.getStatus()
          + (result.getError() != null ? " error : " + result.getError() : "");
    }
    return message;
  }

  @Override
  public boolean isValidAccount(String account) {
    return accountOperationService.checkAccount(account);
  }
}
