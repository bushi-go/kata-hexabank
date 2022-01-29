package dev.rgoussu.hexabank.business.services;

import dev.rgoussu.hexabank.business.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.business.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.business.model.dto.DepositResult;
import dev.rgoussu.hexabank.business.model.entities.Account;
import dev.rgoussu.hexabank.business.model.values.Money;
import dev.rgoussu.hexabank.business.ports.driven.ExchangeRateProviderPort;
import dev.rgoussu.hexabank.business.ports.driven.AccountPersistencePort;

/**
 * Deposit and withdrawal operation manager
 */
public class AccountOperationManager implements AccountOperationService{

    private final AccountPersistencePort accountPersistencePort;
    private final ExchangeRateProviderPort exchangeRateProviderPort;
    public AccountOperationManager(AccountPersistencePort accountPersistencePort, ExchangeRateProviderPort exchangeRateProviderPort) {
        this.accountPersistencePort = accountPersistencePort;
        this.exchangeRateProviderPort = exchangeRateProviderPort;
    }

    @Override
    public DepositResult processDeposit(String accountId, Money deposit) {
        try {
            Account account = accountPersistencePort
                    .findByAccountId(accountId);
            Money finalDeposit;
            if(account.getOperatingCurrency().equals(deposit.getCurrency())){
               finalDeposit = deposit;
            }else{
                double exchangeRate = exchangeRateProviderPort.getExchangeRateForCurrencies(deposit.getCurrency(), account.getOperatingCurrency());
                finalDeposit = deposit.convert(account.getOperatingCurrency(), exchangeRate);
            }
            account = account.deposit(finalDeposit);
            accountPersistencePort.save(account);
            return DepositResult.success(accountId, account.getBalance());
        }catch(NoSuchAccountException e){
            return DepositResult.noSuchAccount(accountId);
        }catch(UnavailableExchangeRateException e){
            return DepositResult.unavailableExchangeRate(accountId);
        }
    }
}
