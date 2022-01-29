package dev.rgoussu.hexabank.business.ports.driving;

import dev.rgoussu.hexabank.business.model.values.Money;

public interface AccountOperationsPort<T> {
    T deposit(String accountId, Money deposit);
}
