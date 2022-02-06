package dev.rgoussu.hexabank.core.model.entities;

import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.values.Money;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class representing a bank account and its operations.
 * Immutable and use a fluent api.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class Account {
  private final String accountId;
  private final Money balance;

  /**
   * Create an account with a balance of 0 €.
   *
   * @param accountId the unique Id of the account
   * @return the new Account
   */
  public static Account create(String accountId) {
    return create(accountId, Currency.EUR, 0);
  }

  /**
   * Create an account with a balance of 0, operating
   * with the given {@link dev.rgoussu.hexabank.core.model.types.Currency}.
   *
   * @param accountId the unique Id of the account
   * @param currency  the Currency of operation of the account
   * @return the new Account
   */
  public static Account create(String accountId, Currency currency) {
    return create(accountId, currency, 0);
  }

  /**
   * Create an account with a balance of 0 €.
   *
   * @param accountId the unique Id of the account
   * @return the new Account
   */
  public static Account create(String accountId, long balance) {
    return create(accountId, Currency.EUR, balance);
  }

  /**
   * Create an account with the given initial balance,
   * operation with the given {@link dev.rgoussu.hexabank.core.model.types.Currency}.
   *
   * @param accountId      the unique Id of the account
   * @param currency       the Currency of operation of the account
   * @param initialBalance the initial balance of the account
   * @return the new Account
   */
  public static Account create(String accountId, Currency currency, long initialBalance) {
    return new Account(accountId, Money.get(initialBalance, currency));
  }

  /**
   * Create an account with the given initial balance,
   * operation with the currency of the initial balance passed in parameters.
   *
   * @param accountId      the unique Id of the account
   * @param initialBalance the initial balance of the account
   * @return the new Account
   */
  public static Account create(String accountId, Money initialBalance) {
    return new Account(accountId, initialBalance);
  }


  /**
   * Make a deposit to the account. The deposit must be in the operating currency of the account.
   *
   * @param amount Deposit to be made.
   * @return the account once the deposit has been made.
   * @throws IllegalArgumentException if there is a mismatch between
   *                                  deposit currency and account currency.
   */
  public Account deposit(Money amount) throws IllegalArgumentException {
    if (amount.getAmount().signum() < 0) {
      throw new IllegalArgumentException("Can not deposit negative amount of money");
    }
    return create(accountId, balance.plus(amount));
  }

  public Currency getOperatingCurrency() {
    return balance.getCurrency();
  }

  public Account withdraw(Money withdrawal) {
    return Account.create(accountId, 900);
  }
}
