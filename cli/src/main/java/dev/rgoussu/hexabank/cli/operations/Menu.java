package dev.rgoussu.hexabank.cli.operations;

import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;


@Component
public class Menu {

    private static final int EXIT_CODE = 0;
    private boolean shouldExit = false;
    private final CliDisplay display;
    private final Map<Integer, BankOperation> operationMap;

    public Menu(CliDisplay display, Map<Integer, BankOperation> operationMap) {
        this.operationMap = operationMap;
        this.display = display;
    }

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

    public void executeOperation(int operation, Scanner scanner) {
        BankOperation actualOperation = operationMap.get(operation);
        if (actualOperation != null) {
            actualOperation.execute(scanner);
        }

    }

    public boolean shouldExit() {
        return shouldExit;
    }
}
