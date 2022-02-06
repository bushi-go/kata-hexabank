package dev.rgoussu.hexabank.rest.adapters.endpoints;

import dev.rgoussu.hexabank.core.model.dto.DepositResult;
import dev.rgoussu.hexabank.core.model.types.DepositStatus;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.InvalidOperationParameterException;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ApplicationError;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ErrorCode;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto.DepositResultDto;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest adapter for all account operation.
 */
@RestController
@RequestMapping("/account")
@Slf4j
public class AccountOperationRestAdapter implements AccountOperationsPort<ResponseEntity<?>> {

  private final AccountOperationService service;

  public AccountOperationRestAdapter(AccountOperationService service) {
    this.service = service;
  }

  @PutMapping(value = "/{accountId}/deposit", consumes = {
      MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  @Override
  public ResponseEntity<?> deposit(@PathVariable("accountId") String accountId,
                                   @RequestBody Money deposit) {
    checkDeposit(accountId, deposit);
    if (accountId == null
        || deposit == null
        || deposit.getAmount() == null
        || deposit.getAmount().equals(BigDecimal.ZERO)
        || deposit.getCurrency() == null) {
      return ResponseEntity.badRequest().build();
    }
    log.info("[Account n°{}] processing deposit of {}", accountId, deposit);
    DepositResult result = service.processDeposit(accountId, deposit);
    if (DepositStatus.SUCCESS.equals(result.getStatus())) {
      log.info("[Account n°{}] deposit of {} successful", accountId, deposit);
      return ResponseEntity.ok(DepositResultDto.builder()
          .balance(result.getBalance())
          .deposit(deposit)
          .build());
    }

    //TODO refactor this by getting exception thrown from service
    ApplicationError.ApplicationErrorBuilder builder = ApplicationError.builder()
        .timeStamp(Instant.now());
    switch (result.getError()) {
      case UNKNOWN_ACCOUNT -> {
        log.warn("[Account n°{}] no such account", accountId);
        builder.errorCode(ErrorCode.UNKNOWN_ACCOUNT)
            .message("Account " + accountId + " does not exist");
      }
      case COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY -> {
        log.warn("[Account n°{}] could not get exchange rate for this deposit", accountId);
        builder.errorCode(ErrorCode.EXCHANGE_RATE_UNAVAILABLE)
            .message("Deposit requires a conversion, and "
                + "no exchange rate provider could provide one")
            .build();
      }
      default -> {
        log.warn(
            "[Account n°{}] an error occured during processing and the deposit could not be made",
            accountId);
        builder.errorCode(ErrorCode.UNKNOWN_ERROR)
            .message("An unexpected error occurred during processing");
      }
    }
    return builder.build().toResponse();
  }

  private void checkDeposit(String accountId, Money deposit) throws IllegalArgumentException {
    if (accountId == null) {
      throw new InvalidOperationParameterException(ErrorCode.NO_ACCOUNT_NUMBER_PROVIDED,
          "No account id was provided");
    }
    if (deposit == null) {
      throw new InvalidOperationParameterException(ErrorCode.NO_DEPOSIT_PROVIDED,
          "No deposit was provided");
    }
    if (deposit.getAmount() == null || deposit.getAmount().equals(BigDecimal.ZERO)) {
      throw new InvalidOperationParameterException(ErrorCode.INVALID_DEPOSIT_AMOUNT,
          "Invalid amount for deposit" + deposit.getAmount());
    }
    if (deposit.getCurrency() == null) {
      throw new InvalidOperationParameterException(ErrorCode.NO_CURRENCY_PROVIDED,
          "No currency was provided");
    }
  }
}
