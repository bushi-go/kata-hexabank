package dev.rgoussu.hexabank.business.model.values;

import dev.rgoussu.hexabank.business.model.types.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyTest {

    @ParameterizedTest
    @CsvSource(value = {"10,10","10,20","10,30"})
    public void givenTwoMoneyInSameCurrencyShouldAdd(int initialAmount, int amountToAdd){
        Money money = Money.get(initialAmount, Currency.EUR);
        Money toAdd = Money.get(amountToAdd, Currency.EUR);
        Money expected = Money.get(initialAmount+amountToAdd, Currency.EUR);
        Money actual = money.plus(toAdd);
        assertEquals(expected, actual);
    }

    @Test
    public void givenTwoMoneyInDifferentCurrencyShouldThrow(){
        Money money = Money.get(10, Currency.EUR);
        Money toAdd = Money.get(10, Currency.USD);
        assertThrows(IllegalArgumentException.class, ()-> money.plus(toAdd));
    }
}
