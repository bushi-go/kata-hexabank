package dev.rgoussu.hexabank.rest.adapters.endpoints;

import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import dev.rgoussu.hexabank.core.operations.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.InvalidOperationParameterException;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ApplicationError;
import dev.rgoussu.hexabank.rest.adapters.endpoints.exceptions.model.ErrorCode;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto.OperationResultDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
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

    checkAmount(accountId, deposit);
    return processOperation(accountId, deposit, OperationType.DEPOSIT);
  }

  @PutMapping(value = "/{accountId}/withdraw", consumes = {
      MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  @Override
  public ResponseEntity<?> witdraw(@PathVariable("accountId") String accountId,
                                   @RequestBody Money withdraw) {
    checkAmount(accountId, withdraw);
    return processOperation(accountId, withdraw, OperationType.WITHDRAW);
  }

  private ResponseEntity<?> processOperation(String accountId, Money amount, OperationType type) {
    OperationResult result = getOperation(type).apply(accountId, amount);
    if (OperationStatus.SUCCESS.equals(result.getStatus())) {
      log.info("[Account n°{}] {} of {} successful", accountId, type, amount);
      return ResponseEntity.ok(OperationResultDto.builder()
          .balance(result.getBalance())
          .amount(amount)
          .type(type)
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
        log.warn("[Account n°{}] could not get exchange rate for this {}", accountId, type);
        builder.errorCode(ErrorCode.EXCHANGE_RATE_UNAVAILABLE)
            .message(type + " requires a conversion, and "
                + "no exchange rate provider could provide one")
            .build();
      }
      default -> {
        log.warn(
            "[Account n°{}] an error occured during processing and the {} could not be made",
            accountId, type);
        builder.errorCode(ErrorCode.UNKNOWN_ERROR)
            .message("An unexpected error occurred during processing");
      }
    }
    return builder.build().toResponse();
  }

  private BiFunction<String, Money, OperationResult> getOperation(OperationType type) {
    return switch (type) {
      case DEPOSIT -> service::processDeposit;
      case WITHDRAW -> service::processWithdrawal;
    };
  }

  private void checkAmount(String accountId, Money deposit) throws IllegalArgumentException {
    if (accountId == null) {
      throw new InvalidOperationParameterException(ErrorCode.NO_ACCOUNT_NUMBER_PROVIDED,
          "No account id was provided");
    }
    if (deposit == null) {
      throw new InvalidOperationParameterException(ErrorCode.NO_DEPOSIT_PROVIDED,
          "No amount was provided");
    }
    if (deposit.getAmount() == null || deposit.getAmount().equals(BigDecimal.ZERO)) {
      throw new InvalidOperationParameterException(ErrorCode.INVALID_DEPOSIT_AMOUNT,
          "Invalid amount for operation" + deposit.getAmount());
    }
    if (deposit.getCurrency() == null) {
      throw new InvalidOperationParameterException(ErrorCode.NO_CURRENCY_PROVIDED,
          "No currency was provided");
    }
  }
}
