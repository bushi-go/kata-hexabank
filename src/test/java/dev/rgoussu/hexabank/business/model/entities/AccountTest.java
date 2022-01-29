package dev.rgoussu.hexabank.business.model.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    @ParameterizedTest
    @ValueSource(ints={0,10,15,20,30,45,50,100})
    public void givenAccountAndDepositShouldAddToBalance(int amount){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.builder().accountId(accountId).build();
        Account expected = Account.builder().accountId(accountId).balance(amount).build();
        Account actual = underTest.deposit(amount);
        assertEquals(expected, actual);
    }

}
