package dev.rgoussu.hexabank.rest.adapters.persistence.model;

import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.model.types.Currency;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document("account_record")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class AccountRecord {
    @Id
    private String accountId;
    private BigDecimal balance;
    private Currency currency;

    public Account toAccount() {
        return Account.create(accountId, currency, balance.longValue());
    }

    public static AccountRecord fromAccount(Account account) {
        return builder().accountId(account.getAccountId())
                .balance(account.getBalance().getAmount())
                .currency(account.getOperatingCurrency())
                .build();
    }

}
