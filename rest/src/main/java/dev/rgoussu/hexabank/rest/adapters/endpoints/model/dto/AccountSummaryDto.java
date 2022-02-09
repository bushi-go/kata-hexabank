package dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto;

import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dto for the account summary exposed via the rest api.
 */
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Slf4j
public class AccountSummaryDto {

  private Money balance;
  private Money totalCreditsMovements;
  private Money totalDebitMovements;
  private List<OperationSummaryDto> operations;

  /**
   * Factory method to build the dto from an account summary.
   *
   * @param summary the account summary
   * @return a dto representation
   */
  public static AccountSummaryDto fromOperationsHistory(AccountSummary summary) {
    return builder()
        .balance(summary.getBalance())
        .operations(
            summary.getOperations().stream().map(OperationSummaryDto::fromAccountOperationSummary)
                .collect(
                    Collectors.toList()))
        .totalCreditsMovements(summary.getOperations().stream()
            .filter(op -> OperationType.DEPOSIT.equals(op.getOperationType())
                && OperationStatus.SUCCESS.equals(op.getOperationStatus()))
            .reduce(
                Money.get(0, summary.getBalance().getCurrency()),
                (acc, op) ->
                    op.getConversion().map(currencyConversion -> op.getOperationAmount()
                            .convert(currencyConversion.getTo(),
                                currencyConversion.getExchangeRate()))
                        .orElse(op.getOperationAmount()).plus(acc), Money::plus))
        .totalDebitMovements(summary.getOperations().stream()
            .filter(op -> OperationType.WITHDRAW.equals(op.getOperationType())
                && OperationStatus.SUCCESS.equals(op.getOperationStatus()))
            .reduce(
                Money.get(0, summary.getBalance().getCurrency()),
                (acc, op) ->
                    op.getConversion().map(currencyConversion -> op.getOperationAmount()
                            .convert(currencyConversion.getTo(),
                                currencyConversion.getExchangeRate()))
                        .orElse(op.getOperationAmount()).plus(acc), Money::plus))
        .build();
  }
}
