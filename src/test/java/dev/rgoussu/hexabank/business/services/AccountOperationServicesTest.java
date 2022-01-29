package dev.rgoussu.hexabank.business.services;

import dev.rgoussu.hexabank.business.model.dto.DepositResult;
import dev.rgoussu.hexabank.business.model.entities.Account;
import dev.rgoussu.hexabank.business.model.types.Currency;
import dev.rgoussu.hexabank.business.model.values.Money;
import dev.rgoussu.hexabank.business.ports.driven.AccountPersistencePort;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountOperationServicesTest {

    AccountPersistencePort persistencePort = Mockito.mock(AccountPersistencePort.class);
    AccountOperationService underTest = new AccountOperationManager(persistencePort);

    @BeforeEach
    public void reset(){
        Mockito.reset(persistencePort);
    }

    @Test
    public void givenExistingAccountShouldProceedToDepositSuccessfully(){
        String accountId = UUID.randomUUID().toString();
        Account targetAccount = Account.create(accountId, 30);
        Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
        Money deposit = Money.get(30, Currency.EUR);
        DepositResult expected = DepositResult.success(accountId, Money.get(60, Currency.EUR));
        DepositResult actual = underTest.processDeposit(accountId,deposit);
        assertEquals(expected, actual);
    }

}
