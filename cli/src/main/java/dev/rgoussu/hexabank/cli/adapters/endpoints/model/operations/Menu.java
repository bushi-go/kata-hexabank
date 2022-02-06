package dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import org.springframework.stereotype.Component;


/**
 * Root "operation" encapsulating the cli logic
 * to present users with the operations it can make.
 */
@Component
public class Menu {

  private static final int EXIT_CODE = 0;
  private final CliDisplay display;
  private final Map<Integer, BankOperation> operationMap;
  private boolean shouldExit = false;

  public Menu(CliDisplay display, Map<Integer, BankOperation> operationMap) {
    this.operationMap = operationMap;
    this.display = display;
  }

  /**
   * Display the menu and wait for the user to choose its operation.
   *
   * @param scanner the scanner to be used to get user input.
   */
  public void display(Scanner scanner) {
    display.clearScreen();
    printHeader();
    printOperations();
    int operation = scanner.nextInt();
    scanner.nextLine();
    if (operation == EXIT_CODE) {
      this.shouldExit = true;
    } else {
      executeOperation(operation, scanner);
    }
  }

  private void printHeader() {
    display.printCenteredAsSeparator("Welcome to Hexa Bank CLI application");
    display.printLeft("");
    display.printLeft("© Hexa Bank Limited. All rights reserved");
    display.printLeftAsSeparator("");

  }

  private void printOperations() {
    display.print("Please choose the operation you wish to perform");
    display.print("");
    operationMap.values().stream().sorted(Comparator.comparing(BankOperation::getCode))
        .forEach(operation -> display.print(operation.getCode() + ") " + operation.getName()));
    display.print(EXIT_CODE + ") Exit application");
  }


  private void executeOperation(int operation, Scanner scanner) {
    BankOperation actualOperation = operationMap.get(operation);
    if (actualOperation != null) {
      actualOperation.execute(scanner);
    }

  }

  public boolean shouldExit() {
    return shouldExit;
  }
}
