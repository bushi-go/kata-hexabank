package dev.rgoussu.hexabank.operations.adapters.endpoints;

import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import dev.rgoussu.hexabank.core.operations.ports.driving.AccountOperationsPort;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;
import dev.rgoussu.hexabank.operations.adapters.endpoints.model.OperationResultDto;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class QarkusAccountOperationAdapter implements AccountOperationsPort<OperationResultDto> {

  private final AccountOperationService accountOperationService;

  QarkusAccountOperationAdapter(AccountOperationService service) {
    this.accountOperationService = service;
  }

  @POST
  @Path("/{accountId}/deposit")
  @Override
  public OperationResultDto deposit(@PathParam("accountId") String accountId, Money deposit) {
    OperationResultDto.OperationResultDtoBuilder builder =
        OperationResultDto.builder().accountId(accountId);
    log.info("[Acccount n° {}] processing deposit of {}", accountId, deposit);
    try {
      OperationResult result = accountOperationService.processDeposit(accountId, deposit);
      return builder.operationStatus(result.getStatus())
          .error(result.getError())
          .balance(result.getBalance())
          .accountId(accountId)
          .build();
    } catch (Exception e) {
      log.error("[Account n° {}] could not process deposit", accountId, e);
      builder.error(OperationError.NONE).operationStatus(OperationStatus.FAILURE);
    }
    return builder.build();
  }

  @POST
  @Path("/{accountId}/withdraw")
  @Override
  public OperationResultDto withdraw(@PathParam("accountId") String accountId, Money withdraw) {
    OperationResultDto.OperationResultDtoBuilder builder =
        OperationResultDto.builder().accountId(accountId);
    log.info("[Acccount n° {}] processing withdraw of {}", accountId, withdraw);
    try {
      OperationResult result = accountOperationService.processWithdrawal(accountId, withdraw);
      return builder.operationStatus(result.getStatus())
          .error(result.getError())
          .balance(result.getBalance())
          .accountId(accountId)
          .build();
    } catch (Exception e) {
      log.error("[Account n° {}] could not process withdraw", accountId, e);
      builder.error(OperationError.NONE).operationStatus(OperationStatus.FAILURE);
    }
    return builder.build();
  }


}
