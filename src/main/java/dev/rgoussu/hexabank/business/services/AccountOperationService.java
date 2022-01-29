package dev.rgoussu.hexabank.business.services;

import dev.rgoussu.hexabank.business.model.dto.DepositResult;
import dev.rgoussu.hexabank.business.model.values.Money;

public interface AccountOperationService {
    DepositResult processDeposit(String accountId, Money deposit);
}
