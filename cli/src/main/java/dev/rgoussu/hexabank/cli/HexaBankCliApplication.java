package dev.rgoussu.hexabank.cli;

import dev.rgoussu.hexabank.cli.adapters.endpoints.model.operations.Menu;
import dev.rgoussu.hexabank.cli.adapters.persistence.FileAccountStore;
import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

/**
 * Hexa bank Command Line Interface Application.
 */
@SpringBootApplication
@EnableConfigurationProperties(AccountCsvStoreConfig.class)
@Slf4j
public class HexaBankCliApplication implements CommandLineRunner {

  private final Menu menu;

  private final AccountCsvStoreConfig config;

  private final List<FileAccountStore> fileAccountStore;
  private final ApplicationContext context;

  /**
   * Public constructor.
   *
   * @param context          the application context
   * @param menu             the root operation, displaying the menu
   * @param config           the csv account store config
   * @param fileAccountStore the file account store, to allow to read and write to it.
   */
  public HexaBankCliApplication(ApplicationContext context,
                                Menu menu, AccountCsvStoreConfig config,
                                List<FileAccountStore> fileAccountStore) {
    this.context = context;
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
    log.info("Starting hexabank CLI");
    SpringApplication.run(HexaBankCliApplication.class, args);
    log.info("Stopping hexabank CLI");
  }

  @Override
  public void run(String... args) throws IOException {
    init();
    Scanner scanner = new Scanner(System.in);
    try {
      while (!menu.shouldExit()) {
        menu.display(scanner);
      }
    } catch (Exception e) {
      log.error("AN error occured during execution", e);
    } finally {
      shutdownApplication();
    }
  }

  private void shutdownApplication() {
    log.info("[Application] saving data to file");
    try {
      for (FileAccountStore store : fileAccountStore) {
        store.saveToFile();
      }
    } catch (IOException e) {
      log.error("Could not save account data !");
      SpringApplication.exit(context, () -> 2);
    }
  }

  private void init() {
    try {
      for (FileAccountStore store : fileAccountStore) {
        store.readFromFile();
      }
      SpringApplication.exit(context, () -> 0);
    } catch (IOException e) {
      log.error("Could not initialize account data from store, shutting down");
      SpringApplication.exit(context, () -> 1);
    }
  }
}
