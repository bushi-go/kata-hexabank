package dev.rgoussu.hexabank.core.history.model.values;

import dev.rgoussu.hexabank.core.operations.model.types.Currency;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A snapshot of a conversion operation from a given currency
 * to another, at a specific rate.
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class CurrencyConversion {
  private Currency from;
  private Currency to;
  private double exchangeRate;
}
