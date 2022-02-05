package dev.rgoussu.hexabank.rest.adapters.endpoints.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.rest.adapters.endpoints.model.serializer.MoneySerializer;
import lombok.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class DepositResultDto {
    @JsonSerialize(using = MoneySerializer.class)
    private Money deposit;
    private Money balance;
}
