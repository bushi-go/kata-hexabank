package dev.rgoussu.hexabank.business.model.values;

import dev.rgoussu.hexabank.business.model.types.Currency;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class Money {
    private BigDecimal amount;
    private Currency currency;

    public static Money get(Number amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount.doubleValue()), currency);
    }

    public Money plus(Money other) {
        if (!other.currency.equals(currency)) {
            throw new IllegalArgumentException("Can not add money in different currencies : "
                    + this.currency
                    + " to "
                    + other.currency);
        }
        return Money.get(other.amount.add(this.amount), currency);
    }

    @Override
    public String toString() {
        return amount.toString() + " " + currency.getSymbol();
    }

    public Money convert(Currency otherCurrency, double exchangeRate) {
        if (exchangeRate <= 0) {
            throw new IllegalArgumentException("Invalid exchangeRate :" + exchangeRate + " when trying to convert from " + this.currency + " to " + otherCurrency);
        }
        return Money.get(amount.multiply(BigDecimal.valueOf(exchangeRate)), otherCurrency);
    }
}
