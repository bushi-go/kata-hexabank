package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import java.util.Scanner;

/**
 * Interface for all bank operation usable in this cli implementation.
 */
public interface BankOperation {
  void execute(Scanner scanner);

  int getCode();

  String getName();

  /**
   * Method to get the account number from user input.
   *
   * @param scanner the scanner from with we will get the account number
   * @param display the cli display utility bean
   * @param validator the account validator bean
   * @return the account number
   */
  default String proceedToAccountNumber(Scanner scanner, CliDisplay display,
                                        AccountValidator validator) {
    boolean doing = true;
    String account = "";
    while (doing) {
      display.print("Please enter your account number");
      account = scanner.nextLine();
      if (!account.isBlank()) {

        if (validator.isValidAccount(account)) {
          doing = false;
        } else {
          display.printLeft(" Invalid account number");
        }
      }
    }
    return account;
  }
}
