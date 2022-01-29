package dev.rgoussu.hexabank.business.model.entities;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class Account {
    private String accountId;
    private int balance;

    public Account deposit(int amount){
        return this.toBuilder().balance(10).build();
    }
}
