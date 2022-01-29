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
        Account underTest = Account.builder().accountId(accountId).balance(initialBalance).build();
        Account expected = Account.builder().accountId(accountId).balance(initialBalance.plus(amount)).build();
        Account actual = underTest.deposit(amount);
        assertEquals(expected, actual);
    }

    @Test
    public void givenNegativeMoneyShouldThrow(){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.builder().accountId(accountId).balance(Money.get(0, Currency.EUR)).build();
        assertThrows(IllegalArgumentException.class, ()->underTest.deposit(Money.get(-10, Currency.EUR)));
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
