package dev.rgoussu.hexabank.rest.adapters.persistence.model;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.model.values.CurrencyConversion;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.time.Instant;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * ElasticSearch based operation history record.
 */
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Document(indexName = "history_operations")
public class AccountOperationSummaryRecord {

  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String accountId;

  @Field(type = FieldType.Date)
  private Instant operationDate;

  @Field(type = FieldType.Keyword)
  private OperationType operationType;

  @Field(type = FieldType.Keyword)
  private OperationStatus operationStatus;

  @Field(type = FieldType.Object)
  private Money operationAmount;

  @Field(type = FieldType.Object)
  private CurrencyConversion conversion;

  @Field(type = FieldType.Object)
  private Money balanceAfterOperation;

  public Optional<CurrencyConversion> getConversion() {
    return Optional.ofNullable(conversion);
  }

  /**
   * Mapping method to convert this record back to a business representation for this record.
   *
   * @return the mapped business object
   */
  public AccountOperationSummary toAccountOperationSummary() {
    return AccountOperationSummary.builder()
        .operationDate(operationDate)
        .operationAmount(operationAmount)
        .operationType(operationType)
        .operationStatus(operationStatus)
        .conversion(conversion)
        .balanceAfterOperation(balanceAfterOperation)
        .build();
  }
}
