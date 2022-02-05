package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import java.util.Scanner;

public abstract class BankOperation {
  protected final CliDisplay display;
  protected final AccountOperationsPort<String> service;
  protected final AccountValidator validator;
  private final int code;
  private final String name;
  protected BankOperation(AccountOperationsPort<String> service, AccountValidator validator,CliDisplay display,int code, String name){
    this.code = code;
    this.name = name;
    this.display = display;
    this.service = service;
    this.validator = validator;
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

        if(validator.isValidAccount(account)) {
          doing = false;
        }else{
          display.printLeft(" Invalid account number");
        }
      }
    }
    return account;
  }
}
