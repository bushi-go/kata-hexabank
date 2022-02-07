package dev.rgoussu.hexabank.core.operations.ports.driving;

import dev.rgoussu.hexabank.core.operations.model.values.Money;

/**
 * Driving port for account operations.
 *
 * @param <T> type of answer returned by the implementing adapter
 *            e.g Void for a cli implementation, an Http response for a rest api, etc.
 */
public interface AccountOperationsPort<T> {
  /**
   * Process a deposit to the given account.
   *
   * @param accountId target account id
   * @param deposit   deposit to be made
   * @return to be defined by the implementing adapter
   */
  T deposit(String accountId, Money deposit);

  /**
   * Process a withdraw to the given account
   *
   * @param accountId target account id
   * @param withdraw  withdrawal to be made
   * @return to be defined by the implementing adapter
   */
  T witdraw(String accountId, Money withdraw);
}
