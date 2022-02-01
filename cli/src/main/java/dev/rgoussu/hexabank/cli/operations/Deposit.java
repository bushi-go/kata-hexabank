package dev.rgoussu.hexabank.cli.operations;

import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class Deposit extends BankOperation{
  private static final String CONFIRM_CODE = "Y";

  public Deposit(){
    super(1, "Deposit");
  }

  @Override
  public void execute(AccountOperationsPort<String> service, Scanner scanner) {
    String accountNumber = proceedToAccountNumber(scanner);
    double amount = proceedToAmount(scanner);
    System.out.println("| You declared you wish to deposit " + amount +
        "€                                       |");
    Currency currency = proceedToCurrency(scanner);
    String confirm = proceedToConfirm(amount, currency, accountNumber, scanner);
    if (Objects.equals(confirm, CONFIRM_CODE)) {
      System.out.println(service.deposit(accountNumber, Money.get(amount, currency)));
    }
  }


  private double proceedToAmount(Scanner scanner) {
    boolean doing = true;
    double amount = 0;
    while (doing) {
      System.out.println(
          "| Please enter the amount you wish to deposit :                                      |");
      try {
        amount = scanner.nextDouble();
      } catch (InputMismatchException e) {
        System.out.println(
            "| Invalid amount, please try again                                  |");
      }
      if (amount < 0) {
        System.out.println(
            "| Invalid amount, please try again                                  |");
      } else {
        doing = false;
      }
    }
    return amount;
  }

  private Currency proceedToCurrency(Scanner scanner) {
    System.out.println(
        "| Do you wish to change the currency ? Is so please enter its code then hit enter key  |");
    System.out.println(
        "| otherwise hit enter key to proceed                                                   |");
    boolean doing = true;
    Currency currency = Currency.EUR;
    while (doing) {
      String currencyCode = scanner.next();
      if (!currencyCode.isBlank()) {
        try {
          currency = Currency.valueOf(currencyCode);
          doing = false;
        } catch (IllegalArgumentException e) {
          System.out.println(
              "| Invalid currency code, please try again                                            |");
        }
      } else {
        doing = false;
      }
    }
    return currency;
  }
  private String proceedToConfirm(double amount, Currency currency, String accountNumber,
                                  Scanner scanner) {
    boolean doing = true;
    String confirm = "";
    while(doing) {
      System.out.println(
          "| Do you confirm the deposit of " + amount + " " + currency + " on account " +
              accountNumber + " ? |");
      System.out.println("| enter Y to confirm, press any key to cancel                      |");
      confirm =  scanner.next();
      if(!confirm.isBlank()){
        doing = false;
      }
    }
    return confirm;
  }
}
