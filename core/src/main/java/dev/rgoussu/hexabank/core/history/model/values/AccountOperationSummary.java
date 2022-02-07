package dev.rgoussu.hexabank.core.history.model.values;

import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.time.Instant;
import java.util.Comparator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode(of = {"operationDate","operationStatus","operationError", "operationType", "operationAmount"})
public class AccountOperationSummary implements Comparable<AccountOperationSummary> {

  private static final Comparator<AccountOperationSummary> COMPARATOR =
      Comparator.comparing(AccountOperationSummary::getOperationDate)
          .thenComparing(AccountOperationSummary::getOperationType);

  private Instant operationDate;
  private OperationStatus operationStatus;
  @Builder.Default
  private OperationError operationError = OperationError.NONE;
  private OperationType operationType;
  private Money operationAmount;
  private Money balanceAfterOperation;


  @Override
  public int compareTo(AccountOperationSummary other) {
    return COMPARATOR.compare(this, other);
  }
}
