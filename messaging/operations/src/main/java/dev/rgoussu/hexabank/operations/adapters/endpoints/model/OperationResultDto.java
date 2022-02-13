package dev.rgoussu.hexabank.operations.adapters.endpoints.model;

import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class OperationResultDto {
  private OperationStatus operationStatus;
  @Builder.Default
  private OperationError error = OperationError.NONE;
  private String accountId;
  private Money balance;
}
