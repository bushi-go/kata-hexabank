package dev.rgoussu.hexabank.core.model.dto;

import dev.rgoussu.hexabank.core.model.types.DepositError;
import dev.rgoussu.hexabank.core.model.types.DepositStatus;
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
public class DepositResult {

  private DepositStatus status;
  @Builder.Default
  private DepositError error = DepositError.NONE;
  private String accountId;
  private Money balance;

  public static DepositResult success(String accountId, Money money) {
    return builder().accountId(accountId).balance(money).status(DepositStatus.SUCCESS).build();
  }

  public static DepositResult noSuchAccount(String accountId) {
    return builder().accountId(accountId).status(DepositStatus.FAILURE)
        .error(DepositError.UNKNOWN_ACCOUNT).build();
  }

  public static DepositResult unavailableExchangeRate(String accountId) {
    return builder().accountId(accountId).status(DepositStatus.FAILURE)
        .error(DepositError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY).build();
  }
}
