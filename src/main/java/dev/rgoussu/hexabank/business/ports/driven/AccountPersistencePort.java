package dev.rgoussu.hexabank.business.ports.driven;

import dev.rgoussu.hexabank.business.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.business.model.entities.Account;

public interface AccountPersistencePort {
    Account findByAccountId(String accountId) throws NoSuchAccountException;
    Account save(Account account);
}
