package dev.rgoussu.hexabank.business.model.entities;

import dev.rgoussu.hexabank.business.model.values.Money;
import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class Account {
    private String accountId;
    private Money balance;


    public Account deposit(Money amount){

        return this.toBuilder().balance(this.balance.plus(amount)).build();
    }
}
