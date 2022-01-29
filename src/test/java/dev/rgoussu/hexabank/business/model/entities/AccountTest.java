package dev.rgoussu.hexabank.business.model.entities;

import dev.rgoussu.hexabank.business.model.types.Currency;
import dev.rgoussu.hexabank.business.model.values.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {



    @ParameterizedTest
    @MethodSource("generateDeposits")
    public void givenAccountAndDepositShouldAddToBalance(Money initialBalance, Money amount){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.create(accountId,initialBalance);
        Account expected = Account.create(accountId,initialBalance.plus(amount));
        Account actual = underTest.deposit(amount);
        assertEquals(expected, actual);
    }

    @Test
    public void givenNegativeMoneyShouldThrow(){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.create(accountId,Money.get(0, Currency.EUR));
        assertThrows(IllegalArgumentException.class, ()->underTest.deposit(Money.get(-10, Currency.EUR)));
    }

    @Test
    public void givenUninitializedBalanceShouldInitializeAndProcessDeposit(){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.create(accountId);
        Money deposit = Money.get(10, Currency.EUR);
        Account expected = Account.create(accountId,deposit);
        Account actual = underTest.deposit(deposit);
        assertEquals(expected, actual);
    }

    public static Stream<Arguments> generateDeposits() {
        return Stream.of(
                Arguments.of(Money.get(0, Currency.EUR), Money.get(10, Currency.EUR)),

                Arguments.of(Money.get(0, Currency.EUR), Money.get(20, Currency.EUR)),

                Arguments.of(Money.get(0, Currency.EUR), Money.get(30, Currency.EUR)),

                Arguments.of(Money.get(10, Currency.EUR), Money.get(100, Currency.EUR))
        );
    }
}
