package dev.rgoussu.hexabank.core.operations.model.types;

import dev.rgoussu.hexabank.core.operations.model.entities.Account;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.util.function.BiFunction;

/**
 * Available operation type.
 * Encapsulate the corresponding account operation to improve code reuse.
 */
public enum OperationType {
  DEPOSIT {
    @Override
    public BiFunction<Account, Money, Account> getOperation() {
      return Account::deposit;
    }
  }, WITHDRAW {
    @Override
    public BiFunction<Account, Money, Account> getOperation() {
      return Account::withdraw;
    }
  };

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  public abstract BiFunction<Account, Money, Account> getOperation();
}
