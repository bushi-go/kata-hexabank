package dev.rgoussu.hexabank.cli.adapters.persistence.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the csv file based store of this cli application.
 */
@ConfigurationProperties(prefix = "store.csv")
@Validated
public class AccountCsvStoreConfig {

  private String accountCsvBackingFile;

  private String delimiter;

  public File getAccountCsvBackingFile() throws IOException {
    return Paths.get(accountCsvBackingFile).toFile();
  }

  public void setAccountCsvBackingFile(String accountCsvBackingFile) {
    this.accountCsvBackingFile = accountCsvBackingFile;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }
}
