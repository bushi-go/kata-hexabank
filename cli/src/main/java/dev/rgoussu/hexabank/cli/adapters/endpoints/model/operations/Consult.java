package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.AccountValidator;
import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.history.model.entities.AccountSummary;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.springframework.stereotype.Component;

/**
 * Consult operation bean implementing the cli procedure to consult one's account summary.
 */
@Component
public class Consult implements BankOperation {

  private final int dateColumnLength;
  private final int typeColumnLength;
  private final int statusColumnLength;
  private final int remainingColumnLength;

  private final DateTimeFormatter dateFormat;
  private final AccountValidator validator;
  private final CliDisplay display;
  private final AccountHistoryPort service;

  Consult(
      DateTimeFormatter dateFormat,
      AccountHistoryPort accountHistoryPort,
      AccountValidator validator,
      CliDisplay display) {
    this.dateFormat = dateFormat;
    this.display = display;
    this.service = accountHistoryPort;
    this.validator = validator;
    dateColumnLength = dateFormat.format(Instant.now()).length();
    typeColumnLength = OperationType.WITHDRAW.name().length();
    statusColumnLength = OperationStatus.SUCCESS.name().length();
    remainingColumnLength =
        display.getLineLength() - dateColumnLength - typeColumnLength - statusColumnLength - 10;
  }

  @Override
  public void execute(Scanner scanner) {
    String account = proceedToAccountNumber(scanner, display, validator);
    AccountSummary history = service.getAccountHistory(account);
    display.printCenteredAsSeparator("Account n° " + account);
    display.printLeft("Current balance : " + history.getBalance());
    display.printCenteredAsSeparator("");
    display.printLeft(getHeaderLine());
    history.getOperations().forEach(this::printLine);
    display.printLeftAsSeparator("");
    display.printLeft("Enter Y when you wish to return to the menu");

    String confirm = "N";
    while (!"Y".equalsIgnoreCase(confirm)) {
      confirm = scanner.nextLine();
    }
  }

  private void printLine(AccountOperationSummary operation) {
    String line =
        display.paddCenter(dateFormat.format(operation.getOperationDate()), dateColumnLength) + "|"
            + display.paddCenter(operation.getOperationType().name(), typeColumnLength) + "|"
            + display.paddCenter(operation.getOperationStatus().name(), statusColumnLength) + " |"
            + display.paddCenter(operation.getOperationAmount().toString(), remainingColumnLength);
    display.printLeft(line);
    display.printLeft("Balance after operation:" + operation.getBalanceAfterOperation());
  }

  private String getHeaderLine() {
    return display.paddCenter("Date", dateColumnLength) + "|"
        + display.paddCenter("Type ", typeColumnLength) + "|"
        + display.paddCenter("Status", statusColumnLength) + "|"
        + display.paddCenter("Amount", remainingColumnLength);
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
