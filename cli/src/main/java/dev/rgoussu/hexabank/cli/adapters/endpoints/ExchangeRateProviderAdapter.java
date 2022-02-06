package dev.rgoussu.hexabank.cli.adapters.endpoints;

import dev.rgoussu.hexabank.core.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Static, offline exchange rate provider, providing rates from a hard coded map.
 * Can provide rates for EUR -> USD, EUR -> GBP, and vice versa.
 */
@Component
public class ExchangeRateProviderAdapter implements ExchangeRateProviderPort {

  private Map<Pair<Currency, Currency>, Double> exchangeTable = Map.of(
      new Pair<>(Currency.EUR, Currency.USD), 1.12,
      new Pair<>(Currency.USD, Currency.EUR), 0.89,
      new Pair<>(Currency.EUR, Currency.GBP), 0.84,
      new Pair<>(Currency.GBP, Currency.EUR), 1.20
  );

  @Override
  public double getExchangeRateForCurrencies(Currency from, Currency to)
      throws UnavailableExchangeRateException {
    Pair<Currency, Currency> exchangePair = Pair.of(from, to);
    if (exchangeTable.containsKey(exchangePair)) {
      return exchangeTable.get(exchangePair);
    }
    throw new UnavailableExchangeRateException(
        "Conversion from " + from + " to " + to + " is not supported");
  }

  private record Pair<A, B>(A first, B second) {
    public static <A, B> Pair<A, B> of(A first, B second) {
      return new Pair<>(first, second);
    }
  }
}
