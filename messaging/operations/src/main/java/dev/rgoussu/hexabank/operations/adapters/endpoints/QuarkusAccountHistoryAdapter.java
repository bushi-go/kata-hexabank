package dev.rgoussu.hexabank.operations.adapters.endpoints;

import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.operations.adapters.endpoints.model.AccountOperationSummaryDto;
import java.time.Instant;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class QuarkusAccountHistoryAdapter
    implements AccountHistoryPort<AccountOperationSummaryDto> {

  private final AccountHistoryCache accountHistoryCache;

  QuarkusAccountHistoryAdapter(AccountHistoryCache accountHistoryCache) {
    this.accountHistoryCache = accountHistoryCache;
  }

  @Outgoing("get-history")
  @Override
  public AccountSummary getAccountHistory(String accountId) throws NoSuchAccountException {
    return null;
  }

  @Outgoing("register-operation")
  @Override
  public AccountOperationSummaryDto registerOperationToHistory(String accountId,
                                                               AccountOperationSummary operation) {
    accountHistoryCache.cacheOperationSummary(accountId, operation, Instant.now());
    AccountOperationSummaryDto.AccountOperationSummaryDtoBuilder builder =
        AccountOperationSummaryDto.builder()
            .accountId(accountId)
            .operationDate(operation.getOperationDate())
            .balanceAfterOperation(operation.getBalanceAfterOperation())
            .amount(operation.getOperationAmount())
            .error(operation.getOperationError())
            .status(operation.getOperationStatus());
    if (operation.getConversion().isPresent()) {
      builder.currencyConversion(operation.getConversion().get());
    }
    return builder.build();
  }
}
