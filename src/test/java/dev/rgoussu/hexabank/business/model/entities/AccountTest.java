package dev.rgoussu.hexabank.business.model.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    @ParameterizedTest
    @CsvSource(value={
            "0,0",
            "0,10",
            "0,15",
            "0,20",
            "10,30"
    })
    public void givenAccountAndDepositShouldAddToBalance(int initialBalance, int amount){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.builder().accountId(accountId).balance(initialBalance).build();
        Account expected = Account.builder().accountId(accountId).balance(initialBalance+amount).build();
        Account actual = underTest.deposit(amount);
        assertEquals(expected, actual);
    }

}
