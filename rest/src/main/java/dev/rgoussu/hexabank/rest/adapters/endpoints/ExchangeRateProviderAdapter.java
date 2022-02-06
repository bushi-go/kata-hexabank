package dev.rgoussu.hexabank.rest.adapters.endpoints;

import dev.rgoussu.hexabank.core.exceptions.UnavailableExchangeRateException;
import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.ports.driven.ExchangeRateProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

@Component
@Slf4j
public class ExchangeRateProviderAdapter implements ExchangeRateProviderPort {

    @Override
    public double getExchangeRateForCurrencies(Currency from, Currency to) throws UnavailableExchangeRateException {
        try {
            log.info("Getting exchange rate between {} and {}", from, to);
            CurrencyConversion conversion = MonetaryConversions.getConversion(to.name());
            return conversion.getExchangeRate(Monetary.getDefaultAmountFactory().setCurrency(from.name()).setNumber(1).create())
                    .getFactor().doubleValue();
        } catch (MonetaryException e) {
            log.warn("Could not get exchange rate between {} and {}",from,to, e);
            throw new UnavailableExchangeRateException(e.getMessage(), e);
        }
    }
}
