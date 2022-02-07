package dev.rgoussu.hexabank.core.operations.ports.driven;

import dev.rgoussu.hexabank.core.operations.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.operations.model.types.Currency;

/**
 * Driven port to retrieve the exchange rate between two currencies.
 */
public interface ExchangeRateProviderPort {
  /**
   * Retrieve the current exchange rate between two currencies.
   *
   * @param from original currency
   * @param to   target currency
   * @return the exchange rate between the currencies
   * @throws UnavailableExchangeRateException if the rate could not be retrieved
   */
  double getExchangeRateForCurrencies(Currency from, Currency to)
      throws UnavailableExchangeRateException;
}
