package dev.rgoussu.hexabank.business.model.values;

import dev.rgoussu.hexabank.business.model.types.Currency;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Money {
    public BigDecimal amount;
    public Currency currency;

    public static Money get(Number amount, Currency currency){
        return new Money(BigDecimal.valueOf(amount.doubleValue()), currency);
    }

    public Money plus(Money other){
        if(!other.currency.equals(currency)){
            throw new IllegalArgumentException("Can not add money in different currencies : "
                    + this.currency
                    + " to "
                    + other.currency);
        }
        return Money.get(other.amount.add(this.amount), currency);
    }

    @Override
    public String toString() {
        return amount.toString() +" "+currency.getSymbol();
    }

}
