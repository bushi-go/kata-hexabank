package dev.rgoussu.hexabank.core.services;

import dev.rgoussu.hexabank.core.model.dto.DepositResult;
import dev.rgoussu.hexabank.core.model.values.Money;

/**
 * Interface defining the account operation services.
 */
public interface AccountOperationService {
  /**
   * Process a deposit to the account with the given id.
   * Returns a dto encapsulating the result
   *
   * @param accountId the target account id
   * @param deposit   the deposit to be made
   * @return à dto encapsulating the result
   */
  DepositResult processDeposit(String accountId, Money deposit);
}
