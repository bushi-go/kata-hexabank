package dev.rgoussu.hexabank.core.services;

import dev.rgoussu.hexabank.core.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.model.dto.DepositResult;
import dev.rgoussu.hexabank.core.model.entities.Account;
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
  public DepositResult processDeposit(String accountId, Money deposit) {
    logger.info("[Account n° {}] processing deposit of {} to account", accountId, deposit);
    try {
      logger.debug("[Account n° {}] retrieving account", accountId);
      Account account = accountPersistencePort
          .findByAccountId(accountId);
      Money finalDeposit;
      if (account.getOperatingCurrency().equals(deposit.getCurrency())) {
        logger.debug(
            "[Account n° {}] deposit and account on the same currency, proceeding to deposit",
            accountId);
        finalDeposit = deposit;
      } else {
        logger.debug(
            "[Account n° {}] account and deposit not operating "
                + "under same currency, requesting exchange rate",
            accountId);
        double exchangeRate =
            exchangeRateProviderPort.getExchangeRateForCurrencies(deposit.getCurrency(),
                account.getOperatingCurrency());
        finalDeposit = deposit.convert(account.getOperatingCurrency(), exchangeRate);
      }
      account = account.deposit(finalDeposit);
      accountPersistencePort.save(account);
      return DepositResult.success(accountId, account.getBalance());
    } catch (NoSuchAccountException e) {
      return DepositResult.noSuchAccount(accountId);
    } catch (UnavailableExchangeRateException e) {
      return DepositResult.unavailableExchangeRate(accountId);
    }
  }
}
