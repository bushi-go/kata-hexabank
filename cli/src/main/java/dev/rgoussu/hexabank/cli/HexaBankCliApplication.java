package dev.rgoussu.hexabank.cli;

import dev.rgoussu.hexabank.cli.adapters.persistence.FileAccountStore;
import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import dev.rgoussu.hexabank.cli.operations.BankOperation;
import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.ports.driving.AccountOperationsPort;
import java.io.IOException;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Hexa bank Command Line Interface Application.
 */
@SpringBootApplication
@EnableConfigurationProperties(AccountCsvStoreConfig.class)
public class HexaBankCliApplication implements CommandLineRunner {

  private static final int EXIT_CODE = 0;
  private static final Logger LOGGER = LoggerFactory.getLogger("Application");


  @Autowired
  private AccountOperationsPort<String> accountOperationsPort;

  @Autowired
  private AccountCsvStoreConfig config;

  @Autowired
  private FileAccountStore fileAccountStore;

  @Autowired
  private Map<Integer, BankOperation> operationMap;

  public static void main(String... args) {
    LOGGER.info("Starting hexabank CLI");
    SpringApplication.run(HexaBankCliApplication.class, args);
    LOGGER.info("Stopping hexabank CLI");
  }

  @Override
  public void run(String... args) throws IOException {
    init();
    boolean run = true;
    while (run) {
      printMenu();
      Scanner scanner = new Scanner(System.in);
      int operation = scanner.nextInt();
      if (operation == EXIT_CODE) {
        run = false;
      } else {
        executeOperation(operation, scanner);
      }
    }
    fileAccountStore.saveToFile(config.getAccountCsvBackingFile());
  }

  private void init() throws IOException {
    fileAccountStore.readFromFile(config.getAccountCsvBackingFile());
  }

  private void executeOperation(int operation, Scanner scanner) {
    clearScreen();
    printHeader();
    BankOperation actualOperation = operationMap.get(operation);
    if (actualOperation != null) {
      actualOperation.execute(accountOperationsPort, scanner);
    }

  }

  private void printMenu() {
    printHeader();
    printOperations();
    printFooter();
  }


  private void printHeader() {
    System.out.println(
        "|----------------------- Welcome to Hexa Bank CLI application -----------------------|");
    System.out.println(
        "|                                                                                    |");

  }

  private void printOperations() {
    System.out.println(
        "|  Please choose the operation you wish to perform                                   |");
    System.out.println(
        "|                                                                                    |");
    operationMap.values().stream().sorted(Comparator.comparing(BankOperation::getCode))
        .forEach(operation -> System.out.println("| " + operation.getCode() +
            ") " + operation.getName() + "                                                   |"));
    System.out.println("| " + EXIT_CODE +
        ") Exit application                                                    |");
  }

  private void printFooter() {
    System.out.println(
        "|                                                                                    |");
    System.out.println(
        "| © Hexa Bank Limited. All rights reserved                                           |");
    System.out.println(
        "|------------------------------------------------------------------------------------|");

  }

  private void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

}
