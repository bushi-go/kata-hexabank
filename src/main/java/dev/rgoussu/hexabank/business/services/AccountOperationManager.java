package dev.rgoussu.hexabank.business.services;

import dev.rgoussu.hexabank.business.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.business.model.dto.DepositResult;
import dev.rgoussu.hexabank.business.model.entities.Account;
import dev.rgoussu.hexabank.business.model.values.Money;
import dev.rgoussu.hexabank.business.ports.driven.AccountPersistencePort;

/**
 * Deposit and withdrawal operation manager
 */
public class AccountOperationManager implements AccountOperationService{

    private final AccountPersistencePort accountPersistencePort;

    public AccountOperationManager(AccountPersistencePort accountPersistencePort) {
        this.accountPersistencePort = accountPersistencePort;
    }

    @Override
    public DepositResult processDeposit(String accountId, Money deposit) {
        try {
            Account account = accountPersistencePort.findByAccountId(accountId).deposit(deposit);
            accountPersistencePort.save(account);
            return DepositResult.success(accountId, account.getBalance());
        }catch(NoSuchAccountException e){
            return DepositResult.noSuchAccount(accountId);
        }
    }
}
