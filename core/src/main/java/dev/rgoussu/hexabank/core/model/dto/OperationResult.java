package dev.rgoussu.hexabank.core.model.dto;

import dev.rgoussu.hexabank.core.model.types.OperationError;
import dev.rgoussu.hexabank.core.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.model.values.Money;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO encapsulating the result of a deposit operation to a given account.
 */
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class OperationResult {

  private OperationStatus status;
  @Builder.Default
  private OperationError error = OperationError.NONE;
  private String accountId;
  private Money balance;

  public static OperationResult success(String accountId, Money money) {
    return builder().accountId(accountId).balance(money).status(OperationStatus.SUCCESS).build();
  }

  public static OperationResult noSuchAccount(String accountId) {
    return builder().accountId(accountId).status(OperationStatus.FAILURE)
        .error(OperationError.UNKNOWN_ACCOUNT).build();
  }

  public static OperationResult unavailableExchangeRate(String accountId) {
    return builder().accountId(accountId).status(OperationStatus.FAILURE)
        .error(OperationError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY).build();
  }
}
