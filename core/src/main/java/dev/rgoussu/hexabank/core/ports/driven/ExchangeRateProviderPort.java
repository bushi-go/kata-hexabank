package dev.rgoussu.hexabank.core.ports.driven;

import dev.rgoussu.hexabank.core.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.model.types.Currency;

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
