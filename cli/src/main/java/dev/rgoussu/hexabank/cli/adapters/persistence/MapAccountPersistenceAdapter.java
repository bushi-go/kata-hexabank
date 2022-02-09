package dev.rgoussu.hexabank.cli.adapters.persistence;

import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import dev.rgoussu.hexabank.cli.adapters.persistence.model.CsvAccountRecord;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import dev.rgoussu.hexabank.core.operations.model.entities.Account;
import dev.rgoussu.hexabank.core.operations.ports.driven.AccountPersistencePort;
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
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simple map based, csv file backed store implementation for account data.
 */
@Component
@Slf4j
public class MapAccountPersistenceAdapter implements AccountPersistencePort, FileAccountStore {

  private final AccountCsvStoreConfig config;
  private final Map<String, CsvAccountRecord> accountMap;

  MapAccountPersistenceAdapter(AccountCsvStoreConfig config) {

    this.config = config;
    log.debug(config.getDelimiter());
    // There is very little likelihood that this class will ever be used in a concurrent context,
    // so a simple hashMap should be enough
    accountMap = new HashMap<>();

  }

  @Override
  public File getBackingFile() throws IOException {
    return config.getAccountCsvBackingFile();
  }

  /**
   * Initialization method that read the account data from the configured file into the map.
   *
   * @throws IllegalStateException if for any reason data could not be read.
   */
  public void readFromFile(File accountFile) throws IllegalStateException {
    try (Stream<String> stream = Files.lines(accountFile.toPath())) {
      accountMap.putAll(mapCsvToAccountRecord(stream));
      log.info("Initialized {} accounts data from csv store located at {}", accountMap.size(),
          accountFile.getPath());
    } catch (FileNotFoundException e) {
      log.warn(
          "Could not find account csv backing file");
      throw new IllegalStateException(
          "A problem occured during initialization of account data : ",
          e);
    } catch (IOException e) {
      log.error(
          "Could not read account backing file, "
              + "run will be aborted to prevent data loss");
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
              log.error("Could not writte data : {}", line, e);
            }
          });

    } catch (IOException e) {
      log.error("Could not save account data", e);
    }
  }

  @Override
  public Account findByAccountId(String accountId) throws NoSuchAccountException {
    if (!accountMap.containsKey(accountId)) {
      log.warn("[Account n° {}] does not seem to exist", accountId);
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
      log.info("No account data found");
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
    return accountLines.stream()
        .map(line -> {
          try {
            return CsvAccountRecord.fromCsv(line, delimiter, headerLine);
          } catch (IllegalStateException e) {
            log.error("Could not initialize account with line {}", line);
            return null;
          }
        }).filter(Objects::nonNull)
        .collect(Collectors.toMap(CsvAccountRecord::accountId, Function.identity()));
  }
}
