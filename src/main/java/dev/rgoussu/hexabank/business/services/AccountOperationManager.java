package dev.rgoussu.hexabank.business.services;

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
            Account account = accountPersistencePort.findByAccountId(accountId).deposit(deposit);
            accountPersistencePort.save(account);
            return DepositResult.success(accountId, account.getBalance());
    }
}
