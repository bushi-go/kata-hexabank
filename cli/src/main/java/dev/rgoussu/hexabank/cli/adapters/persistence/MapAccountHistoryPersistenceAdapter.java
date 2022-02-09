package dev.rgoussu.hexabank.cli.adapters.persistence;

import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import dev.rgoussu.hexabank.cli.adapters.persistence.model.CsvAccountOperationRecord;
import dev.rgoussu.hexabank.cli.adapters.persistence.model.CsvAccountRecord;
import dev.rgoussu.hexabank.core.history.model.entities.AccountOperationsHistory;
import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.history.ports.driven.AccountHistoryPersistencePort;
import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.exceptions.NoSuchAccountException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MapAccountHistoryPersistenceAdapter implements AccountHistoryPersistencePort, FileAccountStore {

  private final AccountCsvStoreConfig config;
  private final Map<String, SortedSet<CsvAccountOperationRecord>> accountMap;

  public MapAccountHistoryPersistenceAdapter(AccountCsvStoreConfig config) {
    this.accountMap = new HashMap<>();
    this.config = config;
  }

  @Override
  public File getBackingFile() throws IOException {
    return config.getAccountHistoryCsvBackingFile();
  }

  @Override
  public void readFromFile(File accountFile) {
    try (Stream<String> stream = Files.lines(accountFile.toPath())) {
      accountMap.putAll(mapCsvToAccountOperationRecord(stream));
      log.info("Initialized {} accounts operation data from csv store located at {}",
          accountMap.size(),
          accountFile.getPath());
    } catch (FileNotFoundException e) {
      log.warn(
          "Could not find account csv backing file");
      throw new IllegalStateException(
          "A problem occured during initialization of account operations data : ",
          e);
    } catch (IOException e) {
      log.error(
          "Could not read account operation backing file, "
              + "run will be aborted to prevent data loss");
      throw new IllegalStateException(
          "A problem occured during initialization of account operation data : ",
          e);
    }
  }

  private Map<String, SortedSet<CsvAccountOperationRecord>> mapCsvToAccountOperationRecord(
      Stream<String> lineStream) {
    List<String> lines = lineStream.collect(Collectors.toList());
    if (lines.size() == 0) {
      log.info("No account operations data found");
      return Collections.emptyMap();
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
            return CsvAccountOperationRecord.fromCsv(line, delimiter, headerLine);
          } catch (IllegalStateException e) {
            log.error("Could not initialize account with line {}", line);
            return null;
          }
        }).filter(Objects::nonNull)
        .collect(
            Collectors.groupingBy(CsvAccountOperationRecord::getAccountId, Collectors.toCollection(
                TreeSet::new)));
  }

  @Override
  public void saveToFile(File accountFile) {
    try (BufferedWriter writter = new BufferedWriter(
        new FileWriter(accountFile, false))) {
      String delimiter =
          Optional.ofNullable(config.getDelimiter()).orElse(CsvAccountRecord.getDefaultDelimiter());
      String headerLine = CsvAccountOperationRecord.getHeaderLine(delimiter);
      writter.write(headerLine);
      writter.newLine();
      accountMap.values().stream().flatMap(Collection::stream)
          .map(record -> record.toCsv(delimiter, headerLine))
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
  public void recordOperationSummary(String accountId, AccountOperationSummary operationSummary) {
    SortedSet<CsvAccountOperationRecord> history = accountMap.getOrDefault(accountId, new TreeSet<>());
    history.add(CsvAccountOperationRecord.fromOperationSummary(accountId,operationSummary));
    accountMap.put(accountId, history);
  }

  @Override
  public SortedSet<AccountOperationSummary> findAccountHistory(String accountId)
      throws NoSuchAccountException {
      return accountMap.getOrDefault(accountId, new TreeSet<>()).stream()
              .map(CsvAccountOperationRecord::toSummary).collect(Collectors.toCollection(TreeSet::new));
  }
}
