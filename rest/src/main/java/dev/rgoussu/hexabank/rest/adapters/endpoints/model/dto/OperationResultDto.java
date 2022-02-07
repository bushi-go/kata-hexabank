package dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto;

import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Dto send back with the http response for a deposit.
 * Give the user the deposit made, and the new balance for the account.
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class OperationResultDto {
  private OperationType type;
  private Money balance;
  private Money amount;
}
