package dev.rgoussu.hexabank.rest.adapters.persistence;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driven.AccountHistoryPersistencePort;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.rest.adapters.persistence.elastic.ElasticSearchAccountHistoryRepository;
import dev.rgoussu.hexabank.rest.adapters.persistence.model.AccountOperationSummaryRecord;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Persistence adapter for account history.
 * Wraps an elastic search index for the account operations
 */
@Component
@Slf4j
public class AccountHistoryPersistenceAdapter implements AccountHistoryPersistencePort {

  private final ElasticSearchAccountHistoryRepository accountHistoryRepository;

  AccountHistoryPersistenceAdapter(ElasticSearchAccountHistoryRepository accountHistoryRepository) {
    this.accountHistoryRepository = accountHistoryRepository;
  }

  @Override
  public void recordOperationSummary(String accountId, AccountOperationSummary history) {
    AccountOperationSummaryRecord.AccountOperationSummaryRecordBuilder builder =
        AccountOperationSummaryRecord.builder()
            .accountId(accountId)
            .operationDate(history.getOperationDate())
            .operationAmount(history.getOperationAmount())
            .operationStatus(history.getOperationStatus())
            .balanceAfterOperation(history.getBalanceAfterOperation())
            .operationType(history.getOperationType());
    if (history.getConversion().isPresent()) {
      builder.conversion(history.getConversion().get());
    }
    accountHistoryRepository.save(builder.build());
  }

  @Override
  public SortedSet<AccountOperationSummary> findAccountHistory(String accountId)
      throws NoSuchAccountException {
    return accountHistoryRepository.findByAccountId(accountId, Pageable.unpaged()).stream()
        .map(AccountOperationSummaryRecord::toAccountOperationSummary).collect(
            Collectors.toCollection(TreeSet::new));
  }
}
