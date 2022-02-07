package dev.rgoussu.hexabank.core.history.services;

import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.history.model.entities.AccountHistory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;

public interface AccountHistoryService {

  AccountHistory getAccountHistory(String accountId) throws NoSuchAccountException;

  void recordOperationToHistory(String accountId,AccountOperationSummary operationSummary);
}
