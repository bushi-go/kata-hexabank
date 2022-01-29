package dev.rgoussu.hexabank.business.model.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    @Test
    public void givenAccountAndDepositShouldAddToBalance(){
        String accountId = UUID.randomUUID().toString();
        Account underTest = Account.builder().id(accountId).build();
        Account expected = Account.builder().id(accountId).balance(10).build();
        Account actual = underTest.deposit(10);
        assertEquals(expected, actual);
    }

}
