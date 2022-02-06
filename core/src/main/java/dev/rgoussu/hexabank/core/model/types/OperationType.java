package dev.rgoussu.hexabank.core.model.types;

import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.model.values.Money;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum OperationType {
  DEPOSIT{
    @Override
    public BiFunction<Account, Money, Account> getOperation() {
      return Account::deposit;
    }
  }, WITHDRAWAL{
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
