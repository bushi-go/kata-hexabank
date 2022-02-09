package dev.rgoussu.hexabank.core.operations.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import dev.rgoussu.hexabank.core.AccountServicesFactory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.operations.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.entities.Account;
import dev.rgoussu.hexabank.core.operations.model.types.Currency;
import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.operations.ports.driven.ExchangeRateProviderPort;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class AccountOperationServicesTest {

  private final AccountHistoryPort accountHistoryPort = Mockito.mock(AccountHistoryPort.class);
  private final AccountPersistencePort persistencePort = Mockito.mock(AccountPersistencePort.class);
  private final ExchangeRateProviderPort exchangeRateProviderPort =
      Mockito.mock(ExchangeRateProviderPort.class);
  private final AccountOperationService underTest =
      AccountServicesFactory.INSTANCE.getOperationService(accountHistoryPort,
          exchangeRateProviderPort, persistencePort);
  private final String accountId = UUID.randomUUID().toString();

  public static Stream<Arguments> generateDepositInDifferentCurrency() {
    return Stream.of(Arguments.of(100.0, 0.88, Currency.USD),
        Arguments.of(50.0, 1.20, Currency.GBP), Arguments.of(50000, 0.0078, Currency.JPY));
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(persistencePort);
  }

  @Test
  public void givenExistingAccountShouldProceedToDepositSuccessfully() {
    Account targetAccount = Account.create(accountId, 30);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Money deposit = Money.get(30, Currency.EUR);
    OperationResult expected = OperationResult.success(accountId, Money.get(60, Currency.EUR));
    OperationResult actual = underTest.processDeposit(accountId, deposit);
    assertEquals(expected, actual);
  }

  @Test
  public void givenNonExistingAccountShouldReturnFailure() {
    Mockito.when(persistencePort.findByAccountId(accountId))
        .thenThrow(new NoSuchAccountException("No account with id " + accountId));
    Money deposit = Money.get(30, Currency.EUR);
    OperationResult expected = OperationResult.noSuchAccount(accountId);
    OperationResult actual = underTest.processDeposit(accountId, deposit);
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @MethodSource("generateDepositInDifferentCurrency")
  public void givenDepositInAnotherCurrencyShouldConvertAndProceedToDepositSuccessfully(
      double amount, double exchangeRate, Currency currency) {
    Account targetAccount = Account.create(accountId, 5000);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Mockito.when(exchangeRateProviderPort.getExchangeRateForCurrencies(currency,
        targetAccount.getOperatingCurrency())).thenReturn(exchangeRate);
    Money deposit = Money.get(amount, currency);
    Money expectedValue = deposit.convert(targetAccount.getOperatingCurrency(), exchangeRate)
        .plus(targetAccount.getBalance());
    OperationResult expected = OperationResult.success(accountId, expectedValue);
    OperationResult actual = underTest.processDeposit(accountId, deposit);
    assertEquals(expected, actual);
  }

  @Test
  public void givenDepositAndExchangeRateUnavailableShouldReturnFailure() {
    Account targetAccount = Account.create(accountId);
    Money deposit = Money.get(5000, Currency.JPY);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Mockito.when(exchangeRateProviderPort.getExchangeRateForCurrencies(deposit.getCurrency(),
        targetAccount.getOperatingCurrency())).thenThrow(new UnavailableExchangeRateException(
        "Could not get exchange rate between " + deposit.getCurrency() + "and {}" +
            targetAccount.getOperatingCurrency()));
    OperationResult expected = OperationResult.unavailableExchangeRate(accountId);
    OperationResult actual = underTest.processDeposit(accountId, deposit);
    assertEquals(expected, actual);
  }

  @Test
  public void givenExistingAccountShouldProceedToWithdrawalSuccessfully() {
    Account targetAccount = Account.create(accountId, 30);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Money withdrawal = Money.get(30, Currency.EUR);
    OperationResult expected = OperationResult.success(accountId, Money.get(0, Currency.EUR));
    OperationResult actual = underTest.processWithdrawal(accountId, withdrawal);
    assertEquals(expected, actual);
  }

  @Test
  public void givenDepositOnAccountShouldRecordInHistory() {
    Account targetAccount = Account.create(accountId);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Money deposit = Money.get(30, Currency.EUR);
    underTest.processDeposit(accountId, deposit);
    ArgumentCaptor<AccountOperationSummary> argument =
        ArgumentCaptor.forClass(AccountOperationSummary.class);
    Mockito.verify(accountHistoryPort, Mockito.times(1))
        .registerOperationToHistory(Mockito.eq(accountId), argument.capture());
    assertAll(
        () -> assertEquals(deposit, argument.getValue().getOperationAmount()),
        () -> assertEquals(deposit, argument.getValue().getBalanceAfterOperation()),
        () -> assertEquals(OperationStatus.SUCCESS, argument.getValue().getOperationStatus()),
        () -> assertEquals(OperationError.NONE, argument.getValue().getOperationError()),
        () -> assertEquals(OperationType.DEPOSIT, argument.getValue().getOperationType()),
        () -> assertNotNull(argument.getValue().getOperationDate())
    );
  }

  @Test
  public void givenWithdrawalOnAccountShouldRecordInHistory() {
    Account targetAccount = Account.create(accountId);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Money withdraw = Money.get(30, Currency.EUR);
    underTest.processWithdrawal(accountId, withdraw);
    ArgumentCaptor<AccountOperationSummary> argument =
        ArgumentCaptor.forClass(AccountOperationSummary.class);
    Mockito.verify(accountHistoryPort, Mockito.times(1))
        .registerOperationToHistory(Mockito.eq(accountId), argument.capture());
    assertAll(
        () -> assertEquals(withdraw, argument.getValue().getOperationAmount()),
        () -> assertEquals(withdraw.negate(), argument.getValue().getBalanceAfterOperation()),
        () -> assertEquals(OperationStatus.SUCCESS, argument.getValue().getOperationStatus()),
        () -> assertEquals(OperationError.NONE, argument.getValue().getOperationError()),
        () -> assertEquals(OperationType.WITHDRAW, argument.getValue().getOperationType()),
        () -> assertNotNull(argument.getValue().getOperationDate())
    );
  }

  @Test
  public void givenFailedDepositOnAccountShouldRecordInHistory() {
    Account targetAccount = Account.create(accountId);
    Mockito.when(persistencePort.findByAccountId(accountId)).thenReturn(targetAccount);
    Mockito.when(exchangeRateProviderPort.getExchangeRateForCurrencies(Currency.USD, Currency.EUR))
        .thenThrow(new UnavailableExchangeRateException("Unavailable"));
    Money deposit = Money.get(30, Currency.USD);
    underTest.processDeposit(accountId, deposit);
    ArgumentCaptor<AccountOperationSummary> argument =
        ArgumentCaptor.forClass(AccountOperationSummary.class);
    Mockito.verify(accountHistoryPort, Mockito.times(1))
        .registerOperationToHistory(Mockito.eq(accountId), argument.capture());
    assertAll(
        () -> assertEquals(deposit, argument.getValue().getOperationAmount()),
        () -> assertEquals(targetAccount.getBalance(),
            argument.getValue().getBalanceAfterOperation()),
        () -> assertEquals(OperationStatus.FAILURE, argument.getValue().getOperationStatus()),
        () -> assertEquals(OperationError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY,
            argument.getValue().getOperationError()),
        () -> assertEquals(OperationType.DEPOSIT, argument.getValue().getOperationType()),
        () -> assertNotNull(argument.getValue().getOperationDate())
    );
  }

  @Test
  public void givenAccountNonExistingShouldNotRecord() {
    Account targetAccount = Account.create(accountId);
    Mockito.when(persistencePort.findByAccountId(accountId))
        .thenThrow(new NoSuchAccountException(accountId));
    Money deposit = Money.get(30, Currency.USD);
    underTest.processDeposit(accountId, deposit);
    ArgumentCaptor<AccountOperationSummary> argument =
        ArgumentCaptor.forClass(AccountOperationSummary.class);
    Mockito.verify(accountHistoryPort, Mockito.times(0))
        .registerOperationToHistory(Mockito.eq(accountId), any());

  }
}
