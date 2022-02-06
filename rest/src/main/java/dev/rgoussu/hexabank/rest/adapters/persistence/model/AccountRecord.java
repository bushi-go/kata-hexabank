package dev.rgoussu.hexabank.rest.adapters.persistence.model;

import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.model.types.Currency;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Mongo Db based representation of an account.
 */
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

  /**
   * Method factory to build a record from the relevant part of an account.
   *
   * @param account the account business object.
   * @return the account record
   */
  public static AccountRecord fromAccount(Account account) {
    return builder().accountId(account.getAccountId())
        .balance(account.getBalance().getAmount())
        .currency(account.getOperatingCurrency())
        .build();
  }

  public Account toAccount() {
    return Account.create(accountId, currency, balance.longValue());
  }

}
