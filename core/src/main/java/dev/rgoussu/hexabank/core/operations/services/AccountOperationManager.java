package dev.rgoussu.hexabank.core.operations.services;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.model.values.CurrencyConversion;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.operations.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.entities.Account;
import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.operations.ports.driven.ExchangeRateProviderPort;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deposit and withdrawal operation manager.
 */
public class AccountOperationManager implements AccountOperationService {

  private static final String LOG_TAG = "AccountOperationService";
  private final Logger logger = LoggerFactory.getLogger(LOG_TAG);

  private final AccountPersistencePort accountPersistencePort;
  private final ExchangeRateProviderPort exchangeRateProviderPort;
  private final AccountHistoryPort accountHistoryPort;

  /**
   * public constructor.
   *
   * @param accountHistoryPort history driving port
   * @param exchangeRateProviderPort exchange rate provider
   * @param accountPersistencePort account persistence port
   */
  public AccountOperationManager(
      AccountHistoryPort accountHistoryPort,
      ExchangeRateProviderPort exchangeRateProviderPort,
      AccountPersistencePort accountPersistencePort) {
    this.accountHistoryPort = accountHistoryPort;
    this.exchangeRateProviderPort = exchangeRateProviderPort;
    this.accountPersistencePort = accountPersistencePort;
  }

  @Override
  public OperationResult processDeposit(String accountId, Money deposit) {
    return processOperation(accountId, deposit, OperationType.DEPOSIT);
  }

  @Override
  public OperationResult processWithdrawal(String accountId, Money withdraw) {
    if (withdraw.getAmount().signum() < 0) {
      withdraw = withdraw.negate();
    }
    return processOperation(accountId, withdraw, OperationType.WITHDRAW);
  }

  private OperationResult processOperation(String accountId, Money amount, OperationType type) {
    logger.info("[Account n° {}] processing {} of {} to account", accountId, type, amount);
    OperationResult result;
    AccountOperationSummary.AccountOperationSummaryBuilder operationHistoryBuilder =
        AccountOperationSummary.builder().operationType(type).operationAmount(amount);
    try {
      logger.debug("[Account n° {}] retrieving account", accountId);
      Account account = accountPersistencePort
          .findByAccountId(accountId);
      Money finalDeposit;
      operationHistoryBuilder.balanceAfterOperation(account.getBalance());
      if (account.getOperatingCurrency().equals(amount.getCurrency())) {
        logger.debug(
            "[Account n° {}] {}} and account on the same currency, proceeding to {}}",
            accountId, type, type);
        finalDeposit = amount;
      } else {
        logger.debug(
            "[Account n° {}] account and {} not operating "
                + "under same currency, requesting exchange rate",
            accountId, type);
        double exchangeRate =
            exchangeRateProviderPort.getExchangeRateForCurrencies(amount.getCurrency(),
                account.getOperatingCurrency());
        finalDeposit = amount.convert(account.getOperatingCurrency(), exchangeRate);
        operationHistoryBuilder.conversion(
            CurrencyConversion.builder().exchangeRate(exchangeRate).from(amount.getCurrency())
                .to(account.getOperatingCurrency()).build());
      }
      account = type.getOperation().apply(account, finalDeposit);
      accountPersistencePort.save(account);

      result = OperationResult.success(accountId, account.getBalance());
      accountHistoryPort.registerOperationToHistory(accountId,
          operationHistoryBuilder
              .operationDate(Instant.now())
              .operationStatus(OperationStatus.SUCCESS)
              .balanceAfterOperation(account.getBalance())
              .build());
    } catch (NoSuchAccountException e) {
      result = OperationResult.noSuchAccount(accountId);
    } catch (UnavailableExchangeRateException e) {
      result = OperationResult.unavailableExchangeRate(accountId);
      accountHistoryPort.registerOperationToHistory(accountId,
          operationHistoryBuilder
              .operationAmount(amount)
              .operationDate(Instant.now())
              .operationStatus(OperationStatus.FAILURE)
              .operationError(OperationError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY)
              .build());
    }
    return result;
  }

  @Override
  public boolean checkAccount(String account) {
    try {
      return accountPersistencePort.findByAccountId(account) != null;
    } catch (NoSuchAccountException e) {
      return false;
    }
  }
}
