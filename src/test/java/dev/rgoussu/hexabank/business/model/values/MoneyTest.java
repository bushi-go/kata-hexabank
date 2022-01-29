package dev.rgoussu.hexabank.business.model.values;

import dev.rgoussu.hexabank.business.model.types.Currency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTest {

    @Test
    public void givenTwoMoneyInSameCurrencyShouldAdd(){
        Money money = Money.get(10, Currency.EUR);
        Money toAdd = Money.get(10, Currency.EUR);
        Money expected = Money.get(20, Currency.EUR);
        Money actual = money.plus(toAdd);
        assertEquals(expected, actual);
    }
}
