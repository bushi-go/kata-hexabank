package dev.rgoussu.hexabank.business.model.dto;

import dev.rgoussu.hexabank.business.model.types.Currency;
import dev.rgoussu.hexabank.business.model.types.DepositError;
import dev.rgoussu.hexabank.business.model.types.DepositStatus;
import dev.rgoussu.hexabank.business.model.values.Money;
import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class DepositResult {

    private DepositStatus status;
    private DepositError error;
    private String accountId;
    private Money balance;

    public static DepositResult success(String accountId, Money money) {
        return builder().accountId(accountId).balance(money).status(DepositStatus.SUCCESS).build();
    }

    public static DepositResult noSuchAccount(String accountId) {
        return builder().accountId(accountId).status(DepositStatus.FAILURE).error(DepositError.UNKNOWN_ACCOUNT).build();
    }

    public static DepositResult unavailableExchangeRate(String accountId) {
        return builder().accountId(accountId).status(DepositStatus.FAILURE).error(DepositError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY).build();
    }
}
