package dev.rgoussu.hexabank.business.ports.driven;

import dev.rgoussu.hexabank.business.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.business.model.types.Currency;

public interface ExchangeRateProviderPort {
    double getExchangeRateForCurrencies(Currency from, Currency to) throws UnavailableExchangeRateException;
}
