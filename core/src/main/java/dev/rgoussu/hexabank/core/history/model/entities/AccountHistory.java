package dev.rgoussu.hexabank.core.history.model.entities;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
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
public class AccountHistory {
  private String accountId;
  @Singular
  private SortedSet<AccountOperationSummary> operations;

  public AccountHistory registerOperation(AccountOperationSummary operationSummary) {
    return this.toBuilder().operation(operationSummary).build();
  }
}
