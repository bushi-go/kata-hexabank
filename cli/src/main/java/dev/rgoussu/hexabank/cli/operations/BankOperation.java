package dev.rgoussu.hexabank.cli.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import java.util.Scanner;

public abstract class BankOperation {
  protected final CliDisplay display;
  protected final AccountOperationsPort<String> service;
  private final int code;
  private final String name;
  protected BankOperation(AccountOperationsPort<String> service,CliDisplay display,int code, String name){
    this.code = code;
    this.name = name;
    this.display = display;
    this.service = service;
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }
  public abstract void execute(Scanner scanner);

  protected String proceedToAccountNumber(Scanner scanner) {
    boolean doing = true;
    String account ="";
    while(doing) {


      display.print("Please enter your account number");
      account = scanner.nextLine();
      if(!account.isBlank()){

        if(service.isValidAccount(account)) {
          doing = false;
        }else{
          display.printLeft(" Invalid account number");
        }
      }
    }
    return account;
  }
}
