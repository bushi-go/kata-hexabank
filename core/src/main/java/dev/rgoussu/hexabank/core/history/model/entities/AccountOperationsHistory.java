package dev.rgoussu.hexabank.core.history.model.entities;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.util.SortedSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class AccountOperationsHistory {
  private String accountId;
  private Money balance;
  @Singular
  private SortedSet<AccountOperationSummary> operations;

  public AccountOperationsHistory registerOperation(AccountOperationSummary operationSummary) {
    return this.toBuilder().operation(operationSummary).build();
  }
}
