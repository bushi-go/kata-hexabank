package dev.rgoussu.hexabank.rest.adapters.endpoints;

import dev.rgoussu.hexabank.core.model.dto.DepositResult;
import dev.rgoussu.hexabank.core.model.types.DepositStatus;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto.DepositResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest adapter for all account operation.
 */
@RestController("/account")
@Slf4j
public class AccountOperationRestAdapter implements AccountOperationsPort<ResponseEntity<?>> {

  private final AccountOperationService service;

  public AccountOperationRestAdapter(AccountOperationService service) {
    this.service = service;
  }

  @PutMapping("/{accountId}/deposit")
  @Override
  public ResponseEntity<?> deposit(@PathVariable("accountId") String accountId,
                                   @RequestBody Money deposit) {
    log.info("[Account n°{}] processing deposit of {}", accountId, deposit);
    DepositResult result = service.processDeposit(accountId, deposit);
    if (DepositStatus.SUCCESS.equals(result.getStatus())) {
      log.info("[Account n°{}] deposit of {} successful", accountId, deposit);
      return ResponseEntity.ok(DepositResultDto.builder()
          .balance(result.getBalance())
          .deposit(deposit)
          .build());
    }
    switch (result.getError()) {
      case UNKNOWN_ACCOUNT -> {
        log.warn("[Account n°{}] no such account", accountId);
        return ResponseEntity.notFound().build();
      }
      case COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY -> {
        log.warn("[Account n°{}] could not get exchange rate for this deposit", accountId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
      }
      default -> log.warn(
          "[Account n°{}] an error occured during processing and the deposit could not be made",
          accountId);
    }
    return ResponseEntity.internalServerError().build();
  }
}
