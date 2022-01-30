package dev.rgoussu.hexabank.core.model.values;

import dev.rgoussu.hexabank.core.model.types.Currency;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class to represent monetary amount in a given currency, and encapsulate money arithmetics.
 * This is an immutable class using a value object pattern.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class Money {
  private BigDecimal amount;
  private Currency currency;

  public static Money get(Number amount, Currency currency) {
    return new Money(BigDecimal.valueOf(amount.doubleValue()), currency);
  }

  /**
   * Method to add two monetary amount.
   *
   * <p>Adding Money with different currencies is not allowed
   * and will result in an exception being thrown,
   * as it requires getting the exchange rate between the two beforehand,
   * which is variable (see {@link #convert(Currency, double) method}.
   *
   * @param other the amount to add to the current money instance
   * @return a new instance with the added amount
   * @throws IllegalArgumentException if the currencies of the two Money instance do not match
   */
  public Money plus(Money other) throws IllegalArgumentException {
    if (!other.currency.equals(currency)) {
      throw new IllegalArgumentException("Can not add money in different currencies : "
          + this.currency
          + " to "
          + other.currency);
    }
    return Money.get(other.amount.add(this.amount), currency);
  }

  /**
   * Convert the current money instance to the given currency.
   * Throws an exception if the exchange rate is negative or zero.
   *
   * @param otherCurrency the destination currency
   * @param exchangeRate  the exchange rate between the current currency
   *                      and the destination currency
   * @return a new Money instance with the amount converted to the destination currency
   * @throws IllegalArgumentException if the exchange rate is inferior or equal to 0
   */
  public Money convert(Currency otherCurrency, double exchangeRate)
      throws IllegalArgumentException {
    if (exchangeRate <= 0) {
      throw new IllegalArgumentException(
          "Invalid exchangeRate :" + exchangeRate + " when trying to convert from "
              + this.currency + " to " + otherCurrency);
    }
    return Money.get(amount.multiply(BigDecimal.valueOf(exchangeRate)), otherCurrency);
  }

  @Override
  public String toString() {
    return amount.toString() + " " + currency.getSymbol();
  }

}
