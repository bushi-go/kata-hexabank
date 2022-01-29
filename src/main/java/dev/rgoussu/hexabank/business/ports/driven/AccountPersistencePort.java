package dev.rgoussu.hexabank.business.ports.driven;

import dev.rgoussu.hexabank.business.model.entities.Account;

public interface AccountPersistencePort {
    Account findByAccountId(String accountId);
    Account save(Account account);
}
