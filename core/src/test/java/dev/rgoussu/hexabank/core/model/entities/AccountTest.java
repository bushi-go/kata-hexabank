package dev.rgoussu.hexabank.core.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.values.Money;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AccountTest {


  public static Stream<Arguments> generateDeposits() {
    return Stream.of(
        Arguments.of(Money.get(0, Currency.EUR), Money.get(10, Currency.EUR)),

        Arguments.of(Money.get(0, Currency.EUR), Money.get(20, Currency.EUR)),

        Arguments.of(Money.get(0, Currency.EUR), Money.get(30, Currency.EUR)),

        Arguments.of(Money.get(10, Currency.EUR), Money.get(100, Currency.EUR))
    );
  }

  @ParameterizedTest
  @MethodSource("generateDeposits")
  public void givenAccountAndDepositShouldAddToBalance(Money initialBalance, Money amount) {
    String accountId = UUID.randomUUID().toString();
    Account underTest = Account.create(accountId, initialBalance);
    Account expected = Account.create(accountId, initialBalance.plus(amount));
    Account actual = underTest.deposit(amount);
    assertEquals(expected, actual);
  }

  @Test
  public void givenNegativeMoneyShouldThrow() {
    String accountId = UUID.randomUUID().toString();
    Account underTest = Account.create(accountId, Money.get(0, Currency.EUR));
    assertThrows(IllegalArgumentException.class,
        () -> underTest.deposit(Money.get(-10, Currency.EUR)));
  }

  @Test
  public void givenUninitializedBalanceShouldInitializeAndProcessDeposit() {
    String accountId = UUID.randomUUID().toString();
    Account underTest = Account.create(accountId);
    Money deposit = Money.get(10, Currency.EUR);
    Account expected = Account.create(accountId, deposit);
    Account actual = underTest.deposit(deposit);
    assertEquals(expected, actual);
  }
}
