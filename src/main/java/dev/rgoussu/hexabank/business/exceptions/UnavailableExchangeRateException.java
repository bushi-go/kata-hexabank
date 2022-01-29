package dev.rgoussu.hexabank.business.exceptions;

import dev.rgoussu.hexabank.business.model.types.Currency;

public class UnavailableExchangeRateException extends RuntimeException {
    private final Currency originCurrency;
    private final Currency targetCurrency;

    public UnavailableExchangeRateException(String message, Currency originCurrency, Currency targetCurrency) {
        super(message);
        this.originCurrency = originCurrency;
        this.targetCurrency = targetCurrency;
    }

    public Object getOriginCurrency() {
        return originCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }
}
