package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import org.springframework.stereotype.Component;

/**
 * Class encapsulating the cli logic for the deposit operations.
 */
@Component
public class Deposit extends BankOperation {
  private static final String CONFIRM_CODE = "Y";

  public Deposit(AccountOperationsPort<String> service, AccountValidator validator,
                 CliDisplay display) {
    super(service, validator, display, 1, "Deposit");
  }

  @Override
  public void execute(Scanner scanner) {
    String accountNumber = proceedToAccountNumber(scanner);
    double amount = proceedToAmount(scanner);
    display.print("You declared you wish to deposit " + amount + "€");
    Currency currency = proceedToCurrency(scanner);
    String confirm = proceedToConfirm(amount, currency, accountNumber, scanner);
    if (Objects.equals(confirm, CONFIRM_CODE)) {

      display.printLeftAsSeparator("");
      display.print(service.deposit(accountNumber, Money.get(amount, currency)));

      display.printLeft("Press enter to go back to the menu");
      scanner.nextLine();
    }

  }


  private double proceedToAmount(Scanner scanner) {
    boolean doing = true;
    double amount = 0;
    while (doing) {
      display.print("Please enter the amount you wish to deposit :");
      try {
        amount = scanner.nextDouble();
        scanner.nextLine();
      } catch (InputMismatchException e) {
        display.print("Invalid amount, please try again");
      }
      if (amount < 0) {
        display.print("Invalid amount, please try again");
      } else {
        doing = false;
      }
    }
    return amount;
  }

  private Currency proceedToCurrency(Scanner scanner) {
    display.print(
        "Do you wish to change the currency ? Is so please enter its code then hit enter key");
    display.print("otherwise enter N key to proceed");
    boolean doing = true;
    Currency currency = Currency.EUR;
    while (doing) {
      if (scanner.hasNextLine()) {
        String currencyCode = scanner.nextLine();
        if (!currencyCode.isBlank() && !currencyCode.equals("N")) {
          try {
            currency = Currency.valueOf(currencyCode);
            doing = false;
          } catch (IllegalArgumentException e) {
            display.print("Invalid currency code, please try again");
          }
        } else if (currencyCode.equals("N")) {
          doing = false;
        }
      }
    }
    return currency;
  }

  //TODO extract confirm values to constants
  private String proceedToConfirm(double amount, Currency currency, String accountNumber,
                                  Scanner scanner) {
    boolean doing = true;
    String confirm = "";
    while (doing) {
      display.print(
          "Do you confirm the deposit of " + amount + " " + currency.getSymbol() + " on account "
              + accountNumber + " ?");
      display.print("enter Y to confirm, enter N to cancel and go back to menu");
      confirm = scanner.nextLine();
      if (!confirm.isBlank()
          && (confirm.equalsIgnoreCase("Y")
          || confirm.equalsIgnoreCase("N"))) {
        doing = false;
      }
    }
    return confirm;
  }
}
