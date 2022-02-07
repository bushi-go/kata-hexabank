package dev.rgoussu.hexabank.core.operations.services;

import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.values.Money;

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
  OperationResult processDeposit(String accountId, Money deposit);

  /**
   * Process a withdrawal to the account with the given id.
   * Returns a dto encapsulating the result
   *
   * @param accountId the target account id
   * @param deposit   the withdrawal to be made
   * @return à dto encapsulating the result
   */
  OperationResult processWithdrawal(String accountId, Money deposit);

  /**
   * Check an account number for existence.
   *
   * @param account account number
   * @return true if valid account, false otherwise
   */
  boolean checkAccount(String account);
}
