package dev.rgoussu.hexabank.operations.adapters.persistence.mongo.records;

import dev.rgoussu.hexabank.core.history.model.values.CurrencyConversion;
import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@MongoEntity(collection="operations_history")
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class OperationHistoryCache extends PanacheMongoEntity {
  private ObjectId id;
  private String accountId;
  private Instant lastSyncDate;
  private Instant operationDate;
  private OperationType type;
  private OperationStatus operationStatus;
  private OperationError operationError;
  private Money amount;
  private Money balanceAfterOperation;
  private CurrencyConversion conversion;
}
