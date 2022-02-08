package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.history.model.entities.AccountOperationsHistory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class Consult implements BankOperation {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
  private final AccountValidator validator;
  private final CliDisplay display;
  private final AccountHistoryPort service;

  Consult(
      AccountHistoryPort accountHistoryPort,
      AccountValidator validator,
      CliDisplay display) {
    this.display = display;
    this.service = accountHistoryPort;
    this.validator = validator;
  }

  @Override
  public void execute(Scanner scanner) {
    String account = proceedToAccountNumber(scanner, display, validator);
    AccountOperationsHistory history = service.getAccountHistory(account);
    display.printCenteredAsSeparator(account);
    display.printLeft("Current balance : " + history.getBalance());
    display.printCenteredAsSeparator("");
    display.printLeft(getHeaderLine());
    history.getOperations().forEach(this::printLine);
    display.printLeftAsSeparator("");
    display.printLeft("Enter Y when you wish to return to the menu");

    String confirm ="N";
    while(!"Y".equalsIgnoreCase(confirm)){
      confirm = scanner.nextLine();
    }
  }

  private void printLine(AccountOperationSummary operation) {
    display.printLeft(DATE_FORMATTER.format(operation.getOperationDate())
        +"|"+operation.getOperationType().name()+"|"+operation.getOperationStatus().name()+"|"+operation.getOperationAmount());
    display.printLeft("Balance after operation:" + operation.getBalanceAfterOperation());
  }

  private String getHeaderLine() {
    return "Date"+" ".repeat(DATE_FORMATTER.format(Instant.now()).length() -1) + "|"
        +"Type" + " ".repeat(OperationType.WITHDRAW.name().length()-1)+"|" + "Status"+ " ".repeat(
        OperationStatus.SUCCESS.name().length()-1)+"|"+"Amount";
  }

  @Override
  public int getCode() {
    return 3;
  }

  @Override
  public String getName() {
    return "Consult account summary";
  }
}
