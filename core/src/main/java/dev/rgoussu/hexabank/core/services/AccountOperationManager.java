package dev.rgoussu.hexabank.core.services;

import dev.rgoussu.hexabank.core.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.model.types.OperationType;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
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

  public AccountOperationManager(AccountPersistencePort accountPersistencePort,
                                 ExchangeRateProviderPort exchangeRateProviderPort) {
    this.accountPersistencePort = accountPersistencePort;
    this.exchangeRateProviderPort = exchangeRateProviderPort;
  }

  @Override
  public OperationResult processDeposit(String accountId, Money deposit) {
    return processOperation(accountId, deposit, OperationType.DEPOSIT);
  }

  @Override
  public OperationResult processWithdrawal(String accountId, Money withdraw) {
    return processOperation(accountId, withdraw, OperationType.WITHDRAWAL);
  }

  private OperationResult processOperation(String accountId, Money amount, OperationType type){
    logger.info("[Account n° {}] processing {} of {} to account", accountId, type, amount);
    try {
      logger.debug("[Account n° {}] retrieving account", accountId);
      Account account = accountPersistencePort
          .findByAccountId(accountId);
      Money finalDeposit;
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
      }
      account = type.getOperation().apply(account,finalDeposit);
      accountPersistencePort.save(account);
      return OperationResult.success(accountId, account.getBalance());
    } catch (NoSuchAccountException e) {
      return OperationResult.noSuchAccount(accountId);
    } catch (UnavailableExchangeRateException e) {
      return OperationResult.unavailableExchangeRate(accountId);
    }
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
