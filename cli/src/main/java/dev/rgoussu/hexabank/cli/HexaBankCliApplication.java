package dev.rgoussu.hexabank.cli;

import dev.rgoussu.hexabank.cli.adapters.endpoints.CliDisplay;
import dev.rgoussu.hexabank.cli.adapters.persistence.FileAccountStore;
import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import dev.rgoussu.hexabank.cli.operations.BankOperation;
import dev.rgoussu.hexabank.cli.operations.Menu;
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


  private static final Logger LOGGER = LoggerFactory.getLogger("Application");

  @Autowired
  private Menu menu;

  @Autowired
  private AccountCsvStoreConfig config;

  @Autowired
  private FileAccountStore fileAccountStore;

  public static void main(String... args) {
    LOGGER.info("Starting hexabank CLI");
    SpringApplication.run(HexaBankCliApplication.class, args);
    LOGGER.info("Stopping hexabank CLI");
  }

  @Override
  public void run(String... args) throws IOException {
    init();
    Scanner scanner = new Scanner(System.in);
    while (!menu.shouldExit()) {
      menu.display(scanner);
    }
    fileAccountStore.saveToFile(config.getAccountCsvBackingFile());
  }

  private void init() throws IOException {
    LOGGER.debug("Account file : {}, csv delimiter: {}", config.getAccountCsvBackingFile().getPath(), config.getDelimiter());
    fileAccountStore.readFromFile(config.getAccountCsvBackingFile());
  }

}
