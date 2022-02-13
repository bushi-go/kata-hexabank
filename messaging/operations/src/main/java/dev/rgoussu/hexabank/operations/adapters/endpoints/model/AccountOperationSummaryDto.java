package dev.rgoussu.hexabank.operations.adapters.endpoints.model;

import dev.rgoussu.hexabank.core.history.model.values.CurrencyConversion;
import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AccountOperationSummaryDto {
  private String accountId;
  private Instant operationDate;
  private CurrencyConversion currencyConversion;
  private Money amount;
  private Money balanceAfterOperation;
  private OperationStatus status;
  private OperationError error;
}
