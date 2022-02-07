package dev.rgoussu.hexabank.cli.adapters.endpoints;

import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import dev.rgoussu.hexabank.core.operations.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;
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
    OperationResult result = accountOperationService.processDeposit(accountId, deposit);
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
  public String witdraw(String accountId, Money withdraw) {
    log.info("[Account n° {}] processing withdrawal on account", accountId);
    OperationResult result = accountOperationService.processWithdrawal(accountId, withdraw);
    String message;
    switch (result.getStatus()) {
      case SUCCESS -> {
        log.info(
            "[Account n° {}] successfully processed withdrawal. New balance : {}", accountId,
            result.getBalance());
        message = "Withdrawal successfull, new balance : " + result.getBalance();
      }
      case FAILURE -> {
        log.info("[Account n° {}] failed to process withdrawal : {}", accountId,
            result.getError());
        message = "Failed to process deposit :" + result.getError();
      }
      default -> message = "Withdrawal is a " + result.getStatus()
          + (result.getError() != null ? " error : " + result.getError() : "");
    }
    return message;
  }

  @Override
  public boolean isValidAccount(String account) {
    return accountOperationService.checkAccount(account);
  }
}
