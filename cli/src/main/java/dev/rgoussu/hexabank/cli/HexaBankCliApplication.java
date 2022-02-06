package dev.rgoussu.hexabank.cli;

import dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations.Menu;
import dev.rgoussu.hexabank.cli.adapters.persistence.FileAccountStore;
import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Menu menu;

  private final AccountCsvStoreConfig config;

  private final FileAccountStore fileAccountStore;

  /**
   * Public constructor.
   *
   * @param menu             the root operation, displaying the menu
   * @param config           the csv account store config
   * @param fileAccountStore the file account store, to allow to read and write to it.
   */
  public HexaBankCliApplication(Menu menu, AccountCsvStoreConfig config,
                                FileAccountStore fileAccountStore) {
    this.menu = menu;
    this.config = config;
    this.fileAccountStore = fileAccountStore;
  }

  /**
   * Main function.
   *
   * @param args cli args
   */
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
    LOGGER.debug("Account file : {}, csv delimiter: {}",
        config.getAccountCsvBackingFile().getPath(), config.getDelimiter());
    fileAccountStore.readFromFile(config.getAccountCsvBackingFile());
  }

}
