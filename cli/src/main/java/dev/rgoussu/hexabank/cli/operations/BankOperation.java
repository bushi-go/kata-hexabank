package dev.rgoussu.hexabank.cli.operations;

import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import java.util.Scanner;

public abstract class BankOperation {
  private final int code;
  private final String name;
  protected BankOperation(int code, String name){
    this.code = code;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }
  public abstract void execute(AccountOperationsPort<String> service, Scanner scanner);

  protected String proceedToAccountNumber(Scanner scanner) {
    boolean doing = true;
    String account ="";
    while(doing) {
      System.out.println("| Please enter your account number                       |");
      account = scanner.next();
      if(!account.isBlank()){
        doing = false;
      }
    }
    return account;
  }
}
