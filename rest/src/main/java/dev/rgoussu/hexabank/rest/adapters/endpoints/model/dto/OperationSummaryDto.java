package dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.model.values.CurrencyConversion;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A dto representation of an operation on an account.
 */
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OperationSummaryDto {
  private Instant operationDate;
  private OperationType operationType;
  private OperationStatus status;
  private Money operationAmount;
  private CurrencyConversion conversion;
  private Money balanceAfterOperation;

  /**
   * Method factory to build an instance of this dto class from its business counterpart.
   *
   * @param operationSummary the business representation to build from
   * @return an new instance of this dto class.
   */
  public static OperationSummaryDto fromAccountOperationSummary(
      AccountOperationSummary operationSummary) {
    OperationSummaryDto.OperationSummaryDtoBuilder builder =
        builder().operationType(operationSummary.getOperationType())
            .operationAmount(
                switch (operationSummary.getOperationType()) {
                  case DEPOSIT -> operationSummary.getOperationAmount();
                  case WITHDRAW -> operationSummary.getOperationAmount().getAmount().signum() > 0
                      ? operationSummary.getOperationAmount().negate()
                      : operationSummary.getOperationAmount();
                }).balanceAfterOperation(operationSummary.getBalanceAfterOperation())
            .operationDate(operationSummary.getOperationDate())
            .status(operationSummary.getOperationStatus());
    if (operationSummary.getConversion().isPresent()) {
      builder.conversion(operationSummary.getConversion().get());
    }
    return builder.build();
  }
}
