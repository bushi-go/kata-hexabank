package dev.rgoussu.hexabank.core.model.types;

import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.model.values.Money;
import java.util.function.BiFunction;

public enum OperationType {
  DEPOSIT{
    @Override
    public BiFunction<Account, Money, Account> getOperation() {
      return Account::deposit;
    }
  }, WITHDRAW{
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
