package dev.rgoussu.hexabank.cli.adapters.persistence;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.rgoussu.hexabank.cli.adapters.persistence.config.AccountCsvStoreConfig;
import dev.rgoussu.hexabank.cli.adapters.persistence.model.CsvAccountRecord;
import dev.rgoussu.hexabank.core.model.entities.Account;
import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.values.Money;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {
    MapAccountPersistenceAdapter.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableConfigurationProperties(AccountCsvStoreConfig.class)
public class MapAccountPersistenceAdapterTest {

  @Autowired
  private AccountCsvStoreConfig config;
  @Autowired
  private MapAccountPersistenceAdapter underTest;

  @BeforeEach
  public void cleanUp() throws IOException {
    underTest.readFromFile(config.getAccountCsvBackingFile());
  }

  @Test
  public void shouldLoadDataFromFile() {
    Account actual1 = underTest.findByAccountId("TEST_ACCOUNT_1");
    Account expected1 = Account.create("TEST_ACCOUNT_1", Money.get(0, Currency.EUR));
    Account actual2 = underTest.findByAccountId("TEST_ACCOUNT_2");
    Account expected2 = Account.create("TEST_ACCOUNT_2", Money.get(10, Currency.USD));
    assertAll(
        () -> assertEquals(expected1, actual1),
        () -> assertEquals(expected2, actual2)
    );
  }

  @Test
  public void shouldPersistAccountData() throws IOException {
    underTest.save(
        underTest.findByAccountId("TEST_ACCOUNT_1").deposit(Money.get(100, Currency.EUR)));
    File tmp = File.createTempFile("tmp", "account");
    underTest.saveToFile(tmp);
    Map<String, CsvAccountRecord> expected = Map.of("TEST_ACCOUNT_1",
        new CsvAccountRecord("TEST_ACCOUNT_1", Money.get(100, Currency.EUR))
        , "TEST_ACCOUNT_2", new CsvAccountRecord("TEST_ACCOUNT_2", Money.get(10, Currency.USD)));
    try (Stream<String> lines = Files.lines(tmp.toPath())) {
      Map<String, CsvAccountRecord> actual = underTest.mapCsvToAccountRecord(lines);
      assertEquals(expected, actual);
    }
    tmp.deleteOnExit();
  }
}
