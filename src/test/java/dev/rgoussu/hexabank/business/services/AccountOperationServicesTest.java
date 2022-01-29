package dev.rgoussu.hexabank.business.services;

import dev.rgoussu.hexabank.business.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.business.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.business.model.dto.DepositResult;
import dev.rgoussu.hexabank.business.model.entities.Account;
import dev.rgoussu.hexabank.business.model.types.Currency;
import dev.rgoussu.hexabank.business.model.values.Money;
import dev.rgoussu.hexabank.business.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.business.ports.driven.ExchangeRateProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountOperationServicesTest {

    private final AccountPersistencePort persistencePort = Mockito.mock(AccountPersistencePort.class);
    private final ExchangeRateProviderPort exchangeRateProviderPort = Mockito.mock(ExchangeRateProviderPort.class);
    private final AccountOperationService underTest = new AccountOperationManager(persistencePort, exchangeRateProviderPort);
    private final String accountId = UUID.randomUUID().toString();

    @BeforeEach
    public void reset() {
        Mockito.reset(persistencePort);
    }

    @Test
    public void givenExistingAccountShouldProceedToDepositSuccessfully() {
        Account targetAccount = Account.create(accountId, 30);
        Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
        Money deposit = Money.get(30, Currency.EUR);
        DepositResult expected = DepositResult.success(accountId, Money.get(60, Currency.EUR));
        DepositResult actual = underTest.processDeposit(accountId, deposit);
        assertEquals(expected, actual);
    }

    @Test
    public void givenNonExistingAccountShouldReturnFailure() {
        Mockito.when(persistencePort.findByAccountId(accountId)).thenThrow(new NoSuchAccountException("No account with id " + accountId));
        Money deposit = Money.get(30, Currency.EUR);
        DepositResult expected = DepositResult.noSuchAccount(accountId);
        DepositResult actual = underTest.processDeposit(accountId, deposit);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("generateDepositInDifferentCurrency")
    public void givenDepositInAnotherCurrencyShouldConvertAndProceedToDepositSuccessfully(double amount, double exchangeRate, Currency currency) {
        Account targetAccount = Account.create(accountId, 5000);
        Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
        Mockito.when(exchangeRateProviderPort.getExchangeRateForCurrencies(currency, targetAccount.getOperatingCurrency())).thenReturn(exchangeRate);
        Money deposit = Money.get(amount, currency);
        Money expectedValue = deposit.convert(targetAccount.getOperatingCurrency(), exchangeRate).plus(targetAccount.getBalance());
        DepositResult expected = DepositResult.success(accountId, expectedValue);
        DepositResult actual = underTest.processDeposit(accountId, deposit);
        assertEquals(expected, actual);
    }

    @Test
    public void givenDepositAndExchangeRateUnavailableShouldReturnFailure(){
        Account targetAccount = Account.create(accountId);
        Money deposit = Money.get(5000, Currency.JPY);
        Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
        Mockito.when(exchangeRateProviderPort.getExchangeRateForCurrencies(deposit.getCurrency(), targetAccount.getOperatingCurrency())).thenThrow(new UnavailableExchangeRateException("Could not get exchange rate", deposit.getCurrency(), targetAccount.getOperatingCurrency()));
        DepositResult expected = DepositResult.unavailableExchangeRate(accountId);
        DepositResult actual = underTest.processDeposit(accountId, deposit);
        assertEquals(expected, actual);
    }

    public static Stream<Arguments> generateDepositInDifferentCurrency() {
        return Stream.of(Arguments.of(100.0, 0.88, Currency.USD), Arguments.of(50.0, 1.20, Currency.GBP), Arguments.of(50000,0.0078, Currency.JPY));
    }
}
