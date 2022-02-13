package dev.rgoussu.hexabank.operations.adapters.endpoints;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.operations.adapters.persistence.mongo.records.OperationHistoryCache;
import java.time.Instant;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountHistoryCache {

  public void cacheOperationSummary(String accountId, AccountOperationSummary operation,
                                    Instant now) {
    OperationHistoryCache.OperationHistoryCacheBuilder toCache = OperationHistoryCache.builder()
        .accountId(accountId)
        .operationDate(operation.getOperationDate())
        .operationError(operation.getOperationError())
        .amount(operation.getOperationAmount())
        .balanceAfterOperation(operation.getBalanceAfterOperation())
        .lastSyncDate(now)
        .type(operation.getOperationType());
    if(operation.getConversion().isPresent()){
      toCache.conversion(operation.getConversion().get());
    }
    OperationHistoryCache.persist(toCache.build());
  }

  public List<OperationHistoryCache> getFromCache(String accountId){
    return OperationHistoryCache.list("accountId", accountId);
  }
}
