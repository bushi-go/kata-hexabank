package dev.rgoussu.hexabank.cli.adapters.persistence;

import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import dev.rgoussu.hexabank.cli.adapters.persistence.model.CsvAccountRecord;
import dev.rgoussu.hexabank.core.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.ports.driven.AccountPersistencePort;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * A simple map based, csv file backed store implementation for account data
 */
@Component
public class MapAccountPersistenceAdapter implements AccountPersistencePort, FileAccountStore {

  private static final Logger LOGGER = LoggerFactory.getLogger("AccountPersistenceService");

  private final AccountCsvStoreConfig config;
  private final Map<String, CsvAccountRecord> accountMap;

  MapAccountPersistenceAdapter(AccountCsvStoreConfig config) {

    this.config = config;
    // There is very little likelihood that this class will ever be used in a concurrent context, so a simple hashMap should be enough
    accountMap = new HashMap<>();
  }

  /**
   * Initialization method that read the account data from the configured file into the map.
   *
   * @throws IllegalStateException if for any reason data could not be read.
   */
  public void readFromFile(File accountFile) throws IllegalStateException {
    try (Stream<String> stream = Files.lines(accountFile.toPath())) {
      // We collect the stream here to check for the header line
      accountMap.putAll(mapCsvToAccountRecord(stream));
      LOGGER.info("Initialized {} accounts data from csv store", accountMap.size());
    } catch (FileNotFoundException e) {
      LOGGER.warn(
          "Could not find account csv backing file, assuming none exists yet and trying to create one");
      try {
        if (config.getAccountCsvBackingFile().createNewFile()) {
          LOGGER.info("New csv backing file created for account persistence at path {}",
              config.getAccountCsvBackingFile().getAbsolutePath());
        }
      } catch (IOException j) {
        LOGGER.error("Could not create account backing file for persistence, data loss danger");
        throw new IllegalStateException(
            "A problem occured during initialization of account data : ", e);
      }
    } catch (IOException e) {
      LOGGER.error(
          "Could not open,create or read account backing file, run will be aborted to prevent data loss");
      throw new IllegalStateException(
          "A problem occured during initialization of account data : ",
          e);
    }
  }

  /**
   * Save account data to file before the bean is destroyed (aka before the application exits).
   */
  public void saveToFile(File accountFile) {
    try (BufferedWriter writter = new BufferedWriter(
        new FileWriter(accountFile, false))) {
      String delimiter =
          Optional.ofNullable(config.getDelimiter()).orElse(CsvAccountRecord.getDefaultDelimiter());
      String headerLine = CsvAccountRecord.getHeaderLine(delimiter);
      writter.write(headerLine);
      writter.newLine();
      accountMap.values().stream().map(record -> record.toCsv(delimiter, headerLine))
          .forEach(line -> {
            try {
              writter.write(line);
              writter.newLine();
            } catch (IOException e) {
              LOGGER.error("Could not writte data : {}", line, e);
            }
          });

    } catch (IOException e) {
      LOGGER.error("Could not save account data", e);
    }
  }

  @Override
  public Account findByAccountId(String accountId) throws NoSuchAccountException {
    if (!accountMap.containsKey(accountId)) {
      LOGGER.warn("[Account n° {}] does not seem to exist", accountId);
      throw new NoSuchAccountException("No account found with id " + accountId);
    }
    return accountMap.get(accountId).toAccount();
  }

  @Override
  public Account save(Account account) {
    CsvAccountRecord accountRecord = CsvAccountRecord.fromAccount(account);
    accountMap.put(accountRecord.accountId(), accountRecord);
    return account;
  }

  protected Map<String, CsvAccountRecord> mapCsvToAccountRecord(Stream<String> lineStream) {
    List<String> lines = lineStream.collect(Collectors.toList());
    if (lines.size() == 0) {
      LOGGER.info("No account data found");
    }
    String headerLine;
    List<String> accountLines;
    String delimiter =
        Optional.ofNullable(config.getDelimiter()).orElse(CsvAccountRecord.getDefaultDelimiter());
    if (CsvAccountRecord.isHeaderLine(lines.get(0))) {
      headerLine = lines.get(0);
      accountLines = lines.subList(1, lines.size());
    } else {
      headerLine = CsvAccountRecord.getHeaderLine(delimiter);
      accountLines = lines;
    }
    return
        accountLines.stream().map(line -> {
              try {
                return CsvAccountRecord.fromCsv(line, delimiter, headerLine);
              } catch (IllegalStateException e) {
                LOGGER.error("Could not initialize account with line {}", line);
                return null;
              }
            }).filter(Objects::nonNull)
            .collect(Collectors.toMap(CsvAccountRecord::accountId, Function.identity()));
  }
}
