package dev.rgoussu.hexabank.business.model.dto;

import dev.rgoussu.hexabank.business.model.types.DepositStatus;
import dev.rgoussu.hexabank.business.model.values.Money;
import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class DepositResult {

    private DepositStatus status;
    private String accountId;
    private Money balance;

    public static DepositResult success(String accountId, Money money) {
        return builder().accountId(accountId).balance(money).status(DepositStatus.SUCCESS).build();
    }
}
