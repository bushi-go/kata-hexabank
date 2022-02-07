package dev.rgoussu.hexabank.core.operations.model.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.rgoussu.hexabank.core.operations.model.types.Currency;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MoneyTest {

  @ParameterizedTest
  @CsvSource(value = {"10,10", "10,20", "10,30"})
  public void givenTwoMoneyInSameCurrencyShouldAdd(int initialAmount, int amountToAdd) {
    Money money = Money.get(initialAmount, Currency.EUR);
    Money toAdd = Money.get(amountToAdd, Currency.EUR);
    Money expected = Money.get(initialAmount + amountToAdd, Currency.EUR);
    Money actual = money.plus(toAdd);
    assertEquals(expected, actual);
  }

  @Test
  public void givenTwoMoneyToAddInDifferentCurrencyShouldThrow() {
    Money money = Money.get(10, Currency.EUR);
    Money toAdd = Money.get(10, Currency.USD);
    assertThrows(IllegalArgumentException.class, () -> money.plus(toAdd));
  }

  @Test
  public void givenCurrencyAndExchangeRateShouldProperlyConvert() {
    Money money = Money.get(100, Currency.EUR);
    Money expected = Money.get(88, Currency.USD);
    Money actual = money.convert(Currency.USD, 0.88);
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @ValueSource(doubles = {0, -10.0})
  public void givenCurrencyAndInvalidExchangeRateShouldThrow(double exchangeRate) {
    Money money = Money.get(100, Currency.EUR);
    assertThrows(IllegalArgumentException.class, () -> money.convert(Currency.USD, exchangeRate));
  }

  @ParameterizedTest
  @CsvSource({
      "15,20",
      "30,45",
      "60, 10"})
  public void givenTwoCurrenciesShouldSubstract(double amount, double substract) {
    Money money = Money.get(amount, Currency.EUR);
    Money sub = Money.get(substract, Currency.EUR);
    Money actual = money.minus(sub);
    Money expected = Money.get(amount - substract, Currency.EUR);
    assertEquals(actual, expected);
  }

  @Test
  public void givenTwoMoneyToSubstractInDifferentCurrencyShouldThrow() {
    Money money = Money.get(10, Currency.EUR);
    Money toAdd = Money.get(10, Currency.USD);
    assertThrows(IllegalArgumentException.class, () -> money.minus(toAdd));
  }
}
